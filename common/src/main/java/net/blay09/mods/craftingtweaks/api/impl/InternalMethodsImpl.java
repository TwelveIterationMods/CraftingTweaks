package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InternalMethodsImpl implements InternalMethods {

    private static final Map<Class<? extends Recipe<?>>, RecipeMatrixMapper<? extends Recipe<?>>> recipeMatrixMappers = new HashMap<>();

    @Override
    public void registerCraftingGridProvider(CraftingGridProvider provider) {
        CraftingTweaksProviderManager.registerProvider(provider);
    }

    @Override
    public void unregisterCraftingGridProvider(CraftingGridProvider provider) {
        CraftingTweaksProviderManager.unregisterProvider(provider);
    }

    @Override
    public GridTransferHandler<AbstractContainerMenu> defaultTransferHandler() {
        return new DefaultGridTransferHandler();
    }

    @Override
    public GridBalanceHandler<AbstractContainerMenu> defaultBalanceHandler() {
        return new DefaultGridBalanceHandler();
    }

    @Override
    public GridClearHandler<AbstractContainerMenu> defaultClearHandler() {
        return new DefaultGridClearHandler();
    }

    @Override
    public GridRotateHandler<AbstractContainerMenu> defaultRotateHandler() {
        return new DefaultGridRotateHandler();
    }

    @Override
    public GridRotateHandler<AbstractContainerMenu> defaultFourByFourRotateHandler() {
        return new DefaultFourByFourGridRotateHandler();
    }

    @Override
    public GridRotateHandler<AbstractContainerMenu> defaultRectangularRotateHandler() {
        return new DefaultRectangleGridRotateHandler();
    }

    @Override
    public GridRefillHandler<AbstractContainerMenu> defaultRefillHandler() {
        return new DefaultGridRefillHandler();
    }

    @Override
    public Optional<RecipeHolder<?>> getLastCraftedRecipe(Player player) {
        final var level = player.level();
        final var recipeManager = level.getRecipeManager();
        final var persistentData = Balm.getHooks().getPersistentData(player);
        final var lastCraftedRecipeId = ResourceLocation.tryParse(persistentData.getString("LastCraftedRecipe"));
        if (lastCraftedRecipeId != null) {
            return recipeManager.byKey(lastCraftedRecipeId);
        }

        return Optional.empty();
    }

    @Override
    public <T extends Recipe<? extends RecipeInput>> void setLastCraftedRecipe(Player player, RecipeHolder<T> recipe) {
        final var persistentData = Balm.getHooks().getPersistentData(player);
        persistentData.putString("LastCraftedRecipe", recipe.id().toString());
    }

    @Override
    public <C extends RecipeInput, T extends Recipe<C>> void registerRecipeMatrixMapper(Class<T> recipeClass, RecipeMatrixMapper<T> recipeMatrixMapper) {
        recipeMatrixMappers.put(recipeClass, recipeMatrixMapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Recipe<? extends RecipeInput>> RecipeMatrixMapper<T> getRecipeMatrixMapper(Class<T> recipeClass) {
        for (Class<? extends Recipe<?>> handlerClass : recipeMatrixMappers.keySet()) {
            if (handlerClass.isAssignableFrom(recipeClass)) {
                return (RecipeMatrixMapper<T>) recipeMatrixMappers.get(handlerClass);
            }
        }

        return (RecipeMatrixMapper<T>) recipeMatrixMappers.get(recipeClass);
    }
}
