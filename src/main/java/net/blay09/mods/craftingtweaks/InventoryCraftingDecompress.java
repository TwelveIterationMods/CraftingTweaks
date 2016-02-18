package net.blay09.mods.craftingtweaks;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryCraftingDecompress extends InventoryCrafting {

    public InventoryCraftingDecompress(Container container, ItemStack itemStack) {
        super(container, 3, 3);
        setInventorySlotContents(0, itemStack.copy());
    }

}
