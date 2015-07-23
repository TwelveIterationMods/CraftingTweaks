package net.blay09.mods.craftingtweaks.provider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public abstract class DefaultProvider implements TweakProvider {

    protected void clearGridDefault(EntityPlayer entityPlayer, Container container, IInventory craftMatrix) {
        for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack itemStack = craftMatrix.getStackInSlot(i);
            if(itemStack != null) {
                if (entityPlayer.inventory.addItemStackToInventory(itemStack)) {
                    craftMatrix.setInventorySlotContents(i, null);
                }
            }
            container.detectAndSendChanges();
        }
    }

    protected void balanceGridDefault(EntityPlayer entityPlayer, Container container, IInventory craftMatrix) {
        
    }

    protected void rotateGridDefault(EntityPlayer entityPlayer, Container container, IInventory craftMatrix) {
        IInventory matrixClone = new InventoryBasic("", false, craftMatrix.getSizeInventory());
        for(int i = 0; i < matrixClone.getSizeInventory(); i++) {
            matrixClone.setInventorySlotContents(i, craftMatrix.getStackInSlot(i));
        }
        for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            if(i == 4) {
                continue;
            }
            craftMatrix.setInventorySlotContents(rotateSlotId(i), matrixClone.getStackInSlot(i));
        }
        container.detectAndSendChanges();
    }

    protected int rotateSlotId(int i) {
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
