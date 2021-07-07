package net.blay09.mods.craftingtweaks;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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
    public <T extends AbstractContainerMenu> void clearGrid(TweakProvider<T> provider, int id, Player player, T menu, boolean phantomItems, boolean forced) {
        Container craftMatrix = provider.getCraftMatrix(player, menu, id);
        if (craftMatrix == null) {
            return;
        }

        int start = provider.getCraftingGridStart(player, menu, id);
        int size = provider.getCraftingGridSize(player, menu, id);
        for (int i = start; i < start + size; i++) {
            int slotIndex = menu.slots.get(i).getContainerSlot();
            if (phantomItems) {
                craftMatrix.setItem(slotIndex, ItemStack.EMPTY);
            } else {
                ItemStack itemStack = craftMatrix.getItem(slotIndex);
                if (!itemStack.isEmpty()) {
                    ItemStack returnStack = itemStack.copy();
                    player.getInventory().add(returnStack);
                    craftMatrix.setItem(slotIndex, returnStack.getCount() == 0 ? ItemStack.EMPTY : returnStack);
                    if (returnStack.getCount() > 0 && forced) {
                        player.drop(returnStack, false);
                        craftMatrix.setItem(slotIndex, ItemStack.EMPTY);
                    }
                }
            }
        }
        menu.broadcastChanges();
    }

    @Override
    public <T extends AbstractContainerMenu> void balanceGrid(TweakProvider<T> provider, int id, Player player, T container) {
        Container craftMatrix = provider.getCraftMatrix(player, container, id);
        if (craftMatrix == null) {
            return;
        }

        ArrayListMultimap<String, ItemStack> itemMap = ArrayListMultimap.create();
        Multiset<String> itemCount = HashMultiset.create();
        int start = provider.getCraftingGridStart(player, container, id);
        int size = provider.getCraftingGridSize(player, container, id);
        for (int i = start; i < start + size; i++) {
            int slotIndex = container.slots.get(i).getContainerSlot();
            ItemStack itemStack = craftMatrix.getItem(slotIndex);
            if (!itemStack.isEmpty() && itemStack.getMaxStackSize() > 1) {
                ResourceLocation registryName = Registry.ITEM.getKey(itemStack.getItem());
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

        container.broadcastChanges();
    }

    @Override
    public <T extends AbstractContainerMenu> void spreadGrid(TweakProvider<T> provider, int id, Player player, T menu) {
        Container craftMatrix = provider.getCraftMatrix(player, menu, id);
        if (craftMatrix == null) {
            return;
        }

        while (true) {
            ItemStack biggestSlotStack = null;
            int biggestSlotSize = 1;
            int start = provider.getCraftingGridStart(player, menu, id);
            int size = provider.getCraftingGridSize(player, menu, id);
            for (int i = start; i < start + size; i++) {
                int slotIndex = menu.slots.get(i).getContainerSlot();
                ItemStack itemStack = craftMatrix.getItem(slotIndex);
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
                int slotIndex = menu.slots.get(i).getContainerSlot();
                ItemStack itemStack = craftMatrix.getItem(slotIndex);
                if (itemStack.isEmpty()) {
                    if (biggestSlotStack.getCount() > 1) {
                        craftMatrix.setItem(slotIndex, biggestSlotStack.split(1));
                    } else {
                        emptyBiggestSlot = true;
                    }
                }
            }

            if (!emptyBiggestSlot) {
                break;
            }
        }
        balanceGrid(provider, id, player, menu);
    }

    @Override
    public <T extends AbstractContainerMenu> ItemStack putIntoGrid(TweakProvider<T> provider, int id, Player player, T menu, ItemStack itemStack, int index) {
        Container craftMatrix = provider.getCraftMatrix(player, menu, id);
        if (craftMatrix == null) {
            return itemStack;
        }

        ItemStack craftStack = craftMatrix.getItem(index);
        if (!craftStack.isEmpty()) {
            if (craftStack.sameItem(itemStack) && ItemStack.tagMatches(craftStack, itemStack)) {
                int spaceLeft = Math.min(craftMatrix.getMaxStackSize(), craftStack.getMaxStackSize()) - craftStack.getCount();
                if (spaceLeft > 0) {
                    ItemStack splitStack = itemStack.split(Math.min(spaceLeft, itemStack.getCount()));
                    craftStack.grow(splitStack.getCount());
                    if (itemStack.getCount() <= 0) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        } else {
            ItemStack transferStack = itemStack.split(Math.min(itemStack.getCount(), craftMatrix.getMaxStackSize()));
            craftMatrix.setItem(index, transferStack);
        }

        if (itemStack.getCount() <= 0) {
            return ItemStack.EMPTY;
        }

        return itemStack;
    }

    @Override
    public <T extends AbstractContainerMenu> boolean transferIntoGrid(TweakProvider<T> provider, int id, Player player, T menu, Slot sourceSlot) {
        Container craftMatrix = provider.getCraftMatrix(player, menu, id);
        if (craftMatrix == null) {
            return false;
        }

        int start = provider.getCraftingGridStart(player, menu, id);
        int size = provider.getCraftingGridSize(player, menu, id);
        ItemStack itemStack = sourceSlot.getItem();
        if (itemStack.isEmpty()) {
            return false;
        }

        int firstEmptySlot = -1;
        for (int i = start; i < start + size; i++) {
            int slotIndex = menu.slots.get(i).getContainerSlot();
            ItemStack craftStack = craftMatrix.getItem(slotIndex);
            if (!craftStack.isEmpty()) {
                if (craftStack.sameItem(itemStack) && ItemStack.tagMatches(craftStack, itemStack)) {
                    int spaceLeft = Math.min(craftMatrix.getMaxStackSize(), craftStack.getMaxStackSize()) - craftStack.getCount();
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
            ItemStack transferStack = itemStack.split(Math.min(itemStack.getCount(), craftMatrix.getMaxStackSize()));
            craftMatrix.setItem(firstEmptySlot, transferStack);
            return true;
        }

        return false;
    }

    @Override
    public <T extends AbstractContainerMenu> boolean canTransferFrom(Player player, T menu, Slot slot) {
        return slot.container == player.getInventory();
    }

    @Override
    public <T extends AbstractContainerMenu> void rotateGrid(TweakProvider<T> provider, int id, Player player, T menu, boolean counterClockwise) {
        rotateGrid(provider, id, player, menu, rotationHandler, counterClockwise);
    }

    @Override
    public <T extends AbstractContainerMenu> void rotateGrid(TweakProvider<T> provider, int id, Player player, T menu, RotationHandler rotationHandler, boolean counterClockwise) {
        Container craftMatrix = provider.getCraftMatrix(player, menu, id);
        if (craftMatrix == null) {
            return;
        }

        int start = provider.getCraftingGridStart(player, menu, id);
        int size = provider.getCraftingGridSize(player, menu, id);
        Container matrixClone = new SimpleContainer(size);
        for (int i = 0; i < size; i++) {
            int slotIndex = menu.slots.get(start + i).getContainerSlot();
            matrixClone.setItem(i, craftMatrix.getItem(slotIndex));
        }

        for (int i = 0; i < size; i++) {
            if (rotationHandler.ignoreSlotId(i)) {
                continue;
            }
            int slotIndex = menu.slots.get(start + rotationHandler.rotateSlotId(i, counterClockwise)).getContainerSlot();
            craftMatrix.setItem(slotIndex, matrixClone.getItem(i));
        }

        menu.broadcastChanges();
    }

    @Override
    public RotationHandler getRotationHandler() {
        return rotationHandler;
    }
}
