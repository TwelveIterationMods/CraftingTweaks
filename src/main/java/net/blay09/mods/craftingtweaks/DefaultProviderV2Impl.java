package net.blay09.mods.craftingtweaks;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class DefaultProviderV2Impl implements DefaultProviderV2 {

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
    public <T extends Container> void clearGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, boolean phantomItems) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        int start = provider.getCraftingGridStart(id);
        int size = provider.getCraftingGridSize(id);
        for(int i = start; i < start + size; i++) {
            int slotIndex = container.inventorySlots.get(i).getSlotIndex();
            if(!phantomItems) {
                ItemStack itemStack = craftMatrix.getStackInSlot(slotIndex);
                if(!entityPlayer.inventory.addItemStackToInventory(itemStack)) {
                    continue;
                }
            }
            craftMatrix.setInventorySlotContents(slotIndex, null);
        }
        container.detectAndSendChanges();
    }

    @Override
    public <T extends Container> void balanceGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container) {
        ArrayListMultimap<String, ItemStack> itemMap = ArrayListMultimap.create();
        Multiset<String> itemCount = HashMultiset.create();
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        int start = provider.getCraftingGridStart(id);
        int size = provider.getCraftingGridSize(id);
        for(int i = start; i < start + size; i++) {
            int slotIndex = container.inventorySlots.get(i).getSlotIndex();
            ItemStack itemStack = craftMatrix.getStackInSlot(slotIndex);
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
    public <T extends Container> ItemStack putIntoGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, ItemStack itemStack, int index) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        ItemStack craftStack = craftMatrix.getStackInSlot(index);
        if(craftStack != null) {
            if(craftStack.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(craftStack, itemStack)) {
                int spaceLeft = Math.min(craftMatrix.getInventoryStackLimit(), craftStack.getMaxStackSize()) - craftStack.stackSize;
                if(spaceLeft > 0) {
                    ItemStack splitStack = itemStack.splitStack(Math.min(spaceLeft, itemStack.stackSize));
                    craftStack.stackSize += splitStack.stackSize;
                    if(itemStack.stackSize <= 0) {
                        return null;
                    }
                }
            }
        } else {
            ItemStack transferStack = itemStack.splitStack(Math.min(itemStack.stackSize, craftMatrix.getInventoryStackLimit()));
            craftMatrix.setInventorySlotContents(index, transferStack);
        }
        if(itemStack.stackSize <= 0) {
            return null;
        }
        return itemStack;
    }

    @Override
    public <T extends Container> boolean transferIntoGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, Slot sourceSlot) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        int start = provider.getCraftingGridStart(id);
        int size = provider.getCraftingGridSize(id);
        ItemStack itemStack = sourceSlot.getStack();
        if(itemStack == null) {
            return false;
        }
        int firstEmptySlot = -1;
        for(int i = start; i < start + size; i++) {
            int slotIndex = container.inventorySlots.get(i).getSlotIndex();
            ItemStack craftStack = craftMatrix.getStackInSlot(slotIndex);
            if(craftStack != null) {
                if(craftStack.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(craftStack, itemStack)) {
                    int spaceLeft = Math.min(craftMatrix.getInventoryStackLimit(), craftStack.getMaxStackSize()) - craftStack.stackSize;
                    if(spaceLeft > 0) {
                        ItemStack splitStack = itemStack.splitStack(Math.min(spaceLeft, itemStack.stackSize));
                        craftStack.stackSize += splitStack.stackSize;
                        if(itemStack.stackSize <= 0) {
                            return true;
                        }
                    }
                }
            } else if(firstEmptySlot == -1) {
                firstEmptySlot = slotIndex;
            }
        }
        if(itemStack.stackSize > 0 && firstEmptySlot != -1) {
            ItemStack transferStack = itemStack.splitStack(Math.min(itemStack.stackSize, craftMatrix.getInventoryStackLimit()));
            craftMatrix.setInventorySlotContents(firstEmptySlot, transferStack);
            return true;
        }
        return false;
    }

    @Override
    public boolean canTransferFrom(EntityPlayer entityPlayer, Container container, Slot slot) {
        return slot.inventory == entityPlayer.inventory;
    }

    @Override
    public <T extends Container> void rotateGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container) {
        rotateGrid(provider, id, entityPlayer, container, rotationHandler);
    }

    @Override
    public <T extends Container> void rotateGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, RotationHandler rotationHandler) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        int start = provider.getCraftingGridStart(id);
        int size = provider.getCraftingGridSize(id);
        IInventory matrixClone = new InventoryBasic("", false, size);
        for(int i = 0; i < size; i++) {
            int slotIndex = container.inventorySlots.get(start + i).getSlotIndex();
            matrixClone.setInventorySlotContents(i, craftMatrix.getStackInSlot(slotIndex));
        }
        for(int i = 0; i < size; i++) {
            if(rotationHandler.ignoreSlotId(i)) {
                continue;
            }
            int slotIndex = container.inventorySlots.get(start + rotationHandler.rotateSlotId(i)).getSlotIndex();
            craftMatrix.setInventorySlotContents(slotIndex, matrixClone.getStackInSlot(i));
        }
        container.detectAndSendChanges();
    }

    @Override
    public RotationHandler getRotationHandler() {
        return rotationHandler;
    }
}
