package net.blay09.mods.craftingtweaks.crafting;

import net.blay09.mods.craftingtweaks.api.RecipeMatrixMapper;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class ShapelessRecipeMatrixMapper implements RecipeMatrixMapper<ShapelessRecipe> {
    @Override
    public int mapToMatrixSlot(ShapelessRecipe recipe, int ingredientIndex) {
        return ingredientIndex;
    }
}