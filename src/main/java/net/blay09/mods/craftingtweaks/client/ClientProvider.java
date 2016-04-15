package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.blay09.mods.craftingtweaks.CompressType;
import net.blay09.mods.craftingtweaks.InventoryCraftingCompress;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import java.util.Collection;

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

    private PlayerControllerMP getController() {
        return Minecraft.getMinecraft().playerController;
    }

    private boolean canBalance(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public void balanceGrid(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id) {
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
                balanceSlots.put(itemStack.getUnlocalizedName() + "@" + itemStack.getItemDamage(), slot);
            }
        }
        for (String key : balanceSlots.keySet()) {
            Collection<Slot> slotList = balanceSlots.get(key);
            int average = 0;
            for (Slot slot : slotList) {
                average += slot.getStack().stackSize;
            }
            average = (int) Math.floor((float) average / (float) slotList.size());
            for (Slot slot : slotList) {
                if (slot.getHasStack() && slot.getStack().stackSize > average) {
                    // Pick up item from biggest stack
                    int mouseStackSize = slot.getStack().stackSize;
                    getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer); // func_187098_a

                    for (Slot otherSlot : slotList) {
                        if (slot == otherSlot || !otherSlot.getHasStack()) {
                            continue;
                        }
                        int otherStackSize = otherSlot.getStack().stackSize;
                        if (otherStackSize < average) {
                            while (otherStackSize < average && mouseStackSize > average) {
                                getController().func_187098_a(container.windowId, otherSlot.slotNumber, 1, ClickType.PICKUP, entityPlayer); // func_187098_a
                                mouseStackSize--;
                                otherStackSize++;
                            }
                        }
                    }

                    // Put the remaining stack back
                    getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer); // func_187098_a
                }
            }
        }
    }

    public void spreadGrid(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id) {
        while(true) {
            Slot biggestSlot = null;
            int biggestSlotSize = 1;
            int start = provider.getCraftingGridStart(entityPlayer, container, id);
            int size = provider.getCraftingGridSize(entityPlayer, container, id);
            for (int i = start; i < start + size; i++) {
                Slot slot = container.inventorySlots.get(i);
                ItemStack itemStack = slot.getStack();
                if (itemStack != null && itemStack.stackSize > biggestSlotSize) {
                    biggestSlot = slot;
                    biggestSlotSize = itemStack.stackSize;
                }
            }
            if (biggestSlot == null) {
                return;
            }
            boolean emptyBiggestSlot = false;
            getController().func_187098_a(container.windowId, biggestSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
            for (int i = start; i < start + size; i++) {
                ItemStack itemStack = container.inventorySlots.get(i).getStack();
                if (itemStack == null) {
                    if(biggestSlotSize > 1) {
                        getController().func_187098_a(container.windowId, i, 1, ClickType.PICKUP, entityPlayer);
                        biggestSlotSize--;
                    } else {
                        emptyBiggestSlot = true;
                    }
                }
            }
            getController().func_187098_a(container.windowId, biggestSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
            if(!emptyBiggestSlot) {
                break;
            }
        }
        balanceGrid(provider, entityPlayer, container, id);
    }

    private boolean canClear(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public void clearGrid(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id, boolean forced) {
        if (!canClear(provider, entityPlayer, container, id)) {
            return;
        }
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for (int i = start; i < start + size; i++) {
            getController().func_187098_a(container.windowId, i, 0, ClickType.QUICK_MOVE, entityPlayer);
            container.transferStackInSlot(entityPlayer, i);
            if(forced && container.inventorySlots.get(i).getHasStack()) {
                getController().func_187098_a(container.windowId, i, 0, ClickType.THROW, entityPlayer);
            }
        }
    }

    private boolean canRotate(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id) {
        return !provider.requiresServerSide() && provider.getCraftingGridSize(entityPlayer, container, id) == 9;
    }

    public void rotateGrid(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id, boolean counterClockwise) {
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
        getController().func_187098_a(container.windowId, startSlot, 0, ClickType.PICKUP, entityPlayer);
        int currentSlot = startSlot;
        do {
            currentSlot = startSlot + rotationHandler.rotateSlotId(currentSlot - startSlot, counterClockwise);
            getController().func_187098_a(container.windowId, currentSlot, 0, ClickType.PICKUP, entityPlayer);
        } while (currentSlot != startSlot);
    }

    private boolean rotateGridWithBuffer(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id, boolean counterClockwise) {
        int emptyBuffer = 0;
        int[] bufferSlot = new int[2];
        for (Object obj : container.inventorySlots) {
            Slot slot = (Slot) obj;
            if (slot.inventory instanceof InventoryPlayer && !slot.getHasStack()) {
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
            getController().func_187098_a(container.windowId, currentSlot, 0, ClickType.PICKUP, entityPlayer);
            getController().func_187098_a(container.windowId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, entityPlayer);
            emptyBuffer = (emptyBuffer + 1) % 2;
            getController().func_187098_a(container.windowId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, entityPlayer);
            getController().func_187098_a(container.windowId, currentSlot, 0, ClickType.PICKUP, entityPlayer);
            currentSlot = startSlot + rotationHandler.rotateSlotId(currentSlot - startSlot, counterClockwise);
        } while (currentSlot != startSlot);
        emptyBuffer = (emptyBuffer + 1) % 2;
        getController().func_187098_a(container.windowId, bufferSlot[emptyBuffer], 0, ClickType.PICKUP, entityPlayer);
        getController().func_187098_a(container.windowId, startSlot, 0, ClickType.PICKUP, entityPlayer);
        return true;
    }

    private boolean canTransfer(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public boolean transferIntoGrid(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, int id, Slot sourceSlot) {
        if (!canTransfer(provider, entityPlayer, container, id)) {
            return false;
        }
        if (!sourceSlot.getHasStack() || !sourceSlot.canTakeStack(entityPlayer) || !provider.canTransferFrom(entityPlayer, container, id, sourceSlot)) {
            return false;
        }
        if (!dropOffMouseStack(entityPlayer, container)) {
            return false;
        }
        getController().func_187098_a(container.windowId, sourceSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
        ItemStack mouseStack = entityPlayer.inventory.getItemStack();
        if (mouseStack == null) {
            return false;
        }
        boolean itemMoved = false;
        int firstEmptySlot = -1;
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for (int i = start; i < start + size; i++) {
            Slot craftSlot = container.inventorySlots.get(i);
            ItemStack craftStack = craftSlot.getStack();
            if (craftStack != null) {
                if (craftStack.isItemEqual(mouseStack) && ItemStack.areItemStackTagsEqual(craftStack, mouseStack)) {
                    int spaceLeft = Math.min(craftSlot.getSlotStackLimit(), craftStack.getMaxStackSize()) - craftStack.stackSize;
                    if (spaceLeft > 0) {
                        getController().func_187098_a(container.windowId, craftSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                        mouseStack = entityPlayer.inventory.getItemStack();
                        if (mouseStack == null) {
                            return true;
                        }
                    }
                }
            } else if (firstEmptySlot == -1) {
                firstEmptySlot = i;
            }
        }
        if (firstEmptySlot != -1) {
            getController().func_187098_a(container.windowId, firstEmptySlot, 0, ClickType.PICKUP, entityPlayer);
            itemMoved = true;
        }
        if (entityPlayer.inventory.getItemStack() != null) {
            getController().func_187098_a(container.windowId, sourceSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
        }
        dropOffMouseStack(entityPlayer, container);
        return itemMoved;
    }

    private boolean dropOffMouseStack(EntityPlayer entityPlayer, Container container) {
        return dropOffMouseStack(entityPlayer, container, -1);
    }

    private boolean dropOffMouseStack(EntityPlayer entityPlayer, Container container, int ignoreSlot) {
        if (entityPlayer.inventory.getItemStack() == null) {
            return true;
        }
        for (int i = 0; i < container.inventorySlots.size(); i++) {
            if(i == ignoreSlot) {
                continue;
            }
            Slot slot = container.inventorySlots.get(i);
            if (slot.inventory == entityPlayer.inventory) {
                ItemStack mouseItem = entityPlayer.inventory.getItemStack();
                ItemStack slotStack = slot.getStack();
                if (slotStack == null) {
                    getController().func_187098_a(container.windowId, i, 0, ClickType.PICKUP, entityPlayer);
                } else if (mouseItem.getItem() == slotStack.getItem() && (!slotStack.getHasSubtypes() || slotStack.getMetadata() == mouseItem.getMetadata()) && ItemStack.areItemStackTagsEqual(slotStack, mouseItem)) {
                    getController().func_187098_a(container.windowId, i, 0, ClickType.PICKUP, entityPlayer);
                }
                if (entityPlayer.inventory.getItemStack() == null) {
                    return true;
                }
            }
        }
        return entityPlayer.inventory.getItemStack() == null;
    }

    private void decompress(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, Slot mouseSlot, CompressType compressType) {
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
        for(Slot slot : container.inventorySlots) {
            if(compressType != CompressType.DECOMPRESS_ALL && slot != mouseSlot) {
                continue;
            }
            if(slot.inventory instanceof InventoryPlayer && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), mouseSlot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), mouseSlot.getStack())) {
                // Move stack to crafting grid
                getController().func_187098_a(container.windowId, mouseSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                getController().func_187098_a(container.windowId, start, 0, ClickType.PICKUP, entityPlayer);
                for (Slot resultSlot : container.inventorySlots) {
                    // Search for result slot and grab result
                    if (resultSlot instanceof SlotCrafting && resultSlot.getHasStack()) {
                        getController().func_187098_a(container.windowId, resultSlot.slotNumber, 0, decompressAll ? ClickType.QUICK_MOVE : ClickType.PICKUP, entityPlayer);
                        break;
                    }
                }
                dropOffMouseStack(entityPlayer, container, mouseSlot.slotNumber);
                // Take remaining stack back out of the crafting grid
                getController().func_187098_a(container.windowId, start, 0, ClickType.PICKUP, entityPlayer);
                getController().func_187098_a(container.windowId, mouseSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
            }
        }
    }

    public void compress(TweakProvider<Container> provider, EntityPlayer entityPlayer, Container container, Slot mouseSlot, CompressType compressType) {
        if(compressType == CompressType.DECOMPRESS_ALL || compressType == CompressType.DECOMPRESS_ONE || compressType == CompressType.DECOMPRESS_STACK) {
            decompress(provider, entityPlayer, container, mouseSlot, compressType);
            return;
        }
        if (!mouseSlot.getHasStack() || !canClear(provider, entityPlayer, container, 0)) {
            return;
        }
        boolean compressAll = compressType != CompressType.COMPRESS_ONE;
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
        for(Slot slot : container.inventorySlots) {
            if (compressType != CompressType.COMPRESS_ALL && slot != mouseSlot) {
                continue;
            }
            if (slot.inventory instanceof InventoryPlayer && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), mouseSlot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), mouseSlot.getStack())) {
                ItemStack result;
                ItemStack mouseStack = slot.getStack();
                if (size == 9 && mouseStack.stackSize >= 9) {
                    result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 3, mouseStack), entityPlayer.worldObj);
                    if (result != null) {
                        getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                        getController().func_187098_a(container.windowId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, entityPlayer);
                        for (int i = start; i < start + size; i++) {
                            getController().func_187098_a(container.windowId, i, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                        }
                        getController().func_187098_a(container.windowId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, entityPlayer);
                        getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                    } else {
                        result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 2, mouseStack), entityPlayer.worldObj);
                        if (result != null) {
                            getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                            getController().func_187098_a(container.windowId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, start, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, start + 1, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, start + 3, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, start + 4, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                        } else {
                            return;
                        }
                    }
                } else if (size >= 4 && mouseStack.stackSize >= 4) {
                    result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 2, mouseStack), entityPlayer.worldObj);
                    if (result != null) {
                        getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                        getController().func_187098_a(container.windowId, -999, getDragSplittingButton(0, 0), ClickType.QUICK_CRAFT, entityPlayer);
                        if(size == 4) {
                            for (int i = start; i < start + size; i++) {
                                getController().func_187098_a(container.windowId, i, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            }
                        } else {
                            getController().func_187098_a(container.windowId, start, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, start + 1, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, start + 3, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                            getController().func_187098_a(container.windowId, start + 4, getDragSplittingButton(1, 0), ClickType.QUICK_CRAFT, entityPlayer);
                        }
                        getController().func_187098_a(container.windowId, -999, getDragSplittingButton(2, 0), ClickType.QUICK_CRAFT, entityPlayer);
                        getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                    } else {
                        return;
                    }
                }
                for (Slot resultSlot : container.inventorySlots) {
                    if (resultSlot instanceof SlotCrafting && resultSlot.getHasStack()) {
                        getController().func_187098_a(container.windowId, resultSlot.slotNumber, 0, compressAll ? ClickType.QUICK_MOVE : ClickType.PICKUP, entityPlayer);
                        break;
                    }
                }
                dropOffMouseStack(entityPlayer, container, slot.slotNumber);
                for (int i = start; i < start + size; i++) {
                    if (container.inventorySlots.get(i).getHasStack()) {
                        getController().func_187098_a(container.windowId, i, 0, ClickType.PICKUP, entityPlayer);
                        getController().func_187098_a(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                    }
                }
                dropOffMouseStack(entityPlayer, container);
            }
        }
    }

    private static int getDragSplittingButton(int id, int limit) {
        return id & 3 | (limit & 3) << 2;
    }
}
