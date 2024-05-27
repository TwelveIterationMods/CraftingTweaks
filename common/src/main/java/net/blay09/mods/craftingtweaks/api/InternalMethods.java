package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Optional;

public interface InternalMethods {
    void registerCraftingGridProvider(CraftingGridProvider provider);
    void unregisterCraftingGridProvider(CraftingGridProvider provider);

    GridTransferHandler<AbstractContainerMenu> defaultTransferHandler();

    GridBalanceHandler<AbstractContainerMenu> defaultBalanceHandler();

    GridClearHandler<AbstractContainerMenu> defaultClearHandler();

    GridRotateHandler<AbstractContainerMenu> defaultRotateHandler();

    GridRotateHandler<AbstractContainerMenu> defaultFourByFourRotateHandler();

    GridRotateHandler<AbstractContainerMenu> defaultRectangularRotateHandler();

    GridRefillHandler<AbstractContainerMenu> defaultRefillHandler();

    Optional<RecipeHolder<?>> getLastCraftedRecipe(Player player);

    <T extends Recipe<? extends Container>> void setLastCraftedRecipe(Player player, RecipeHolder<T> recipe);

    <C extends Container, T extends Recipe<C>> void registerRecipeMatrixMapper(Class<T> recipeClass, RecipeMatrixMapper<T> recipeMatrixMapper);

    <T extends Recipe<? extends Container>> RecipeMatrixMapper<T> getRecipeMatrixMapper(Class<T> recipeClass);
}
