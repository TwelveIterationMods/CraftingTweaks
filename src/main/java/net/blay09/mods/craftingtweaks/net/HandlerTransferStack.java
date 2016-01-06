package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerTransferStack implements IMessageHandler<MessageTransferStack, IMessage> {

    @Override
    public IMessage onMessage(final MessageTransferStack message, final MessageContext ctx) {
        CraftingTweaks.proxy.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
                Container container = entityPlayer.openContainer;
                if(container != null && message.slotNumber >= 0 && message.slotNumber < container.inventorySlots.size()) {
                    TweakProvider tweakProvider = CraftingTweaks.instance.getProvider(container);
                    if (tweakProvider != null) {
                        Slot slot = container.inventorySlots.get(message.slotNumber);
                        if(!tweakProvider.canTransferFrom(entityPlayer, container, message.id, slot) || slot instanceof SlotCrafting) { // SlotCrafting is always blacklisted
                            return;
                        }
                        ItemStack slotStack = slot.getStack();
                        if(slotStack != null && slot.canTakeStack(entityPlayer)) {
                            ItemStack oldStack = slotStack.copy();
                            if(!tweakProvider.transferIntoGrid(entityPlayer, container, message.id, slotStack)) {
                                return;
                            }
                            slot.onSlotChange(slotStack, oldStack);
                            if(slotStack.stackSize <= 0) {
                                slot.putStack(null);
                            } else {
                                slot.onSlotChanged();
                            }
                            if(slotStack.stackSize == oldStack.stackSize) {
                                return;
                            }
                            slot.onPickupFromSlot(entityPlayer, slotStack);
                        }
                    }
                }
            }
        });
        return null;
    }

}
