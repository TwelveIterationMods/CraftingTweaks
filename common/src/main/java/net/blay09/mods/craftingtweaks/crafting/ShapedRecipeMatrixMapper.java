package net.blay09.mods.craftingtweaks.crafting;

import net.blay09.mods.craftingtweaks.api.RecipeMatrixMapper;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class ShapedRecipeMatrixMapper implements RecipeMatrixMapper<ShapedRecipe> {
    @Override
    public int mapToMatrixSlot(ShapedRecipe recipe, int gridWidth, int ingredientIndex) {
        final int recipeWidth = recipe.getWidth();
        final int origX = ingredientIndex % recipeWidth;
        final int origY = ingredientIndex / recipeWidth;

        // Offset to center the recipe if its width is 1 in a 3-wide grid
        final int offsetX = gridWidth == 3 && recipeWidth == 1 ? 1 : 0;

        return origY * gridWidth + origX + offsetX;
    }
}

