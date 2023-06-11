package net.blay09.mods.craftingtweaks;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public class InventoryCraftingCompress extends TransientCraftingContainer implements RecipeHolder {

    private Recipe<?> recipeUsed;

    public InventoryCraftingCompress(AbstractContainerMenu menu, int size, ItemStack itemStack) {
        super(menu, size, size);
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, itemStack.copy());
        }
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        this.recipeUsed = recipe;
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return recipeUsed;
    }
}
