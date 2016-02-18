package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class HandlerTransferStack implements IMessageHandler<MessageTransferStack, IMessage> {

    @Override
    public IMessage onMessage(final MessageTransferStack message, final MessageContext ctx) {
        EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
        Container container = entityPlayer.openContainer;
        if (container != null && message.slotNumber >= 0 && message.slotNumber < container.inventorySlots.size()) {
            TweakProvider tweakProvider = CraftingTweaks.instance.getProvider(container);
            if (tweakProvider != null) {
                Slot slot = (Slot) container.inventorySlots.get(message.slotNumber);
                if (!tweakProvider.canTransferFrom(entityPlayer, container, message.id, slot) || slot instanceof SlotCrafting) { // SlotCrafting is always blacklisted
                    return null;
                }
                ItemStack slotStack = slot.getStack();
                if (slotStack != null && slot.canTakeStack(entityPlayer)) {
                    ItemStack oldStack = slotStack.copy();
                    if (!tweakProvider.transferIntoGrid(entityPlayer, container, message.id, slot)) {
                        return null;
                    }
                    slot.onSlotChange(slotStack, oldStack);
                    if (slotStack.stackSize <= 0) {
                        slot.putStack(null);
                    } else {
                        slot.onSlotChanged();
                    }
                    if (slotStack.stackSize == oldStack.stackSize) {
                        return null;
                    }
                    slot.onPickupFromSlot(entityPlayer, slotStack);
                }
            }
        }
        return null;
    }

}
