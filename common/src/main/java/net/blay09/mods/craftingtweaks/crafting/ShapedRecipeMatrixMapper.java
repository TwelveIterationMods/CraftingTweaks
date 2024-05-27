package net.blay09.mods.craftingtweaks.crafting;

import net.blay09.mods.craftingtweaks.api.RecipeMatrixMapper;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class ShapedRecipeMatrixMapper implements RecipeMatrixMapper<ShapedRecipe> {
    @Override
    public int mapToMatrixSlot(ShapedRecipe recipe, int ingredientIndex) {
        final int recipeWidth = recipe.getWidth();
        final int origX = ingredientIndex % recipeWidth;
        final int origY = ingredientIndex / recipeWidth;

        // Offset to center the recipe if its width is 1
        final int offsetX = recipeWidth == 1 ? 1 : 0;

        return origY * 3 + origX + offsetX;
    }
}

