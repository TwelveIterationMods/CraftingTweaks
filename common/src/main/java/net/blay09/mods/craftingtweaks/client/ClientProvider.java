package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.blay09.mods.craftingtweaks.CompressType;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.InventoryCraftingCompress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.Objects;

public class ClientProvider {

    private final SimpleContainer lastCraftedMatrix = new SimpleContainer(9);
    private boolean hasLastCraftedMatrix;

    private MultiPlayerGameMode getController() {
        return Minecraft.getInstance().gameMode;
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
                    ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
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
                    getController().handleInventoryMouseClick(container.containerId, slot.index, 0, ClickType.PICKUP, entityPlayer);

                    for (Slot otherSlot : slotList) {
                        if (slot == otherSlot || !otherSlot.hasItem()) {
                            continue;
                        }
                        ItemStack otherStack = otherSlot.getItem();
                        if (!otherStack.isEmpty()) {
                            int otherStackSize = otherStack.getCount();
                            if (otherStackSize < average) {
                                while (otherStackSize < average && mouseStackSize > average) {
                                    getController().handleInventoryMouseClick(container.containerId, otherSlot.index, 1, ClickType.PICKUP, entityPlayer);
                                    mouseStackSize--;
                                    otherStackSize++;
                                }
                            }
                        }
                    }

                    // Put the remaining stack back
                    getController().handleInventoryMouseClick(container.containerId, slot.index, 0, ClickType.PICKUP, entityPlayer);
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
                Slot slot = menu.slots.get(i);
                ItemStack itemStack = slot.getItem();
                if (!itemStack.isEmpty() && itemStack.getCount() > biggestSlotSize) {
                    biggestSlot = slot;
                    biggestSlotSize = itemStack.getCount();
                }
            }
            if (biggestSlot == null) {
                return;
            }
            getController().handleInventoryMouseClick(menu.containerId, biggestSlot.index, 0, ClickType.PICKUP, player);
            for (int i = start; i < start + size; i++) {
                if (i == biggestSlot.index) {
                    continue;
                }
                ItemStack itemStack = menu.slots.get(i).getItem();
                if (itemStack.isEmpty()) {
                    if (biggestSlotSize > 1) {
                        getController().handleInventoryMouseClick(menu.containerId, i, 1, ClickType.PICKUP, player);
                        biggestSlotSize--;
                        if (biggestSlotSize == 1) {
                            break;
                        }
                    }
                }
            }
            getController().handleInventoryMouseClick(menu.containerId, biggestSlot.index, 0, ClickType.PICKUP, player);
        }

        balanceGrid(player, menu, grid);
    }

    public void clearGrid(Player player, AbstractContainerMenu menu, CraftingGrid grid, boolean forced) {
        int start = grid.getGridStartSlot(player, menu);
        int size = grid.getGridSize(player, menu);
        for (int i = start; i < start + size; i++) {
            getController().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.QUICK_MOVE, player);
            menu.quickMoveStack(player, i);
            if (forced && menu.slots.get(i).hasItem()) {
                getController().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.THROW, player);
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
        getController().handleInventoryMouseClick(menu.containerId, startSlot, 0, ClickType.PICKUP, player);
        int currentSlot = startSlot;
        do {
            currentSlot = startSlot + rotateSlotId(currentSlot - startSlot, reverse);
            getController().handleInventoryMouseClick(menu.containerId, currentSlot, 0, ClickType.PICKUP, player);
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
            getController().handleInventoryMouseClick(menu.containerId, currentSlot, 0, ClickType.PICKUP, player);
            getController().handleInventoryMouseClick(menu.containerId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, player);
            emptyBuffer = (emptyBuffer + 1) % 2;
            getController().handleInventoryMouseClick(menu.containerId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, player);
            getController().handleInventoryMouseClick(menu.containerId, currentSlot, 0, ClickType.PICKUP, player);
            currentSlot = startSlot + rotateSlotId(currentSlot - startSlot, counterClockwise);
        } while (currentSlot != startSlot);
        emptyBuffer = (emptyBuffer + 1) % 2;
        getController().handleInventoryMouseClick(menu.containerId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, player);
        getController().handleInventoryMouseClick(menu.containerId, startSlot, 0, ClickType.PICKUP, player);
        return true;
    }

    public boolean transferIntoGrid(Player player, AbstractContainerMenu menu, CraftingGrid grid, Slot sourceSlot) {
        if (!sourceSlot.hasItem() || !sourceSlot.mayPickup(player) || !grid.transferHandler().canTransferFrom(player, menu, sourceSlot, grid)) {
            return false;
        }

        if (!dropOffMouseStack(player, menu)) {
            return false;
        }

        getController().handleInventoryMouseClick(menu.containerId, sourceSlot.index, 0, ClickType.PICKUP, player);
        ItemStack mouseStack = menu.getCarried();
        if (mouseStack.isEmpty()) {
            return false;
        }

        boolean itemMoved = false;
        int firstEmptySlot = -1;
        int start = grid.getGridStartSlot(player, menu);
        int size = grid.getGridSize(player, menu);
        for (int i = start; i < start + size; i++) {
            Slot craftSlot = menu.slots.get(i);
            ItemStack craftStack = craftSlot.getItem();
            if (!craftStack.isEmpty()) {
                if (ItemStack.isSameItemSameComponents(craftStack, mouseStack)) {
                    int spaceLeft = Math.min(craftSlot.getMaxStackSize(), craftStack.getMaxStackSize()) - craftStack.getCount();
                    if (spaceLeft > 0) {
                        getController().handleInventoryMouseClick(menu.containerId, craftSlot.index, 0, ClickType.PICKUP, player);
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
            getController().handleInventoryMouseClick(menu.containerId, firstEmptySlot, 0, ClickType.PICKUP, player);
            itemMoved = true;
        }

        if (!menu.getCarried().isEmpty()) {
            getController().handleInventoryMouseClick(menu.containerId, sourceSlot.index, 0, ClickType.PICKUP, player);
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
                    getController().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.PICKUP, player);
                } else if (ItemStack.isSameItemSameComponents(slotStack, mouseItem)) {
                    getController().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.PICKUP, player);
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
            if (menu.slots.get(i).hasItem()) {
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
                getController().handleInventoryMouseClick(menu.containerId, mouseSlot.index, 0, ClickType.PICKUP, player);
                getController().handleInventoryMouseClick(menu.containerId, start, 0, ClickType.PICKUP, player);
                for (Slot resultSlot : menu.slots) {
                    // Search for result slot and grab result
                    if (resultSlot instanceof ResultSlot && resultSlot.hasItem()) {
                        getController().handleInventoryMouseClick(menu.containerId,
                                resultSlot.index,
                                0,
                                decompressAll ? ClickType.QUICK_MOVE : ClickType.PICKUP,
                                player);
                        break;
                    }
                }

                dropOffMouseStack(player, menu, mouseSlot.index);
                // Take remaining stack back out of the crafting grid
                getController().handleInventoryMouseClick(menu.containerId, start, 0, ClickType.PICKUP, player);
                getController().handleInventoryMouseClick(menu.containerId, mouseSlot.index, 0, ClickType.PICKUP, player);
            }
        }
    }

    public void compress(LocalPlayer player, AbstractContainerMenu menu, CraftingGrid grid, Slot mouseSlot, CompressType compressType) {
        if (compressType == CompressType.DECOMPRESS_ALL || compressType == CompressType.DECOMPRESS_ONE || compressType == CompressType.DECOMPRESS_STACK) {
            decompress(player, menu, grid, mouseSlot, compressType);
            return;
        }

        if (!mouseSlot.hasItem()) {
            return;
        }

        boolean compressAll = compressType != CompressType.COMPRESS_ONE;
        // Clear the crafting grid
        clearGrid(player, menu, grid, false);
        int start = grid.getGridStartSlot(player, menu);
        int size = grid.getGridSize(player, menu);
        // Ensure the crafting grid is empty
        for (int i = start; i < start + size; i++) {
            if (menu.slots.get(i).hasItem()) {
                return;
            }
        }

        // Perform decompression on all valid slots
        for (Slot slot : menu.slots) {
            if (compressType != CompressType.COMPRESS_ALL && slot != mouseSlot) {
                continue;
            }
            if (slot.container instanceof Inventory && slot.hasItem() && ItemStack.isSameItemSameComponents(slot.getItem(), mouseSlot.getItem())) {
                ItemStack result;
                ItemStack mouseStack = slot.getItem();
                if (size == 9 && !mouseStack.isEmpty() && mouseStack.getCount() >= 9) {
                    final var craftingContainer3x3 = new InventoryCraftingCompress(menu, 3, mouseStack);
                    result = findMatchingResult(craftingContainer3x3.asCraftInput(), player);
                    if (!result.isEmpty() && !isCompressBlacklisted(result)) {
                        getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                        getController().handleInventoryMouseClick(menu.containerId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, player);
                        for (int i = start; i < start + size; i++) {
                            getController().handleInventoryMouseClick(menu.containerId, i, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                        }
                        getController().handleInventoryMouseClick(menu.containerId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, player);
                        getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                    } else {
                        final var craftingContainer2x2 = new InventoryCraftingCompress(menu, 2, mouseStack);
                        result = findMatchingResult(craftingContainer2x2.asCraftInput(), player);
                        if (!result.isEmpty() && !isCompressBlacklisted(result)) {
                            getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                            getController().handleInventoryMouseClick(menu.containerId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, start, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, start + 1, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, start + 3, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, start + 4, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                        } else {
                            return;
                        }
                    }
                } else if (size >= 4 && !mouseStack.isEmpty() && mouseStack.getCount() >= 4) {
                    final var craftingContainer = new InventoryCraftingCompress(menu, 2, mouseStack);
                    result = findMatchingResult(craftingContainer.asCraftInput(), player);
                    if (!result.isEmpty() && !isCompressBlacklisted(result)) {
                        getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                        getController().handleInventoryMouseClick(menu.containerId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, player);
                        if (size == 4) {
                            for (int i = start; i < start + size; i++) {
                                getController().handleInventoryMouseClick(menu.containerId, i, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            }
                        } else {
                            getController().handleInventoryMouseClick(menu.containerId, start, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, start + 1, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, start + 3, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().handleInventoryMouseClick(menu.containerId, start + 4, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                        }
                        getController().handleInventoryMouseClick(menu.containerId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, player);
                        getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                    } else {
                        return;
                    }
                }
                for (Slot resultSlot : menu.slots) {
                    if (resultSlot instanceof ResultSlot && resultSlot.hasItem()) {
                        getController().handleInventoryMouseClick(menu.containerId,
                                resultSlot.index,
                                0,
                                compressAll ? ClickType.QUICK_MOVE : ClickType.PICKUP,
                                player);
                        break;
                    }
                }
                dropOffMouseStack(player, menu, slot.index);
                for (int i = start; i < start + size; i++) {
                    if (menu.slots.get(i).hasItem()) {
                        getController().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.PICKUP, player);
                        getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                    }
                }

                dropOffMouseStack(player, menu);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends RecipeInput> ItemStack findMatchingResult(T craftingInventory, LocalPlayer player) {
        for (RecipeCollection recipeList : player.getRecipeBook().getCollections()) {
            for (RecipeHolder<?> recipe : recipeList.getRecipes()) {
                if (recipe.value().getType() == RecipeType.CRAFTING) {
                    final var craftingRecipe = (Recipe<RecipeInput>) recipe.value();
                    if (craftingRecipe.matches(craftingInventory, player.level())) {
                        return craftingRecipe.assemble(craftingInventory, player.level().registryAccess());
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private static int getDragSplittingButton(int id, int limit) {
        return id & 3 | (limit & 3) << 2;
    }

    private boolean isCompressBlacklisted(ItemStack result) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(result.getItem());
        return registryName != null && CraftingTweaksConfig.getActive().common.compressDenylist.contains(registryName.toString());
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
                        getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                        getController().handleInventoryMouseClick(menu.containerId, gridStart + i, fullStack ? 0 : 1, ClickType.PICKUP, player);
                        getController().handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
                        break;
                    }
                }
            } else {
                if (menu.slots.get(gridStart + i).hasItem()) {
                    getController().handleInventoryMouseClick(menu.containerId, gridStart + i, 0, ClickType.PICKUP, player);
                    if (!dropOffMouseStack(player, menu)) {
                        getController().handleInventoryMouseClick(menu.containerId, gridStart + i, 0, ClickType.PICKUP, player);
                        return;
                    }
                }
            }
        }

        if (fullStack) {
            // Check if there's items missing and if so, try to find them in the crafting grid and distribute
            for (int i = 0; i < lastCraftedMatrix.getContainerSize(); i++) {
                ItemStack itemStack = lastCraftedMatrix.getItem(i);
                if (!itemStack.isEmpty() && !menu.slots.get(gridStart + i).hasItem()) {
                    for (int j = gridStart; j < gridStart + gridSize; j++) {
                        if (j == gridStart + i) {
                            continue;
                        }

                        ItemStack gridStack = menu.slots.get(j).getItem();
                        if (gridStack.getCount() > 1 && ItemStack.isSameItemSameComponents(gridStack, itemStack)) {
                            getController().handleInventoryMouseClick(menu.containerId, j, 0, ClickType.PICKUP, player);
                            getController().handleInventoryMouseClick(menu.containerId, gridStart + i, 1, ClickType.PICKUP, player);
                            getController().handleInventoryMouseClick(menu.containerId, j, 0, ClickType.PICKUP, player);
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

    public boolean rotateIgnoresSlotId(int slotId) {
        return slotId == 4;
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
