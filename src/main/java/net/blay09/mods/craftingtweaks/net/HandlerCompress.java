package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.InventoryCraftingCompress;
import net.blay09.mods.craftingtweaks.InventoryCraftingDecompress;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class HandlerCompress implements IMessageHandler<MessageCompress, IMessage> {
    @Override
    public IMessage onMessage(MessageCompress message, MessageContext ctx) {
        EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
        Container container = entityPlayer.openContainer;
        if (container == null) {
            return null;
        }
        Slot mouseSlot = (Slot) container.inventorySlots.get(message.getSlotNumber());
        if(!(mouseSlot.inventory instanceof InventoryPlayer)) {
            return null;
        }
        ItemStack mouseStack = mouseSlot.getStack();
        if (mouseStack == null) {
            return null;
        }
        TweakProvider provider = CraftingTweaks.instance.getProvider(container);
        if (!CraftingTweaks.compressAnywhere && provider == null) {
            return null;
        }
        if (message.isDecompress()) {
            ItemStack result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingDecompress(container, mouseStack), entityPlayer.worldObj);
            if (result != null && mouseStack.stackSize >= 1) {
                if (entityPlayer.inventory.addItemStackToInventory(result)) {
                    mouseSlot.decrStackSize(1);
                }
            }
        } else {
            int size = provider != null ? provider.getCraftingGridSize(entityPlayer, container, 0) : 9;
            if (size == 9 && mouseStack.stackSize >= 9) {
                ItemStack result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 3, mouseStack), entityPlayer.worldObj);
                if (result != null) {
                    if (entityPlayer.inventory.addItemStackToInventory(result)) {
                        mouseSlot.decrStackSize(9);
                    }
                } else {
                    result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 2, mouseStack), entityPlayer.worldObj);
                    if (result != null) {
                        if (entityPlayer.inventory.addItemStackToInventory(result)) {
                            mouseSlot.decrStackSize(4);
                        }
                    }
                }
            } else if (size == 4 && mouseStack.stackSize >= 4) {
                ItemStack result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 2, mouseStack), entityPlayer.worldObj);
                if (result != null) {
                    if (entityPlayer.inventory.addItemStackToInventory(result)) {
                        mouseSlot.decrStackSize(4);
                    }
                }
            }
        }
        container.detectAndSendChanges();
        return null;
    }
}
