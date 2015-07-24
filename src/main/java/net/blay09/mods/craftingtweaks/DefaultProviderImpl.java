package net.blay09.mods.craftingtweaks;

import com.google.common.collect.*;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import java.util.List;

public class DefaultProviderImpl implements DefaultProvider {

    private static final RotationHandler rotationHandler = new RotationHandler() {
        @Override
        public boolean ignoreSlotId(int slotId) {
            return slotId == 4;
        }

        @Override
        public int rotateSlotId(int slotId) {
            switch(slotId) {
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
    };

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix) {
        clearGrid(entityPlayer, container, craftMatrix, 0, craftMatrix.getSizeInventory());
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size) {
        for(int i = start; i < size; i++) {
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
        balanceGrid(entityPlayer, container, craftMatrix, 0, craftMatrix.getSizeInventory());
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size) {
        ArrayListMultimap<String, ItemStack> itemMap = ArrayListMultimap.create();
        Multiset<String> itemCount = HashMultiset.create();
        for(int i = start; i < size; i++) {
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
        rotateGrid(entityPlayer, container, craftMatrix, 0, craftMatrix.getSizeInventory(), rotationHandler);
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size, RotationHandler rotationHandler) {
        IInventory matrixClone = new InventoryBasic("", false, craftMatrix.getSizeInventory());
        for(int i = start; i < size; i++) {
            matrixClone.setInventorySlotContents(i, craftMatrix.getStackInSlot(i));
        }
        for(int i = start; i < size; i++) {
            if(rotationHandler.ignoreSlotId(i)) {
                continue;
            }
            craftMatrix.setInventorySlotContents(rotationHandler.rotateSlotId(i), matrixClone.getStackInSlot(i));
        }
        container.detectAndSendChanges();
    }

    @Override
    public RotationHandler getRotationHandler() {
        return rotationHandler;
    }
}
