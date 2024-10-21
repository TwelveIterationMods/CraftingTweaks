package net.blay09.mods.craftingtweaks.crafting;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CraftingOperation {

    public record IngredientTokenKey(int providerIndex, List<Holder<Item>> items) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IngredientTokenKey that = (IngredientTokenKey) o;
            return providerIndex == that.providerIndex && Objects.equals(items, that.items);
        }

        @Override
        public int hashCode() {
            return Objects.hash(providerIndex, items);
        }
    }

    private final CraftingContext context;
    private final Recipe<?> recipe;

    private final Multimap<IngredientTokenKey, IngredientToken> tokensByIngredient = ArrayListMultimap.create();
    private final List<IngredientToken> ingredientTokens = new ArrayList<>();
    private final List<Ingredient> missingIngredients = new ArrayList<>();

    private NonNullList<ItemStack> lockedInputs;
    private int missingIngredientsMask;

    public CraftingOperation(final CraftingContext context, RecipeHolder<Recipe<?>> recipe) {
        this.context = context;
        this.recipe = recipe.value();
    }

    public CraftingOperation withLockedInputs(@Nullable NonNullList<ItemStack> lockedInputs) {
        this.lockedInputs = lockedInputs;
        return this;
    }

    public CraftingOperation prepare() {
        tokensByIngredient.clear();
        ingredientTokens.clear();
        missingIngredients.clear();
        missingIngredientsMask = 0;

        final var recipeMapper = CraftingTweaksAPI.getRecipeMapper(recipe.getClass());
        final List<Optional<Ingredient>> ingredients = recipeMapper.getIngredients(recipe);
        for (int i = 0; i < ingredients.size(); i++) {
            final var ingredient = ingredients.get(i).orElse(null);
            if (ingredient == null || ingredient.items().isEmpty()) {
                ingredientTokens.add(IngredientToken.EMPTY);
                continue;
            }

            final var lockedInput = lockedInputs != null ? lockedInputs.get(i) : ItemStack.EMPTY;
            final var ingredientToken = accountForIngredient(ingredient, lockedInput);
            if (ingredientToken != null) {
                if (ingredient.items().size() > 1) {
                    if (lockedInputs == null) {
                        lockedInputs = NonNullList.withSize(ingredients.size(), ItemStack.EMPTY);
                    }
                    lockedInputs.set(i, ingredientToken.peek());
                }
            } else {
                missingIngredients.add(ingredient);
                missingIngredientsMask |= 1 << i;
            }
        }

        return this;
    }

    @Nullable
    private IngredientToken accountForIngredient(Ingredient ingredient, ItemStack lockedInput) {
        final var ingredientProviders = context.getIngredientProviders();
        final var cachedProviderIndex = context.getCachedIngredientProviderIndexFor(ingredient);
        if (cachedProviderIndex != -1) {
            final var ingredientProvider = ingredientProviders.get(cachedProviderIndex);
            final var ingredientToken = accountForIngredient(cachedProviderIndex, ingredientProvider, ingredient, lockedInput, true);
            if (ingredientToken != null) {
                return ingredientToken;
            }
        }

        for (int j = 0; j < ingredientProviders.size(); j++) {
            final var ingredientProvider = ingredientProviders.get(j);
            IngredientToken ingredientToken = accountForIngredient(j, ingredientProvider, ingredient, lockedInput, false);
            if (ingredientToken != null) {
                return ingredientToken;
            }
        }

        return null;
    }

    @Nullable
    private IngredientToken accountForIngredient(int ingredientProviderIndex, IngredientProvider ingredientProvider, Ingredient ingredient, ItemStack lockedInput, boolean useCache) {
        final var ingredientTokenKey = new IngredientTokenKey(ingredientProviderIndex, ingredient.items());
        final var scopedIngredientTokens = tokensByIngredient.get(ingredientTokenKey);
        final var cacheHint = useCache ? context.getCacheHintFor(ingredientTokenKey) : IngredientCacheHint.NONE;
        final var ingredientToken = findIngredient(ingredientProvider, ingredient, lockedInput, scopedIngredientTokens, cacheHint);
        if (ingredientToken != null) {
            tokensByIngredient.put(ingredientTokenKey, ingredientToken);
            context.cache(ingredientTokenKey, ingredientProviderIndex, ingredientProvider.getCacheHint(ingredientToken));
            ingredientTokens.add(ingredientToken);
            return ingredientToken;
        }
        return null;
    }

    @Nullable
    private IngredientToken findIngredient(IngredientProvider ingredientProvider, Ingredient ingredient, ItemStack lockedInput, Collection<IngredientToken> ingredientTokens, IngredientCacheHint cacheHint) {
        IngredientToken ingredientToken;
        if (lockedInput.isEmpty()) {
            ingredientToken = ingredientProvider.findIngredient(ingredient, ingredientTokens, cacheHint);
        } else {
            ingredientToken = ingredientProvider.findIngredient(lockedInput, ingredientTokens, cacheHint);
        }
        return ingredientToken;
    }

    public boolean canCraft() {
        return missingIngredients.isEmpty();
    }

    public NonNullList<ItemStack> getLockedInputs() {
        return lockedInputs;
    }

    public List<IngredientToken> getIngredientTokens() {
        return ingredientTokens;
    }

    public List<Ingredient> getMissingIngredients() {
        return missingIngredients;
    }

    public int getMissingIngredientsMask() {
        return missingIngredientsMask;
    }
}
