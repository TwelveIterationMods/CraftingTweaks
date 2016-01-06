package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class HandlerTransferStack implements IMessageHandler<MessageTransferStack, IMessage> {

    @Override
    public IMessage onMessage(MessageTransferStack message, MessageContext ctx) {
        EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
        Container container = entityPlayer.openContainer;
        if(container != null && message.slotNumber >= 0 && message.slotNumber < container.inventorySlots.size()) {
            TweakProvider tweakProvider = CraftingTweaks.instance.getProvider(container);
            if (tweakProvider != null) {
                Slot slot = (Slot) container.inventorySlots.get(message.slotNumber);
                if(slot.inventory != entityPlayer.inventory) {
                    return null;
                }
                ItemStack itemStack = slot.getStack();
                if(itemStack != null && slot.canTakeStack(entityPlayer)) {
                    ItemStack restStack = tweakProvider.transferIntoGrid(entityPlayer, container, message.id, itemStack);
                    if(restStack == null) {
                        restStack = itemStack.copy();
                        restStack.stackSize = 0;
                    }
                    if(restStack.stackSize != itemStack.stackSize) {
                        slot.onSlotChange(restStack, itemStack);
                        ItemStack movedStack = itemStack.copy();
                        movedStack.stackSize -= restStack.stackSize;
                        slot.onPickupFromSlot(entityPlayer, movedStack);
                    }
                    slot.putStack(restStack);
                    container.detectAndSendChanges();
                }
            }
        }
        return null;
    }

}
