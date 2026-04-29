package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.blay09.mods.craftingtweaks.CompressType;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Objects;

public class ClientProvider {

    private final SimpleContainer lastCraftedMatrix = new SimpleContainer(9);
    private boolean hasLastCraftedMatrix;

    private MultiPlayerGameMode getController() {
        return Objects.requireNonNull(Minecraft.getInstance().gameMode);
    }

    public void balanceGrid(Player entityPlayer, AbstractContainerMenu container, CraftingGrid grid) {
        Multimap<String, Slot> balanceSlots = ArrayListMultimap.create();
        int start = grid.getGridStartSlot(entityPlayer, container);
        int size = grid.getGridSize(entityPlayer, container);
        for (int i = start; i < start + size; i++) {
            Slot slot = container.slots.get(i);
            if (slot.hasItem()) {
                ItemStack itemStack = slot.getItem();
                if (!itemStack.isEmpty()) {
                    Identifier registryName = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                    balanceSlots.put(Objects.toString(registryName), slot);
                }
            }
        }
        for (String key : balanceSlots.keySet()) {
            Collection<Slot> slotList = balanceSlots.get(key);
            int average = 0;
            for (Slot slot : slotList) {
                ItemStack itemStack = slot.getItem();
                if (!itemStack.isEmpty()) {
                    average += itemStack.getCount();
                }
            }
            average = (int) Math.floor((float) average / (float) slotList.size());
            for (Slot slot : slotList) {
                if (!slot.hasItem()) {
                    continue;
                }
                ItemStack itemStack = slot.getItem();
                if (!itemStack.isEmpty() && itemStack.getCount() > average) {
                    // Pick up item from biggest stack
                    int mouseStackSize = itemStack.getCount();
                    getController().handleContainerInput(container.containerId, slot.index, 0, ContainerInput.PICKUP, entityPlayer);

                    for (Slot otherSlot : slotList) {
                        if (slot == otherSlot || !otherSlot.hasItem()) {
                            continue;
                        }
                        ItemStack otherStack = otherSlot.getItem();
                        if (!otherStack.isEmpty()) {
                            int otherStackSize = otherStack.getCount();
                            if (otherStackSize < average) {
                                while (otherStackSize < average && mouseStackSize > average) {
                                    getController().handleContainerInput(container.containerId, otherSlot.index, 1, ContainerInput.PICKUP, entityPlayer);
                                    mouseStackSize--;
                                    otherStackSize++;
                                }
                            }
                        }
                    }

                    // Put the remaining stack back
                    getController().handleContainerInput(container.containerId, slot.index, 0, ContainerInput.PICKUP, entityPlayer);
                }
            }
        }
    }

    public void spreadGrid(Player player, AbstractContainerMenu menu, CraftingGrid grid) {
        int tries = 0;
        while (tries < 9) {
            tries++;
            Slot biggestSlot = null;
            int biggestSlotSize = 1;
            int start = grid.getGridStartSlot(player, menu);
            int size = grid.getGridSize(player, menu);
            for (int i = start; i < start + size; i++) {
                Slot slot = menu.getSlot(i);
                ItemStack itemStack = slot.getItem();
                if (!itemStack.isEmpty() && itemStack.getCount() > biggestSlotSize) {
                    biggestSlot = slot;
                    biggestSlotSize = itemStack.getCount();
                }
            }
            if (biggestSlot == null) {
                return;
            }
            getController().handleContainerInput(menu.containerId, biggestSlot.index, 0, ContainerInput.PICKUP, player);
            for (int i = start; i < start + size; i++) {
                if (i == biggestSlot.index) {
                    continue;
                }
                ItemStack itemStack = menu.getSlot(i).getItem();
                if (itemStack.isEmpty()) {
                    if (biggestSlotSize > 1) {
                        getController().handleContainerInput(menu.containerId, i, 1, ContainerInput.PICKUP, player);
                        biggestSlotSize--;
                        if (biggestSlotSize == 1) {
                            break;
                        }
                    }
                }
            }
            getController().handleContainerInput(menu.containerId, biggestSlot.index, 0, ContainerInput.PICKUP, player);
        }

        balanceGrid(player, menu, grid);
    }

