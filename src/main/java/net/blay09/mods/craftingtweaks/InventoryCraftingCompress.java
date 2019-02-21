package net.blay09.mods.craftingtweaks;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nullable;

public class InventoryCraftingCompress extends InventoryCrafting implements IRecipeHolder {

    private IRecipe recipeUsed;

    public InventoryCraftingCompress(Container container, int size, ItemStack itemStack) {
        super(container, size, size);
        for(int i = 0; i < getSizeInventory(); i++) {
            setInventorySlotContents(i, itemStack.copy());
        }
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
