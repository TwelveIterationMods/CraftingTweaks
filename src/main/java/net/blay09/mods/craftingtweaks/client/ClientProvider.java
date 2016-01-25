package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientProvider {

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

    public PlayerControllerMP getController() {
        return Minecraft.getMinecraft().playerController;
    }

    public boolean canBalance(TweakProvider provider, EntityPlayer entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public void balanceGrid(TweakProvider provider, EntityPlayer entityPlayer, Container container, int id) {
        if(!canBalance(provider, entityPlayer, container, id)) {
            return;
        }
        Multimap<String, Slot> balanceSlots = ArrayListMultimap.create();
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for(int i = start; i < start + size; i++) {
            Slot slot = container.inventorySlots.get(i);
            if(slot.getHasStack()) {
                ItemStack itemStack = slot.getStack();
                balanceSlots.put(itemStack.getUnlocalizedName() + "@" + itemStack.getItemDamage(), slot);
            }
        }
        for(String key : balanceSlots.keySet()) {
            Collection<Slot> slotList = balanceSlots.get(key);
            int average = 0;
            for(Slot slot : slotList) {
                average += slot.getStack().stackSize;
            }
            average = (int) Math.ceil(average / (float) slotList.size());
            for(Slot slot : slotList) {
                if(slot.getHasStack() && slot.getStack().stackSize > average) {
                    // Pick up item from biggest stack
                    int mouseStackSize = slot.getStack().stackSize;
                    getController().windowClick(container.windowId, slot.slotNumber, 0, 0, entityPlayer);

                    for(Slot otherSlot : slotList) {
                        if(slot == otherSlot || !otherSlot.getHasStack()) {
                            continue;
                        }
                        int otherStackSize = otherSlot.getStack().stackSize;
                        if(otherStackSize < average) {
                            while(otherStackSize < average && mouseStackSize > average) {
                                getController().windowClick(container.windowId, otherSlot.slotNumber, 1, 0, entityPlayer);
                                mouseStackSize--;
                                otherStackSize++;
                            }
                        }
                    }

                    // Put the remaining stack back
                    getController().windowClick(container.windowId, slot.slotNumber, 0, 0, entityPlayer);
                }
            }
        }
    }

    public boolean canClear(TweakProvider provider, EntityPlayer entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public void clearGrid(TweakProvider provider, EntityPlayer entityPlayer, Container container, int id) {
        if(!canClear(provider, entityPlayer, container, id)) {
            return;
        }
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for(int i = start; i < start + size; i++) {
            getController().windowClick(container.windowId, i, 0, 1, entityPlayer);
            container.transferStackInSlot(entityPlayer, i);
        }
    }

    public boolean canRotate(TweakProvider provider, EntityPlayer entityPlayer, Container container, int id) {
        return !provider.requiresServerSide() && provider.getCraftingGridSize(entityPlayer, container, id) == 9;
    }

    public void rotateGrid(TweakProvider provider, EntityPlayer entityPlayer, Container container, int id) {
        if(!canRotate(provider, entityPlayer, container, id)) {
            return;
        }
        if(!dropOffMouseStack(entityPlayer, container)) {
            return;
        }
        int startSlot = provider.getCraftingGridStart(entityPlayer, container, id);
        getController().windowClick(container.windowId, startSlot, 0, 0, entityPlayer);
        int currentSlot = startSlot;
        do {
            currentSlot = startSlot + rotationHandler.rotateSlotId(currentSlot - startSlot);
            getController().windowClick(container.windowId, currentSlot, 0, 0, entityPlayer);
        } while(currentSlot != startSlot);
    }

    public boolean canTransfer(TweakProvider provider, EntityPlayer entityPlayer, Container container, int id) {
        return !provider.requiresServerSide();
    }

    public boolean transferIntoGrid(TweakProvider provider, EntityPlayer entityPlayer, Container container, int id, Slot sourceSlot) {
        if(!canTransfer(provider, entityPlayer, container, id)) {
            return false;
        }
        if(!sourceSlot.getHasStack() || !sourceSlot.canTakeStack(entityPlayer) || !provider.canTransferFrom(entityPlayer, container, id, sourceSlot)) {
            return false;
        }
        if(!dropOffMouseStack(entityPlayer, container)) {
            return false;
        }
        getController().windowClick(container.windowId, sourceSlot.slotNumber, 0, 0, entityPlayer);
        ItemStack mouseStack = entityPlayer.inventory.getItemStack();
        if(mouseStack == null) {
            return false;
        }
        boolean itemMoved = false;
        int firstEmptySlot = -1;
        int start = provider.getCraftingGridStart(entityPlayer, container, id);
        int size = provider.getCraftingGridSize(entityPlayer, container, id);
        for(int i = start; i < start + size; i++) {
            Slot craftSlot = container.inventorySlots.get(i);
            ItemStack craftStack = craftSlot.getStack();
            if(craftStack != null) {
                if(craftStack.isItemEqual(mouseStack) && ItemStack.areItemStackTagsEqual(craftStack, mouseStack)) {
                    int spaceLeft = Math.min(craftSlot.getSlotStackLimit(), craftStack.getMaxStackSize()) - craftStack.stackSize;
                    if(spaceLeft > 0) {
                        getController().windowClick(container.windowId, craftSlot.slotNumber, 0, 0, entityPlayer);
                        mouseStack = entityPlayer.inventory.getItemStack();
                        if(mouseStack == null) {
                            return true;
                        }
                    }
                }
            } else if(firstEmptySlot == -1) {
                firstEmptySlot = i;
            }
        }
        if(firstEmptySlot != -1) {
            getController().windowClick(container.windowId, firstEmptySlot, 0, 0, entityPlayer);
            itemMoved = true;
        }
        if(entityPlayer.inventory.getItemStack() != null) {
            getController().windowClick(container.windowId, sourceSlot.slotNumber, 0, 0, entityPlayer);
        }
        dropOffMouseStack(entityPlayer, container);
        return itemMoved;
    }

    public boolean dropOffMouseStack(EntityPlayer entityPlayer, Container container) {
        if(entityPlayer.inventory.getItemStack() == null) {
            return true;
        }
        for(int i = 0; i < container.inventorySlots.size(); i++) {
            Slot slot = container.inventorySlots.get(i);
            if(slot.inventory == entityPlayer.inventory) {
                ItemStack mouseItem = entityPlayer.inventory.getItemStack();
                ItemStack slotStack = slot.getStack();
                if(slotStack == null) {
                    getController().windowClick(container.windowId, i, 1, 0, entityPlayer);
                } else if(mouseItem.getItem() == slotStack.getItem() && (!slotStack.getHasSubtypes() || slotStack.getMetadata() == mouseItem.getMetadata()) && ItemStack.areItemStackTagsEqual(slotStack, mouseItem)) {
                    getController().windowClick(container.windowId, i, 1, 0, entityPlayer);
                }
                if(entityPlayer.inventory.getItemStack() == null) {
                    return true;
                }
            }
        }
        return entityPlayer.inventory.getItemStack() == null;
    }
}