    public void clearGrid(Player player, AbstractContainerMenu menu, CraftingGrid grid, boolean forced) {
        int start = grid.getGridStartSlot(player, menu);
        int size = grid.getGridSize(player, menu);
        for (int i = start; i < start + size; i++) {
            getController().handleContainerInput(menu.containerId, i, 0, ContainerInput.QUICK_MOVE, player);
            menu.quickMoveStack(player, i);
            if (forced && menu.getSlot(i).hasItem()) {
                getController().handleContainerInput(menu.containerId, i, 0, ContainerInput.THROW, player);
            }
        }
    }

    public void rotateGrid(Player player, AbstractContainerMenu menu, CraftingGrid grid, boolean reverse) {
        if (grid.getGridSize(player, menu) != 9) {
            return;
        }

        if (!dropOffMouseStack(player, menu)) {
            return;
        }

        if (rotateGridWithBuffer(player, menu, grid, reverse)) {
            return;
        }

        int startSlot = grid.getGridStartSlot(player, menu);
        getController().handleContainerInput(menu.containerId, startSlot, 0, ContainerInput.PICKUP, player);
        int currentSlot = startSlot;
        do {
            currentSlot = startSlot + rotateSlotId(currentSlot - startSlot, reverse);
            getController().handleContainerInput(menu.containerId, currentSlot, 0, ContainerInput.PICKUP, player);
        } while (currentSlot != startSlot);
    }

    private boolean rotateGridWithBuffer(Player player, AbstractContainerMenu menu, CraftingGrid grid, boolean counterClockwise) {
        int emptyBuffer = 0;
        int[] bufferSlot = new int[2];
        for (Slot slot : menu.slots) {
            if (slot.container instanceof Inventory && !slot.hasItem()) {
                bufferSlot[emptyBuffer] = slot.index;
                emptyBuffer++;
                if (emptyBuffer >= 2) {
                    break;
                }
            }
        }
        if (emptyBuffer < 2) {
            return false;
        }
        emptyBuffer = 0;
        int startSlot = grid.getGridStartSlot(player, menu);
        int currentSlot = startSlot;
        do {
            getController().handleContainerInput(menu.containerId, currentSlot, 0, ContainerInput.PICKUP, player);
            getController().handleContainerInput(menu.containerId, bufferSlot[emptyBuffer], 0, ContainerInput.PICKUP, player);
            emptyBuffer = (emptyBuffer + 1) % 2;
            getController().handleContainerInput(menu.containerId, bufferSlot[emptyBuffer], 0, ContainerInput.PICKUP, player);
            getController().handleContainerInput(menu.containerId, currentSlot, 0, ContainerInput.PICKUP, player);
            currentSlot = startSlot + rotateSlotId(currentSlot - startSlot, counterClockwise);
        } while (currentSlot != startSlot);
        emptyBuffer = (emptyBuffer + 1) % 2;
        getController().handleContainerInput(menu.containerId, bufferSlot[emptyBuffer], 0, ContainerInput.PICKUP, player);
        getController().handleContainerInput(menu.containerId, startSlot, 0, ContainerInput.PICKUP, player);
        return true;
    }

