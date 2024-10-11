package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.GridRefillHandler;
import net.blay09.mods.craftingtweaks.crafting.CraftingContext;
import net.blay09.mods.craftingtweaks.crafting.ContainerIngredientProvider;
import net.blay09.mods.craftingtweaks.crafting.IngredientToken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.HashMap;
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

        outer:
        do {
            final var ingredientTokens = operation.getIngredientTokens();
            final var matrixMapper = CraftingTweaksAPI.getRecipeMapper(recipe.getClass());

            final var matrixDiff = new HashMap<Integer, IngredientToken>();
            for (int i = 0; i < ingredientTokens.size(); i++) {
                final var ingredientToken = ingredientTokens.get(i);
                var matrixSlot = matrixMapper.mapToMatrixSlot(recipe, i);
                if (matrixSlot != -1) {
                    final var itemStack = ingredientToken.peek();
                    final var slotStack = craftMatrix.getItem(matrixSlot);
                    if (!slotStack.isEmpty()) {
                        if (slotStack.getCount() >= slotStack.getMaxStackSize()) {
                            break outer;
                        } else if (!slotStack.isStackable()) {
                            break outer;
                        } else if (!ItemStack.isSameItemSameComponents(slotStack, itemStack)) {
                            break outer;
                        }
                    }

                    matrixDiff.put(matrixSlot, ingredientToken);
                }
            }
            matrixDiff.forEach((slot, ingredientToken) -> {
                final var slotStack = craftMatrix.getItem(slot);
                final var itemStack = ingredientToken.consume();
                craftMatrix.setItem(slot, itemStack.copyWithCount(itemStack.getCount() + slotStack.getCount()));
            });
            if (!stack) {
                break;
            }
        } while (operation.prepare().canCraft());

        menu.broadcastChanges();
    }
}
