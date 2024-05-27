package net.blay09.mods.craftingtweaks.crafting;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingContext {

    private final Map<IntList, Integer> cachedProviderIndexByIngredient = new HashMap<>();
    private final Map<CraftingOperation.IngredientTokenKey, IngredientCacheHint> cacheHintsByIngredient = new HashMap<>();

    private final List<IngredientProvider> ingredientProviders;

    public CraftingContext(List<IngredientProvider> ingredientProviders) {
        this.ingredientProviders = ingredientProviders;
    }

    public CraftingOperation createOperation(RecipeHolder<Recipe<?>> recipe) {
        return new CraftingOperation(this, recipe);
    }

    public List<IngredientProvider> getIngredientProviders() {
        return ingredientProviders;
    }

    public int getCachedIngredientProviderIndexFor(Ingredient ingredient) {
        return cachedProviderIndexByIngredient.getOrDefault(ingredient.getStackingIds(), -1);
    }

    public IngredientCacheHint getCacheHintFor(CraftingOperation.IngredientTokenKey ingredientTokenKey) {
        return cacheHintsByIngredient.getOrDefault(ingredientTokenKey, IngredientCacheHint.NONE);
    }

    public void cache(CraftingOperation.IngredientTokenKey ingredientTokenKey, int itemProviderIndex, IngredientCacheHint cacheHint) {
        cacheHintsByIngredient.put(ingredientTokenKey, cacheHint);
        cachedProviderIndexByIngredient.put(ingredientTokenKey.stackingIds(), itemProviderIndex);
    }
}
