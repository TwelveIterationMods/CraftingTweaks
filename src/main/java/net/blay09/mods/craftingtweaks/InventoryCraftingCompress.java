package net.blay09.mods.craftingtweaks;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryCraftingCompress extends InventoryCrafting {

    public InventoryCraftingCompress(Container container, int size, ItemStack itemStack) {
        super(container, size, size);
        for(int i = 0; i < getSizeInventory(); i++) {
            setInventorySlotContents(i, itemStack.copy());
        }
    }

}
