package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.GridRefillHandler;
import net.blay09.mods.craftingtweaks.crafting.CraftingContext;
import net.blay09.mods.craftingtweaks.crafting.ContainerIngredientProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class DefaultGridRefillHandler implements GridRefillHandler<AbstractContainerMenu> {
    @Override
    @SuppressWarnings("unchecked")
    public void refillRecipe(CraftingGrid grid, Player player, AbstractContainerMenu menu, RecipeHolder<?> recipeHolder, boolean stack) {
        grid.clearHandler().clearGrid(grid, player, menu, true);

        final var craftMatrix = grid.getCraftingMatrix(player, menu);
        if (craftMatrix == null) {
            return;
        }

        final var recipe = recipeHolder.value();
        final var context = new CraftingContext(List.of(new ContainerIngredientProvider(player.getInventory())));
        final var operation = context.createOperation((RecipeHolder<Recipe<?>>) recipeHolder).prepare();
        if (!operation.canCraft()) {
            return;
        }

        final var ingredientTokens = operation.getIngredientTokens();
        final var matrixMapper = CraftingTweaksAPI.getRecipeMatrixMapper(recipe.getClass());
        for (int i = 0; i < ingredientTokens.size(); i++) {
            final var ingredientToken = ingredientTokens.get(i);
            var matrixSlot = matrixMapper.mapToMatrixSlot(recipe, i);
            if (matrixSlot != -1) {
                final var itemStack = ingredientToken.consume();
                craftMatrix.setItem(matrixSlot, itemStack);
            }
        }

        menu.broadcastChanges();
    }
}
