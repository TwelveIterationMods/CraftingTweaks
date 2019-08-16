package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.blay09.mods.craftingtweaks.CompressType;
import net.blay09.mods.craftingtweaks.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.InventoryCraftingCompress;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Collection;
import java.util.Objects;

public class ClientProvider {

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

    private final ItemStackHandler lastCraftedMatrix = new ItemStackHandler(9);
    private boolean hasLastCraftedMatrix;

    private PlayerController getController() {
        return Minecraft.getInstance().playerController;
    }

    private boolean canBalance(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public void balanceGrid(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id) {
        if (!canBalance(provider, entityPlayer, container, id)) {
            return;
        }
        Multimap<String, Slot> balanceSlots = ArrayListMultimap.create();
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for (int i = start; i < start + size; i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot.getHasStack()) {
                ItemStack itemStack = slot.getStack();
                if (!itemStack.isEmpty()) {
                    balanceSlots.put(Objects.toString(itemStack.getItem().getRegistryName()), slot);
                }
            }
        }
        for (String key : balanceSlots.keySet()) {
            Collection<Slot> slotList = balanceSlots.get(key);
            int average = 0;
            for (Slot slot : slotList) {
                ItemStack itemStack = slot.getStack();
                if (!itemStack.isEmpty()) {
                    average += itemStack.getCount();
                }
            }
            average = (int) Math.floor((float) average / (float) slotList.size());
            for (Slot slot : slotList) {
                if (!slot.getHasStack()) {
                    continue;
                }
                ItemStack itemStack = slot.getStack();
                if (!itemStack.isEmpty() && itemStack.getCount() > average) {
                    // Pick up item from biggest stack
                    int mouseStackSize = itemStack.getCount();
                    getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer); // windowClick

                    for (Slot otherSlot : slotList) {
                        if (slot == otherSlot || !otherSlot.getHasStack()) {
                            continue;
                        }
                        ItemStack otherStack = otherSlot.getStack();
                        if (!otherStack.isEmpty()) {
                            int otherStackSize = otherStack.getCount();
                            if (otherStackSize < average) {
                                while (otherStackSize < average && mouseStackSize > average) {
                                    getController().windowClick(container.windowId, otherSlot.slotNumber, 1, ClickType.PICKUP, entityPlayer); // windowClick
                                    mouseStackSize--;
                                    otherStackSize++;
                                }
                            }
                        }
                    }

                    // Put the remaining stack back
                    getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer); // windowClick
                }
            }
        }
    }

    public void spreadGrid(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id) {
        int tries = 0;
        while (tries < 9) {
            tries++;
            Slot biggestSlot = null;
            int biggestSlotSize = 1;
            int start = provider.getCraftingGridStart(entityPlayer, container, id);
            int size = provider.getCraftingGridSize(entityPlayer, container, id);
            for (int i = start; i < start + size; i++) {
                Slot slot = container.inventorySlots.get(i);
                ItemStack itemStack = slot.getStack();
                if (!itemStack.isEmpty() && itemStack.getCount() > biggestSlotSize) {
                    biggestSlot = slot;
                    biggestSlotSize = itemStack.getCount();
                }
            }
            if (biggestSlot == null) {
                return;
            }
            getController().windowClick(container.windowId, biggestSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
            for (int i = start; i < start + size; i++) {
                if (i == biggestSlot.slotNumber) {
                    continue;
                }
                ItemStack itemStack = container.inventorySlots.get(i).getStack();
                if (itemStack.isEmpty()) {
                    if (biggestSlotSize > 1) {
                        getController().windowClick(container.windowId, i, 1, ClickType.PICKUP, entityPlayer);
                        biggestSlotSize--;
                        if (biggestSlotSize == 1) {
                            break;
                        }
                    }
                }
            }
            getController().windowClick(container.windowId, biggestSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
        }
        balanceGrid(provider, entityPlayer, container, id);
    }

    private boolean canClear(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public void clearGrid(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id, boolean forced) {
        if (!canClear(provider, entityPlayer, container, id)) {
            return;
        }
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for (int i = start; i < start + size; i++) {
            getController().windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, entityPlayer);
            container.transferStackInSlot(entityPlayer, i);
            if (forced && container.inventorySlots.get(i).getHasStack()) {
                getController().windowClick(container.windowId, i, 0, ClickType.THROW, entityPlayer);
            }
        }
    }

    private boolean canRotate(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id) {
        return !provider.requiresServerSide() && provider.getCraftingGridSize(entityPlayer, container, id) == 9;
    }

    public void rotateGrid(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id, boolean counterClockwise) {
        if (!canRotate(provider, entityPlayer, container, id)) {
            return;
        }
        if (!dropOffMouseStack(entityPlayer, container)) {
            return;
        }
        if (rotateGridWithBuffer(provider, entityPlayer, container, id, counterClockwise)) {
            return;
        }
        int startSlot = provider.getCraftingGridStart(entityPlayer, container, id);
        getController().windowClick(container.windowId, startSlot, 0, ClickType.PICKUP, entityPlayer);
        int currentSlot = startSlot;
        do {
            currentSlot = startSlot + rotationHandler.rotateSlotId(currentSlot - startSlot, counterClockwise);
            getController().windowClick(container.windowId, currentSlot, 0, ClickType.PICKUP, entityPlayer);
        } while (currentSlot != startSlot);
    }

    private boolean rotateGridWithBuffer(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id, boolean counterClockwise) {
        int emptyBuffer = 0;
        int[] bufferSlot = new int[2];
        for (Object obj : container.inventorySlots) {
            Slot slot = (Slot) obj;
            if (slot.inventory instanceof PlayerInventory && !slot.getHasStack()) {
                bufferSlot[emptyBuffer] = slot.slotNumber;
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
        int startSlot = provider.getCraftingGridStart(entityPlayer, container, id);
        int currentSlot = startSlot;
        do {
            getController().windowClick(container.windowId, currentSlot, 0, ClickType.PICKUP, entityPlayer);
            getController().windowClick(container.windowId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, entityPlayer);
            emptyBuffer = (emptyBuffer + 1) % 2;
            getController().windowClick(container.windowId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, entityPlayer);
            getController().windowClick(container.windowId, currentSlot, 0, ClickType.PICKUP, entityPlayer);
            currentSlot = startSlot + rotationHandler.rotateSlotId(currentSlot - startSlot, counterClockwise);
        } while (currentSlot != startSlot);
        emptyBuffer = (emptyBuffer + 1) % 2;
        getController().windowClick(container.windowId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, entityPlayer);
        getController().windowClick(container.windowId, startSlot, 0, ClickType.PICKUP, entityPlayer);
        return true;
    }

    private boolean canTransfer(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public boolean transferIntoGrid(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id, Slot sourceSlot) {
        if (!canTransfer(provider, entityPlayer, container, id)) {
            return false;
        }

        if (!sourceSlot.getHasStack() || !sourceSlot.canTakeStack(entityPlayer) || !provider.canTransferFrom(entityPlayer, container, id, sourceSlot)) {
            return false;
        }

        if (!dropOffMouseStack(entityPlayer, container)) {
            return false;
        }

        getController().windowClick(container.windowId, sourceSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
        ItemStack mouseStack = entityPlayer.inventory.getItemStack();
        if (mouseStack.isEmpty()) {
            return false;
        }

        boolean itemMoved = false;
        int firstEmptySlot = -1;
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for (int i = start; i < start + size; i++) {
            Slot craftSlot = container.inventorySlots.get(i);
            ItemStack craftStack = craftSlot.getStack();
            if (!craftStack.isEmpty()) {
                if (craftStack.isItemEqual(mouseStack) && ItemStack.areItemStackTagsEqual(craftStack, mouseStack)) {
                    int spaceLeft = Math.min(craftSlot.getSlotStackLimit(), craftStack.getMaxStackSize()) - craftStack.getCount();
                    if (spaceLeft > 0) {
                        getController().windowClick(container.windowId, craftSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                        mouseStack = entityPlayer.inventory.getItemStack();
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
            getController().windowClick(container.windowId, firstEmptySlot, 0, ClickType.PICKUP, entityPlayer);
            itemMoved = true;
        }

        if (!entityPlayer.inventory.getItemStack().isEmpty()) {
            getController().windowClick(container.windowId, sourceSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
        }

        dropOffMouseStack(entityPlayer, container);
        return itemMoved;
    }

    private boolean dropOffMouseStack(PlayerEntity entityPlayer, Container container) {
        return dropOffMouseStack(entityPlayer, container, -1);
    }

    private boolean dropOffMouseStack(PlayerEntity entityPlayer, Container container, int ignoreSlot) {
        if (entityPlayer.inventory.getItemStack().isEmpty()) {
            return true;
        }

        for (int i = 0; i < container.inventorySlots.size(); i++) {
            if (i == ignoreSlot) {
                continue;
            }

            Slot slot = container.inventorySlots.get(i);
            if (slot.inventory == entityPlayer.inventory) {
                ItemStack mouseItem = entityPlayer.inventory.getItemStack();
                ItemStack slotStack = slot.getStack();
                if (slotStack.isEmpty()) {
                    getController().windowClick(container.windowId, i, 0, ClickType.PICKUP, entityPlayer);
                } else if (mouseItem.getItem() == slotStack.getItem() && ItemStack.areItemStackTagsEqual(slotStack, mouseItem)) {
                    getController().windowClick(container.windowId, i, 0, ClickType.PICKUP, entityPlayer);
                }

                if (entityPlayer.inventory.getItemStack().isEmpty()) {
                    return true;
                }
            }
        }
        return entityPlayer.inventory.getItemStack().isEmpty();
    }

    private void decompress(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, Slot mouseSlot, CompressType compressType) {
        if (!mouseSlot.getHasStack() || !canClear(provider, entityPlayer, container, 0)) {
            return;
        }

        boolean decompressAll = compressType != CompressType.DECOMPRESS_ONE;
        // Clear the crafting grid
        clearGrid(provider, entityPlayer, container, 0, false);
        int start = provider.getCraftingGridStart(entityPlayer, container, 0);
        int size = provider.getCraftingGridSize(entityPlayer, container, 0);
        // Ensure the crafting grid is empty
        for (int i = start; i < start + size; i++) {
            if (container.inventorySlots.get(i).getHasStack()) {
                return;
            }
        }

        // Perform decompression on all valid slots
        for (Slot slot : container.inventorySlots) {
            if (compressType != CompressType.DECOMPRESS_ALL && slot != mouseSlot) {
                continue;
            }
            if (slot.inventory instanceof PlayerInventory && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), mouseSlot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), mouseSlot.getStack())) {
                // Move stack to crafting grid
                getController().windowClick(container.windowId, mouseSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                getController().windowClick(container.windowId, start, 0, ClickType.PICKUP, entityPlayer);
                for (Slot resultSlot : container.inventorySlots) {
                    // Search for result slot and grab result
                    if (resultSlot instanceof CraftingResultSlot && resultSlot.getHasStack()) {
                        getController().windowClick(container.windowId, resultSlot.slotNumber, 0, decompressAll ? ClickType.QUICK_MOVE : ClickType.PICKUP, entityPlayer);
                        break;
                    }
                }

                dropOffMouseStack(entityPlayer, container, mouseSlot.slotNumber);
                // Take remaining stack back out of the crafting grid
                getController().windowClick(container.windowId, start, 0, ClickType.PICKUP, entityPlayer);
                getController().windowClick(container.windowId, mouseSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
            }
        }
    }

    public void compress(TweakProvider<Container> provider, ClientPlayerEntity player, Container container, Slot mouseSlot, CompressType compressType) {
        if (compressType == CompressType.DECOMPRESS_ALL || compressType == CompressType.DECOMPRESS_ONE || compressType == CompressType.DECOMPRESS_STACK) {
            decompress(provider, player, container, mouseSlot, compressType);
            return;
        }

        if (!mouseSlot.getHasStack() || !canClear(provider, player, container, 0)) {
            return;
        }

        boolean compressAll = compressType != CompressType.COMPRESS_ONE;
        // Clear the crafting grid
        clearGrid(provider, player, container, 0, false);
        int start = provider.getCraftingGridStart(player, container, 0);
        int size = provider.getCraftingGridSize(player, container, 0);
        // Ensure the crafting grid is empty
        for (int i = start; i < start + size; i++) {
            if (container.inventorySlots.get(i).getHasStack()) {
                return;
            }
        }

        // Perform decompression on all valid slots
        for (Slot slot : container.inventorySlots) {
            if (compressType != CompressType.COMPRESS_ALL && slot != mouseSlot) {
                continue;
            }
            if (slot.inventory instanceof PlayerInventory && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), mouseSlot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), mouseSlot.getStack())) {
                ItemStack result;
                ItemStack mouseStack = slot.getStack();
                if (size == 9 && !mouseStack.isEmpty() && mouseStack.getCount() >= 9) {
                    result = findMatchingResult(new InventoryCraftingCompress(container, 3, mouseStack), player);
                    if (!result.isEmpty() && !isCompressBlacklisted(result)) {
                        getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, player);
                        getController().windowClick(container.windowId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, player);
                        for (int i = start; i < start + size; i++) {
                            getController().windowClick(container.windowId, i, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                        }
                        getController().windowClick(container.windowId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, player);
                        getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, player);
                    } else {
                        result = findMatchingResult(new InventoryCraftingCompress(container, 2, mouseStack), player);
                        if (!result.isEmpty() && !isCompressBlacklisted(result)) {
                            getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, player);
                            getController().windowClick(container.windowId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, start, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, start + 1, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, start + 3, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, start + 4, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, player);
                        } else {
                            return;
                        }
                    }
                } else if (size >= 4 && !mouseStack.isEmpty() && mouseStack.getCount() >= 4) {
                    result = findMatchingResult(new InventoryCraftingCompress(container, 2, mouseStack), player);
                    if (!result.isEmpty() && !isCompressBlacklisted(result)) {
                        getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, player);
                        getController().windowClick(container.windowId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, player);
                        if (size == 4) {
                            for (int i = start; i < start + size; i++) {
                                getController().windowClick(container.windowId, i, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            }
                        } else {
                            getController().windowClick(container.windowId, start, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, start + 1, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, start + 3, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                            getController().windowClick(container.windowId, start + 4, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, player);
                        }
                        getController().windowClick(container.windowId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, player);
                        getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, player);
                    } else {
                        return;
                    }
                }
                for (Slot resultSlot : container.inventorySlots) {
                    if (resultSlot instanceof CraftingResultSlot && resultSlot.getHasStack()) {
                        getController().windowClick(container.windowId, resultSlot.slotNumber, 0, compressAll ? ClickType.QUICK_MOVE : ClickType.PICKUP, player);
                        break;
                    }
                }
                dropOffMouseStack(player, container, slot.slotNumber);
                for (int i = start; i < start + size; i++) {
                    if (container.inventorySlots.get(i).getHasStack()) {
                        getController().windowClick(container.windowId, i, 0, ClickType.PICKUP, player);
                        getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, player);
                    }
                }

                dropOffMouseStack(player, container);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CraftingInventory & IRecipeHolder> ItemStack findMatchingResult(T craftingInventory, ClientPlayerEntity player) {
        for (RecipeList recipeList : player.getRecipeBook().getRecipes()) {
            for (IRecipe<?> recipe : recipeList.getRecipes()) {
                if (recipe.getType() == IRecipeType.CRAFTING) {
                    IRecipe<CraftingInventory> craftingRecipe = (IRecipe<CraftingInventory>) recipe;
                    if (craftingRecipe.matches(craftingInventory, player.world)) {
                        return craftingRecipe.getCraftingResult(craftingInventory);
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
        ResourceLocation registryName = result.getItem().getRegistryName();
        return registryName != null && CraftingTweaksConfig.COMMON.compressBlacklist.get().contains(registryName.toString());
    }

    public void onItemCrafted(IInventory craftMatrix) {
        if (craftMatrix.getSizeInventory() <= 9) {
            for (int i = 0; i < lastCraftedMatrix.getSlots(); i++) {
                if (i < craftMatrix.getSizeInventory()) {
                    lastCraftedMatrix.setStackInSlot(i, craftMatrix.getStackInSlot(i).copy());
                } else {
                    lastCraftedMatrix.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
            hasLastCraftedMatrix = true;
        }
    }

    private boolean canRefillLastCrafted(TweakProvider<Container> provider, PlayerEntity player, Container container, int id) {
        return !provider.requiresServerSide() && hasLastCraftedMatrix;
    }

    public void refillLastCrafted(TweakProvider<Container> provider, PlayerEntity entityPlayer, Container container, int id, boolean fullStack) {
        if (!canRefillLastCrafted(provider, entityPlayer, container, id)) {
            return;
        }

        // Make sure the mouse is empty
        dropOffMouseStack(entityPlayer, container);

        int gridStart = provider.getCraftingGridStart(entityPlayer, container, id);
        int gridSize = provider.getCraftingGridSize(entityPlayer, container, id);

        // Now refill the grid
        for (int i = 0; i < lastCraftedMatrix.getSlots(); i++) {
            ItemStack itemStack = lastCraftedMatrix.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                // Search for this item in the inventory
                for (Slot slot : container.inventorySlots) {
                    if (slot.inventory instanceof PlayerInventory && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), itemStack) && ItemStack.areItemStackTagsEqual(slot.getStack(), itemStack)) {
                        getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                        getController().windowClick(container.windowId, gridStart + i, fullStack ? 0 : 1, ClickType.PICKUP, entityPlayer);
                        getController().windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                        break;
                    }
                }
            } else {
                if (container.inventorySlots.get(gridStart + i).getHasStack()) {
                    getController().windowClick(container.windowId, gridStart + i, 0, ClickType.PICKUP, entityPlayer);
                    if (!dropOffMouseStack(entityPlayer, container)) {
                        getController().windowClick(container.windowId, gridStart + i, 0, ClickType.PICKUP, entityPlayer);
                        return;
                    }
                }
            }
        }

        if (fullStack) {
            // Check if there's items missing and if so, try to find them in the crafting grid and distribute
            for (int i = 0; i < lastCraftedMatrix.getSlots(); i++) {
                ItemStack itemStack = lastCraftedMatrix.getStackInSlot(i);
                if (!itemStack.isEmpty() && !container.inventorySlots.get(gridStart + i).getHasStack()) {
                    for (int j = gridStart; j < gridStart + gridSize; j++) {
                        if (j == gridStart + i) {
                            continue;
                        }

                        ItemStack gridStack = container.inventorySlots.get(j).getStack();
                        if (gridStack.getCount() > 1 && ItemStack.areItemsEqual(gridStack, itemStack) && ItemStack.areItemStackTagsEqual(gridStack, itemStack)) {
                            getController().windowClick(container.windowId, j, 0, ClickType.PICKUP, entityPlayer);
                            getController().windowClick(container.windowId, gridStart + i, 1, ClickType.PICKUP, entityPlayer);
                            getController().windowClick(container.windowId, j, 0, ClickType.PICKUP, entityPlayer);
                            break;
                        }
                    }
                }
            }

            // Balance the grid
            balanceGrid(provider, entityPlayer, container, id);
        }

        dropOffMouseStack(entityPlayer, container);
    }
}