    public boolean transferIntoGrid(Player player, AbstractContainerMenu menu, CraftingGrid grid, Slot sourceSlot) {
        if (!sourceSlot.hasItem() || !sourceSlot.mayPickup(player) || !grid.transferHandler().canTransferFrom(player, menu, sourceSlot, grid)) {
            return false;
        }

        if (!dropOffMouseStack(player, menu)) {
            return false;
        }

        getController().handleContainerInput(menu.containerId, sourceSlot.index, 0, ContainerInput.PICKUP, player);
        ItemStack mouseStack = menu.getCarried();
        if (mouseStack.isEmpty()) {
            return false;
        }

        boolean itemMoved = false;
        int firstEmptySlot = -1;
        int start = grid.getGridStartSlot(player, menu);
        int size = grid.getGridSize(player, menu);
        for (int i = start; i < start + size; i++) {
            Slot craftSlot = menu.getSlot(i);
            ItemStack craftStack = craftSlot.getItem();
            if (!craftStack.isEmpty()) {
                if (ItemStack.isSameItemSameComponents(craftStack, mouseStack)) {
                    int spaceLeft = Math.min(craftSlot.getMaxStackSize(), craftStack.getMaxStackSize()) - craftStack.getCount();
                    if (spaceLeft > 0) {
                        getController().handleContainerInput(menu.containerId, craftSlot.index, 0, ContainerInput.PICKUP, player);
                        mouseStack = menu.getCarried();
                        if (mouseStack.isEmpty()) {
                            return true;
                        }
                    }
                }
            } else if (firstEmptySlot == -1) {
                firstEmptySlot = i;
            }
        }

        if (firstEmptySlot != -1) {
            getController().handleContainerInput(menu.containerId, firstEmptySlot, 0, ContainerInput.PICKUP, player);
            itemMoved = true;
        }

        if (!menu.getCarried().isEmpty()) {
            getController().handleContainerInput(menu.containerId, sourceSlot.index, 0, ContainerInput.PICKUP, player);
        }

        dropOffMouseStack(player, menu);
        return itemMoved;
    }

    private boolean dropOffMouseStack(Player player, AbstractContainerMenu menu) {
        return dropOffMouseStack(player, menu, -1);
    }

    private boolean dropOffMouseStack(Player player, AbstractContainerMenu menu, int ignoreSlot) {
        if (menu.getCarried().isEmpty()) {
            return true;
        }

        for (int i = 0; i < menu.slots.size(); i++) {
            if (i == ignoreSlot) {
                continue;
            }

            Slot slot = menu.slots.get(i);
            if (slot.container == player.getInventory()) {
                ItemStack mouseItem = menu.getCarried();
                ItemStack slotStack = slot.getItem();
                if (slotStack.isEmpty()) {
                    getController().handleContainerInput(menu.containerId, i, 0, ContainerInput.PICKUP, player);
                } else if (ItemStack.isSameItemSameComponents(slotStack, mouseItem)) {
                    getController().handleContainerInput(menu.containerId, i, 0, ContainerInput.PICKUP, player);
                }

                if (menu.getCarried().isEmpty()) {
                    return true;
                }
            }
        }
        return menu.getCarried().isEmpty();
    }

    private void decompress(Player player, AbstractContainerMenu menu, CraftingGrid grid, Slot mouseSlot, CompressType compressType) {
        if (!mouseSlot.hasItem()) {
            return;
        }

        boolean decompressAll = compressType != CompressType.DECOMPRESS_ONE;
        // Clear the crafting grid
        clearGrid(player, menu, grid, false);
        int start = grid.getGridStartSlot(player, menu);
        int size = grid.getGridSize(player, menu);
        // Ensure the crafting grid is empty
        for (int i = start; i < start + size; i++) {
            if (menu.getSlot(i).hasItem()) {
                return;
            }
        }

        // Perform decompression on all valid slots
        for (Slot slot : menu.slots) {
            if (compressType != CompressType.DECOMPRESS_ALL && slot != mouseSlot) {
                continue;
            }
            if (slot.container instanceof Inventory && slot.hasItem() && ItemStack.isSameItemSameComponents(slot.getItem(), mouseSlot.getItem())) {
                // Move stack to crafting grid
                getController().handleContainerInput(menu.containerId, mouseSlot.index, 0, ContainerInput.PICKUP, player);
                getController().handleContainerInput(menu.containerId, start, 0, ContainerInput.PICKUP, player);
                for (Slot resultSlot : menu.slots) {
                    // Search for result slot and grab result
                    if (resultSlot instanceof ResultSlot && resultSlot.hasItem()) {
                        getController().handleContainerInput(menu.containerId,
                                resultSlot.index,
                                0,
                                decompressAll ? ContainerInput.QUICK_MOVE : ContainerInput.PICKUP,
                                player);
                        break;
                    }
                }

                dropOffMouseStack(player, menu, mouseSlot.index);
                // Take remaining stack back out of the crafting grid
                getController().handleContainerInput(menu.containerId, start, 0, ContainerInput.PICKUP, player);
                getController().handleContainerInput(menu.containerId, mouseSlot.index, 0, ContainerInput.PICKUP, player);
            }
        }
    }

