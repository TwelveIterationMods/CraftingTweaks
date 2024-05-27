package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.item.crafting.Recipe;

public interface RecipeMatrixMapper<T extends Recipe<?>> {
    int mapToMatrixSlot(T recipe, int ingredientIndex);
}
