package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.GridRefillHandler;
import net.blay09.mods.craftingtweaks.crafting.CraftingContext;
import net.blay09.mods.craftingtweaks.crafting.ContainerIngredientProvider;
import net.blay09.mods.craftingtweaks.crafting.IngredientToken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultGridRefillHandler implements GridRefillHandler<AbstractContainerMenu> {
    @Override
    public void refillRecipe(CraftingGrid grid, Player player, AbstractContainerMenu menu, RecipeHolder<?> recipeHolder, boolean stack) {
        final var craftMatrix = grid.getCraftingMatrix(player, menu);
        if (craftMatrix == null) {
            return;
        }

        final var recipe = recipeHolder.value();
        final var context = new CraftingContext(List.of(new ContainerIngredientProvider(player.getInventory())));
        final var operation = context.createOperation((RecipeHolder<net.minecraft.world.item.crafting.Recipe<?>>) recipeHolder).prepare();
        if (!operation.canCraft()) {
            return;
        }

        int targetGridWidth;
        int matrixSize = craftMatrix.getContainerSize();
        if (matrixSize == 4) {
            targetGridWidth = 2; // Inventory (2x2)
        } else if (matrixSize == 9) {
            targetGridWidth = 3; // Crafting Table (3x3)
        } else {
            CraftingTweaks.logger.warn("Unsupported crafting matrix size: {}. Skipping refill.", matrixSize);
            return;
        }

        var matrixMapper = new net.blay09.mods.craftingtweaks.api.RecipeMapper<>() {
            @Override
            public int mapToMatrixSlot(net.minecraft.world.item.crafting.Recipe<?> recipe, int ingredientIndex) {
                if (recipe instanceof net.minecraft.world.item.crafting.ShapedRecipe shaped) {
                    int recipeWidth = shaped.getWidth();
                    int origX = ingredientIndex % recipeWidth;
                    int origY = ingredientIndex / recipeWidth;
                    int offsetX = (targetGridWidth - recipeWidth) / 2;
                    return origY * targetGridWidth + origX + offsetX;
                } else if (recipe instanceof net.minecraft.world.item.crafting.ShapelessRecipe) {
                    return ingredientIndex < (targetGridWidth * targetGridWidth) ? ingredientIndex : -1;
                }
                return -1;
            }

            @Override
            public List<Optional<Ingredient>> getIngredients(Recipe<?> recipe) {
                if (recipe instanceof ShapedRecipe shaped) {
                    return shaped.getIngredients();
                } else if (recipe instanceof ShapelessRecipe shapeless) {
                    List<Ingredient> raw = (List<Ingredient>) shapeless.getType();
                    List<Optional<Ingredient>> result = new ArrayList<>(raw.size());
                    for (Ingredient ing : raw) {
                        result.add(Optional.of(ing));
                    }
                    return result;
                }
                return List.of();
            }
        };

        final var ingredientTokens = operation.getIngredientTokens();
        final var matrixDiff = new java.util.HashMap<Integer, IngredientToken>();

        for (int i = 0; i < ingredientTokens.size(); i++) {
            final var ingredientToken = ingredientTokens.get(i);
            var matrixSlot = matrixMapper.mapToMatrixSlot(recipe, i);
            if (matrixSlot == -1) {
                continue;
            }

            final var itemStack = ingredientToken.peek();
            if (itemStack.isEmpty()) {
                continue;
            }

            final var slotStack = craftMatrix.getItem(matrixSlot);

            if (slotStack.isEmpty()) {
                matrixDiff.put(matrixSlot, ingredientToken);
            } else if (ItemStack.isSameItemSameComponents(slotStack, itemStack) &&
                    slotStack.getCount() < slotStack.getMaxStackSize()) {
                matrixDiff.put(matrixSlot, ingredientToken);
            }
        }

        matrixDiff.forEach((slot, ingredientToken) -> {
            ItemStack oneItem = ingredientToken.peek();
            if (oneItem.isEmpty()) {
                return;
            }
            oneItem = oneItem.copyWithCount(1);

            final var slotStack = craftMatrix.getItem(slot);

            if (slotStack.isEmpty()) {
                craftMatrix.setItem(slot, oneItem);
                ingredientToken.consume();
            } else if (ItemStack.isSameItemSameComponents(slotStack, oneItem)) {
                int newCount = Math.min(slotStack.getCount() + 1, slotStack.getMaxStackSize());
                craftMatrix.setItem(slot, slotStack.copyWithCount(newCount));
                ingredientToken.consume();
            }
        });

        menu.broadcastChanges();
    }
}
