package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeMapper<T extends Recipe<?>> {
    /**
     * @deprecated Use {@link #mapToMatrixSlot(Recipe, int, int)} which supports a custom grid width instead.
     */
    @Deprecated
    default int mapToMatrixSlot(T recipe, int ingredientIndex) {
        return mapToMatrixSlot(recipe, 3, ingredientIndex);
    }

    default int mapToMatrixSlot(T recipe, int gridWidth, int ingredientIndex) {
        return mapToMatrixSlot(recipe, ingredientIndex);
    }

    List<Optional<Ingredient>> getIngredients(T recipe);
}
