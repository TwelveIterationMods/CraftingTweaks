package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InternalMethodsImpl implements InternalMethods {

    private static final Map<Class<? extends Recipe<?>>, RecipeMapper<? extends Recipe<?>>> recipeMatrixMappers = new HashMap<>();

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
    public Optional<RecipeHolder<?>> getLastCraftedRecipe(ServerPlayer player) {
        final var level = player.level();
        final var recipeManager = level.getServer().getRecipeManager();
        final var persistentData = Balm.hooks().getPersistentData(player);
        return persistentData.getString("LastCraftedRecipe")
                .flatMap(it -> Optional.ofNullable(Identifier.tryParse(it)))
                .map(it -> ResourceKey.create(Registries.RECIPE, it))
                .flatMap(recipeManager::byKey);
    }

    @Override
    public <T extends Recipe<? extends RecipeInput>> void setLastCraftedRecipe(ServerPlayer player, RecipeHolder<T> recipe) {
        final var persistentData = Balm.hooks().getPersistentData(player);
        persistentData.putString("LastCraftedRecipe", recipe.id().identifier().toString());
    }

    @Override
    public <C extends RecipeInput, T extends Recipe<C>> void registerRecipeMapper(Class<T> recipeClass, RecipeMapper<T> recipeMapper) {
        recipeMatrixMappers.put(recipeClass, recipeMapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Recipe<? extends RecipeInput>> RecipeMapper<T> getRecipeMapper(Class<T> recipeClass) {
        for (Class<? extends Recipe<?>> handlerClass : recipeMatrixMappers.keySet()) {
            if (handlerClass.isAssignableFrom(recipeClass)) {
                return (RecipeMapper<T>) recipeMatrixMappers.get(handlerClass);
            }
        }

        return (RecipeMapper<T>) recipeMatrixMappers.get(recipeClass);
    }
}
