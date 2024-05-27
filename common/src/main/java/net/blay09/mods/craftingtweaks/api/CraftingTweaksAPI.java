package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class CraftingTweaksAPI {

    private static final InternalMethods internalMethods = loadInternalMethods();

    private static InternalMethods loadInternalMethods() {
        try {
            return (InternalMethods) Class.forName("net.blay09.mods.craftingtweaks.api.impl.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException("Failed to load Crafting Tweaks API", e);
        }
    }

    public static void registerCraftingGridProvider(CraftingGridProvider provider) {
        internalMethods.registerCraftingGridProvider(provider);
    }

    public static void unregisterCraftingGridProvider(CraftingGridProvider provider) {
        internalMethods.unregisterCraftingGridProvider(provider);
    }

    public static <C extends Container, T extends Recipe<C>> void registerRecipeMatrixMapper(Class<T> recipeClass, RecipeMatrixMapper<T> recipeMatrixMapper) {
        internalMethods.registerRecipeMatrixMapper(recipeClass, recipeMatrixMapper);
    }

    public static <C extends Container, T extends Recipe<C>> RecipeMatrixMapper<T> getRecipeMatrixMapper(Class<T> recipe) {
        return internalMethods.getRecipeMatrixMapper(recipe);
    }

    public static Optional<RecipeHolder<?>> getLastCraftedRecipe(Player player) {
        return internalMethods.getLastCraftedRecipe(player);
    }

    public static <T extends Recipe<? extends Container>> void setLastCraftedRecipe(Player player, RecipeHolder<T> recipe) {
        internalMethods.setLastCraftedRecipe(player, recipe);
    }
}
