package net.blay09.mods.craftingtweaks;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.Objects;

public class DefaultProviderV2Impl implements DefaultProviderV2 {

    private static final RotationHandler rotationHandler = new RotationHandler() {
        @Override
        public boolean ignoreSlotId(int slotId) {
            return slotId == 4;
        }

        @Override
        public int rotateSlotId(int slotId, boolean counterClockwise) {
            if (!counterClockwise) {
                switch (slotId) {
                    case 0:
                        return 1;
                    case 1:
                        return 2;
                    case 2:
                        return 5;
                    case 5:
                        return 8;
                    case 8:
                        return 7;
                    case 7:
                        return 6;
                    case 6:
                        return 3;
                    case 3:
                        return 0;
                }
            } else {
                switch (slotId) {
                    case 0:
                        return 3;
                    case 1:
                        return 0;
                    case 2:
                        return 1;
                    case 3:
                        return 6;
                    case 5:
                        return 2;
                    case 6:
                        return 7;
                    case 7:
                        return 8;
                    case 8:
                        return 5;
                }
            }
            return 0;
        }
    };

    @Override
    public <T extends Container> void clearGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, boolean phantomItems, boolean forced) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        if (craftMatrix == null) {
            return;
        }

        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for (int i = start; i < start + size; i++) {
            int slotIndex = container.inventorySlots.get(i).getSlotIndex();
            if (phantomItems) {
                craftMatrix.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
            } else {
                ItemStack itemStack = craftMatrix.getStackInSlot(slotIndex);
                if (!itemStack.isEmpty()) {
                    ItemStack returnStack = itemStack.copy();
                    entityPlayer.inventory.addItemStackToInventory(returnStack);
                    craftMatrix.setInventorySlotContents(slotIndex, returnStack.getCount() == 0 ? ItemStack.EMPTY : returnStack);
                    if (returnStack.getCount() > 0 && forced) {
                        entityPlayer.dropItem(returnStack, false);
                        craftMatrix.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
                    }
                }
            }
        }
        container.detectAndSendChanges();
    }

    @Override
    public <T extends Container> void balanceGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        if (craftMatrix == null) {
            return;
        }

        ArrayListMultimap<String, ItemStack> itemMap = ArrayListMultimap.create();
        Multiset<String> itemCount = HashMultiset.create();
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for (int i = start; i < start + size; i++) {
            int slotIndex = container.inventorySlots.get(i).getSlotIndex();
            ItemStack itemStack = craftMatrix.getStackInSlot(slotIndex);
            if (!itemStack.isEmpty() && itemStack.getMaxStackSize() > 1) {
                ResourceLocation registryName = itemStack.getItem().getRegistryName();
                String key = Objects.toString(registryName);
                itemMap.put(key, itemStack);
                itemCount.add(key, itemStack.getCount());
            }
        }

        for (String key : itemMap.keySet()) {
            List<ItemStack> balanceList = itemMap.get(key);
            int totalCount = itemCount.count(key);
            int countPerStack = totalCount / balanceList.size();
            int restCount = totalCount % balanceList.size();
            for (ItemStack itemStack : balanceList) {
                itemStack.setCount(countPerStack);
            }

            int idx = 0;
            while (restCount > 0) {
                ItemStack itemStack = balanceList.get(idx);
                if (itemStack.getCount() < itemStack.getMaxStackSize()) {
                    itemStack.grow(1);
                    restCount--;
                }
                idx++;
                if (idx >= balanceList.size()) {
                    idx = 0;
                }
            }
        }

        container.detectAndSendChanges();
    }

    @Override
    public <T extends Container> void spreadGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        if (craftMatrix == null) {
            return;
        }

        while (true) {
            ItemStack biggestSlotStack = null;
            int biggestSlotSize = 1;
            int start = provider.getCraftingGridStart(entityPlayer, container, id);
            int size = provider.getCraftingGridSize(entityPlayer, container, id);
            for (int i = start; i < start + size; i++) {
                int slotIndex = container.inventorySlots.get(i).getSlotIndex();
                ItemStack itemStack = craftMatrix.getStackInSlot(slotIndex);
                if (!itemStack.isEmpty() && itemStack.getCount() > biggestSlotSize) {
                    biggestSlotStack = itemStack;
                    biggestSlotSize = itemStack.getCount();
                }
            }

            if (biggestSlotStack == null) {
                return;
            }

            boolean emptyBiggestSlot = false;
            for (int i = start; i < start + size; i++) {
                int slotIndex = container.inventorySlots.get(i).getSlotIndex();
                ItemStack itemStack = craftMatrix.getStackInSlot(slotIndex);
                if (itemStack.isEmpty()) {
                    if (biggestSlotStack.getCount() > 1) {
                        craftMatrix.setInventorySlotContents(slotIndex, biggestSlotStack.split(1));
                    } else {
                        emptyBiggestSlot = true;
                    }
                }
            }

            if (!emptyBiggestSlot) {
                break;
            }
        }
        balanceGrid(provider, id, entityPlayer, container);
    }

    @Override
    public <T extends Container> ItemStack putIntoGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, ItemStack itemStack, int index) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        if (craftMatrix == null) {
            return itemStack;
        }

        ItemStack craftStack = craftMatrix.getStackInSlot(index);
        if (!craftStack.isEmpty()) {
            if (craftStack.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(craftStack, itemStack)) {
                int spaceLeft = Math.min(craftMatrix.getInventoryStackLimit(), craftStack.getMaxStackSize()) - craftStack.getCount();
                if (spaceLeft > 0) {
                    ItemStack splitStack = itemStack.split(Math.min(spaceLeft, itemStack.getCount()));
                    craftStack.grow(splitStack.getCount());
                    if (itemStack.getCount() <= 0) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        } else {
            ItemStack transferStack = itemStack.split(Math.min(itemStack.getCount(), craftMatrix.getInventoryStackLimit()));
            craftMatrix.setInventorySlotContents(index, transferStack);
        }

        if (itemStack.getCount() <= 0) {
            return ItemStack.EMPTY;
        }

        return itemStack;
    }

    @Override
    public <T extends Container> boolean transferIntoGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, Slot sourceSlot) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        if (craftMatrix == null) {
            return false;
        }

        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        ItemStack itemStack = sourceSlot.getStack();
        if (itemStack.isEmpty()) {
            return false;
        }

        int firstEmptySlot = -1;
        for (int i = start; i < start + size; i++) {
            int slotIndex = container.inventorySlots.get(i).getSlotIndex();
            ItemStack craftStack = craftMatrix.getStackInSlot(slotIndex);
            if (!craftStack.isEmpty()) {
                if (craftStack.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(craftStack, itemStack)) {
                    int spaceLeft = Math.min(craftMatrix.getInventoryStackLimit(), craftStack.getMaxStackSize()) - craftStack.getCount();
                    if (spaceLeft > 0) {
                        ItemStack splitStack = itemStack.split(Math.min(spaceLeft, itemStack.getCount()));
                        craftStack.grow(splitStack.getCount());
                        if (itemStack.getCount() <= 0) {
                            return true;
                        }
                    }
                }
            } else if (firstEmptySlot == -1) {
                firstEmptySlot = slotIndex;
            }
        }

        if (itemStack.getCount() > 0 && firstEmptySlot != -1) {
            ItemStack transferStack = itemStack.split(Math.min(itemStack.getCount(), craftMatrix.getInventoryStackLimit()));
            craftMatrix.setInventorySlotContents(firstEmptySlot, transferStack);
            return true;
        }

        return false;
    }

    @Override
    public <T extends Container> boolean canTransferFrom(EntityPlayer entityPlayer, T container, Slot slot) {
        return slot.inventory == entityPlayer.inventory;
    }

    @Override
    public <T extends Container> void rotateGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, boolean counterClockwise) {
        rotateGrid(provider, id, entityPlayer, container, rotationHandler, counterClockwise);
    }

    @Override
    public <T extends Container> void rotateGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, RotationHandler rotationHandler, boolean counterClockwise) {
        IInventory craftMatrix = provider.getCraftMatrix(entityPlayer, container, id);
        if (craftMatrix == null) {
            return;
        }

        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        IInventory matrixClone = new InventoryBasic(new TextComponentString(""), size);
        for (int i = 0; i < size; i++) {
            int slotIndex = container.inventorySlots.get(start + i).getSlotIndex();
            matrixClone.setInventorySlotContents(i, craftMatrix.getStackInSlot(slotIndex));
        }

        for (int i = 0; i < size; i++) {
            if (rotationHandler.ignoreSlotId(i)) {
                continue;
            }
            int slotIndex = container.inventorySlots.get(start + rotationHandler.rotateSlotId(i, counterClockwise)).getSlotIndex();
            craftMatrix.setInventorySlotContents(slotIndex, matrixClone.getStackInSlot(i));
        }

        container.detectAndSendChanges();
    }

    @Override
    public RotationHandler getRotationHandler() {
        return rotationHandler;
    }
}
