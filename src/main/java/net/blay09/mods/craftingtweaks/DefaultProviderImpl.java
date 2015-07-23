package net.blay09.mods.craftingtweaks;

import com.google.common.collect.*;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import java.util.List;

public class DefaultProviderImpl implements DefaultProvider {

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix) {
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

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix) {
        ArrayListMultimap<String, ItemStack> itemMap = ArrayListMultimap.create();
        Multiset<String> itemCount = HashMultiset.create();
        for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack itemStack = craftMatrix.getStackInSlot(i);
            if(itemStack != null && itemStack.getMaxStackSize() > 1) {
                String key = itemStack.getUnlocalizedName() + "@" + itemStack.getItemDamage();
                itemMap.put(key, itemStack);
                itemCount.add(key, itemStack.stackSize);
            }
        }
        for(String key : itemMap.keySet()) {
            List<ItemStack> balanceList = itemMap.get(key);
            int totalCount = itemCount.count(key);
            int countPerStack = totalCount / balanceList.size();
            int restCount = totalCount % balanceList.size();
            for(ItemStack itemStack : balanceList) {
                itemStack.stackSize = countPerStack;
            }
            int idx = 0;
            while(restCount > 0) {
                ItemStack itemStack = balanceList.get(idx);
                if(itemStack.stackSize < itemStack.getMaxStackSize()) {
                    itemStack.stackSize++;
                    restCount--;
                }
                idx++;
                if(idx >= balanceList.size()) {
                    idx = 0;
                }
            }
        }
        container.detectAndSendChanges();
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix) {
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

    private int rotateSlotId(int i) {
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
