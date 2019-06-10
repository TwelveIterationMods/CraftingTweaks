package net.blay09.mods.craftingtweaks;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nullable;

public class InventoryCraftingDecompress extends CraftingInventory implements IRecipeHolder {

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
