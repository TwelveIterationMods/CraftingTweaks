package net.blay09.mods.craftingtweaks.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

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

    Optional<RecipeHolder<?>> getLastCraftedRecipe(ServerPlayer player);

    <T extends Recipe<? extends RecipeInput>> void setLastCraftedRecipe(ServerPlayer player, RecipeHolder<T> recipe);

    <C extends RecipeInput, T extends Recipe<C>> void registerRecipeMapper(Class<T> recipeClass, RecipeMapper<T> recipeMapper);

    <T extends Recipe<? extends RecipeInput>> RecipeMapper<T> getRecipeMapper(Class<T> recipeClass);
}
