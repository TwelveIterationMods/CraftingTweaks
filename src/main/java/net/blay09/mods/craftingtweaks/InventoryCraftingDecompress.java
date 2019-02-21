package net.blay09.mods.craftingtweaks;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nullable;

public class InventoryCraftingDecompress extends InventoryCrafting implements IRecipeHolder {

    private IRecipe recipeUsed;

    public InventoryCraftingDecompress(Container container, ItemStack itemStack) {
        super(container, 3, 3);
        setInventorySlotContents(0, itemStack.copy());
    }

    @Override
    public void setRecipeUsed(@Nullable IRecipe recipe) {
        this.recipeUsed = recipe;
    }

    @Nullable
    @Override
    public IRecipe getRecipeUsed() {
        return recipeUsed;
    }
}
