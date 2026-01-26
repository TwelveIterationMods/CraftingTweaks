package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.item.crafting.Recipe;

public interface RecipeMatrixMapper<T extends Recipe<?>> {
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
}
