package net.blay09.mods.craftingtweaks.crafting;

import net.blay09.mods.craftingtweaks.api.RecipeMapper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;
import java.util.Optional;

public class ShapedRecipeMatrixMapper implements RecipeMapper<ShapedRecipe> {
    @Override
    public int mapToMatrixSlot(ShapedRecipe recipe, int gridWidth, int ingredientIndex) {
        final int recipeWidth = recipe.getWidth();
        final int origX = ingredientIndex % recipeWidth;
        final int origY = ingredientIndex / recipeWidth;

        // Offset to center the recipe if its width is 1 in a 3-wide grid
        final int offsetX = gridWidth == 3 && recipeWidth == 1 ? 1 : 0;

        return origY * gridWidth + origX + offsetX;
    }

    @Override
    public List<Optional<Ingredient>> getIngredients(ShapedRecipe recipe) {
        return recipe.getIngredients();
    }
}

