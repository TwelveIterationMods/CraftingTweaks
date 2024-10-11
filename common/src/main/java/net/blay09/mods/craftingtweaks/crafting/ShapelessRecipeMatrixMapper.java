package net.blay09.mods.craftingtweaks.crafting;

import net.blay09.mods.craftingtweaks.api.RecipeMapper;
import net.blay09.mods.craftingtweaks.mixin.ShapelessRecipeAccessor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;
import java.util.Optional;

public class ShapelessRecipeMatrixMapper implements RecipeMapper<ShapelessRecipe> {
    @Override
    public int mapToMatrixSlot(ShapelessRecipe recipe, int ingredientIndex) {
        return ingredientIndex;
    }

    @Override
    public List<Optional<Ingredient>> getIngredients(ShapelessRecipe recipe) {
        return recipe instanceof ShapelessRecipeAccessor accessor ? accessor.getIngredients() : List.of();
    }
}