    public void compress(LocalPlayer player, AbstractContainerMenu menu, CraftingGrid grid, Slot mouseSlot, CompressType compressType) {
        if (compressType == CompressType.DECOMPRESS_ALL || compressType == CompressType.DECOMPRESS_ONE || compressType == CompressType.DECOMPRESS_STACK) {
            decompress(player, menu, grid, mouseSlot, compressType);
        }
    }

    public void onItemCrafted(Container craftMatrix) {
        if (craftMatrix.getContainerSize() <= 9) {
            for (int i = 0; i < lastCraftedMatrix.getContainerSize(); i++) {
                if (i < craftMatrix.getContainerSize()) {
                    lastCraftedMatrix.setItem(i, craftMatrix.getItem(i).copy());
                } else {
                    lastCraftedMatrix.setItem(i, ItemStack.EMPTY);
                }
            }
            hasLastCraftedMatrix = true;
        }
    }

    public void refillLastCrafted(Player player, AbstractContainerMenu menu, CraftingGrid grid, boolean fullStack) {
        if (!hasLastCraftedMatrix) {
            return;
        }

        // Make sure the mouse is empty
        dropOffMouseStack(player, menu);

        int gridStart = grid.getGridStartSlot(player, menu);
        int gridSize = grid.getGridSize(player, menu);
        if (menu.slots.size() < gridStart + gridSize || gridSize != lastCraftedMatrix.getContainerSize()) {
            return;
        }

        // Now refill the grid
        for (int i = 0; i < lastCraftedMatrix.getContainerSize(); i++) {
            ItemStack itemStack = lastCraftedMatrix.getItem(i);
            if (!itemStack.isEmpty()) {
                // Search for this item in the inventory
                for (Slot slot : menu.slots) {
                    if (slot.container instanceof Inventory && slot.hasItem() && ItemStack.isSameItemSameComponents(slot.getItem(), itemStack)) {
                        getController().handleContainerInput(menu.containerId, slot.index, 0, ContainerInput.PICKUP, player);
                        getController().handleContainerInput(menu.containerId, gridStart + i, fullStack ? 0 : 1, ContainerInput.PICKUP, player);
                        getController().handleContainerInput(menu.containerId, slot.index, 0, ContainerInput.PICKUP, player);
                        break;
                    }
                }
            } else {
                if (menu.getSlot(gridStart + i).hasItem()) {
                    getController().handleContainerInput(menu.containerId, gridStart + i, 0, ContainerInput.PICKUP, player);
                    if (!dropOffMouseStack(player, menu)) {
                        getController().handleContainerInput(menu.containerId, gridStart + i, 0, ContainerInput.PICKUP, player);
                        return;
                    }
                }
            }
        }

        if (fullStack) {
            // Check if there's items missing and if so, try to find them in the crafting grid and distribute
            for (int i = 0; i < lastCraftedMatrix.getContainerSize(); i++) {
                ItemStack itemStack = lastCraftedMatrix.getItem(i);
                if (!itemStack.isEmpty() && !menu.getSlot(gridStart + i).hasItem()) {
                    for (int j = gridStart; j < gridStart + gridSize; j++) {
                        if (j == gridStart + i) {
                            continue;
                        }

                        ItemStack gridStack = menu.getSlot(j).getItem();
                        if (gridStack.getCount() > 1 && ItemStack.isSameItemSameComponents(gridStack, itemStack)) {
                            getController().handleContainerInput(menu.containerId, j, 0, ContainerInput.PICKUP, player);
                            getController().handleContainerInput(menu.containerId, gridStart + i, 1, ContainerInput.PICKUP, player);
                            getController().handleContainerInput(menu.containerId, j, 0, ContainerInput.PICKUP, player);
                            break;
                        }
                    }
                }
            }

            // Balance the grid
            balanceGrid(player, menu, grid);
        }

        dropOffMouseStack(player, menu);
    }

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
}
