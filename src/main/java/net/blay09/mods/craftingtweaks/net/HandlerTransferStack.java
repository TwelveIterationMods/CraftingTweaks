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

import javax.annotation.Nullable;

public class HandlerTransferStack implements IMessageHandler<MessageTransferStack, IMessage> {

    @Override
    @Nullable
    public IMessage onMessage(final MessageTransferStack message, final MessageContext ctx) {
        CraftingTweaks.proxy.addScheduledTask(() -> {
            EntityPlayer entityPlayer = ctx.getServerHandler().player;
            Container container = entityPlayer.openContainer;
            if(container != null && message.slotNumber >= 0 && message.slotNumber < container.inventorySlots.size()) {
                TweakProvider<Container> tweakProvider = CraftingTweaks.instance.getProvider(container);
                if (tweakProvider != null) {
                    Slot slot = container.inventorySlots.get(message.slotNumber);
                    if(!tweakProvider.canTransferFrom(entityPlayer, container, message.id, slot) || slot instanceof SlotCrafting) { // SlotCrafting is always blacklisted
                        return;
                    }
                    ItemStack slotStack = slot.getStack();
                    if(!slotStack.isEmpty() && slot.canTakeStack(entityPlayer)) {
                        ItemStack oldStack = slotStack.copy();
                        if(!tweakProvider.transferIntoGrid(entityPlayer, container, message.id, slot)) {
                            return;
                        }
                        slot.onSlotChange(slotStack, oldStack);
                        if(slotStack.getCount() <= 0) {
                            slot.putStack(ItemStack.EMPTY);
                        } else {
                            slot.onSlotChanged();
                        }
                        if(slotStack.getCount() == oldStack.getCount()) {
                            return;
                        }
                        slot.onTake(entityPlayer, slotStack);
                    }
                }
            }
        });
        return null;
    }

}
