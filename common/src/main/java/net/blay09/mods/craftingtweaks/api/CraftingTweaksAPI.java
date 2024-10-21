package net.blay09.mods.craftingtweaks.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

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

    public static <C extends RecipeInput, T extends Recipe<C>> void registerRecipeMapper(Class<T> recipeClass, RecipeMapper<T> recipeMapper) {
        internalMethods.registerRecipeMapper(recipeClass, recipeMapper);
    }

    public static <C extends RecipeInput, T extends Recipe<C>> RecipeMapper<T> getRecipeMapper(Class<T> recipe) {
        return internalMethods.getRecipeMapper(recipe);
    }

    public static Optional<RecipeHolder<?>> getLastCraftedRecipe(ServerPlayer player) {
        return internalMethods.getLastCraftedRecipe(player);
    }

    public static <T extends Recipe<? extends RecipeInput>> void setLastCraftedRecipe(ServerPlayer player, RecipeHolder<T> recipe) {
        internalMethods.setLastCraftedRecipe(player, recipe);
    }
}
