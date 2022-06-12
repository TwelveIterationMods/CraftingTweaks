package net.blay09.mods.craftingtweaks.api.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.GridBalanceHandler;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class DefaultGridBalanceHandler implements GridBalanceHandler<AbstractContainerMenu> {
    @Override
    public void balanceGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu) {
        Container craftMatrix = grid.getCraftingMatrix(player, menu);
        if (craftMatrix == null) {
            return;
        }

        ArrayListMultimap<String, ItemStack> itemMap = ArrayListMultimap.create();
        Multiset<String> itemCount = HashMultiset.create();
        int start = grid.getGridStartSlot(player, menu);
        int size = grid.getGridSize(player, menu);
        for (int i = start; i < start + size; i++) {
            int slotIndex = menu.slots.get(i).getContainerSlot();
            ItemStack itemStack = craftMatrix.getItem(slotIndex);
            if (!itemStack.isEmpty() && itemStack.getMaxStackSize() > 1) {
                ResourceLocation registryName = Registry.ITEM.getKey(itemStack.getItem());
                String key = Objects.toString(registryName);
                if (itemStack.getTag() != null) {
                    key = key + "@" + itemStack.getTag().toString();
                }
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

        menu.broadcastChanges();
    }

    @Override
    public void spreadGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu) {
        Container craftMatrix = grid.getCraftingMatrix(player, menu);
        if (craftMatrix == null) {
            return;
        }

        while (true) {
            ItemStack biggestSlotStack = null;
            int biggestSlotSize = 1;
            int start = grid.getGridStartSlot(player, menu);
            int size = grid.getGridSize(player, menu);
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

        balanceGrid(grid, player, menu);
    }
}
