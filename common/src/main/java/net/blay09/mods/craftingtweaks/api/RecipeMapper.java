package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeMapper<T extends Recipe<?>> {
    int mapToMatrixSlot(T recipe, int ingredientIndex);
    List<Optional<Ingredient>> getIngredients(T recipe);
}
