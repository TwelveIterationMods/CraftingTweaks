package net.blay09.mods.craftingtweaks.provider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class VanillaTweakProvider extends TweakProvider<ContainerWorkbench> {

    @Override
    public void clearGrid(EntityPlayer entityPlayer, ContainerWorkbench container) {
        for(int i = 0; i < container.craftMatrix.getSizeInventory(); i++) {
            ItemStack itemStack = container.craftMatrix.getStackInSlot(i);
            if(itemStack != null) {
                if (!entityPlayer.inventory.addItemStackToInventory(itemStack)) {
                    if (entityPlayer.dropPlayerItemWithRandomChoice(itemStack, false) == null) {
                        continue;
                    }
                }
            }
            container.craftMatrix.setInventorySlotContents(i, null);
            container.detectAndSendChanges();
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, ContainerWorkbench container) {
        IInventory matrixClone = new InventoryBasic("", false, container.craftMatrix.getSizeInventory());
        for(int i = 0; i < matrixClone.getSizeInventory(); i++) {
            matrixClone.setInventorySlotContents(i, container.craftMatrix.getStackInSlot(i));
        }
        for(int i = 0; i < container.craftMatrix.getSizeInventory(); i++) {
            if(i == 4) {
                continue;
            }
            container.craftMatrix.setInventorySlotContents(getNextSlotId(i), matrixClone.getStackInSlot(i));
        }
        container.detectAndSendChanges();
    }

    private int getNextSlotId(int i) {
        switch(i) {
            case 0: return 1;
            case 1: return 2;
            case 2: return 5;
            case 5: return 8;
            case 8: return 7;
            case 7: return 6;
            case 6: return 3;
            case 3: return 0;
        }
        return 0;
    }
}
