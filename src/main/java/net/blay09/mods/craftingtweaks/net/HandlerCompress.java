package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CompressType;
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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerCompress implements IMessageHandler<MessageCompress, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(MessageCompress message, MessageContext ctx) {
        CraftingTweaks.proxy.addScheduledTask(() -> {
            EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
            Container container = entityPlayer.openContainer;
            if (container == null) {
                return;
            }
            CompressType compressType = message.getType();
            Slot mouseSlot = container.inventorySlots.get(message.getSlotNumber());
            if (!(mouseSlot.inventory instanceof InventoryPlayer)) {
                return;
            }
            ItemStack mouseStack = mouseSlot.getStack();
            if (mouseStack.func_190926_b()) {
                return;
            }
            TweakProvider<Container> provider = CraftingTweaks.instance.getProvider(container);
            if (!CraftingTweaks.compressAnywhere && provider == null) {
                return;
            }
            if (compressType == CompressType.DECOMPRESS_ALL || compressType == CompressType.DECOMPRESS_STACK || compressType == CompressType.DECOMPRESS_ONE) {
                boolean decompressAll = compressType != CompressType.DECOMPRESS_ONE;
                // Perform decompression on all valid slots
                for(Slot slot : container.inventorySlots) {
                    if (compressType != CompressType.DECOMPRESS_ALL && slot != mouseSlot) {
                        continue;
                    }
                    if (slot.inventory instanceof InventoryPlayer && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), mouseSlot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), mouseSlot.getStack())) {
                        ItemStack result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingDecompress(container, slot.getStack()), entityPlayer.world);
                        if (!result.func_190926_b() && !isBlacklisted(result) && !slot.getStack().func_190926_b() && slot.getStack().func_190916_E() >= 1) {
                            do {
                                if (entityPlayer.inventory.addItemStackToInventory(result.copy())) {
                                    slot.decrStackSize(1);
                                } else {
                                    break;
                                }
                            } while (decompressAll && slot.getHasStack() && slot.getStack().func_190916_E() >= 1);
                        }
                    }
                }
            } else {
                boolean compressAll = compressType != CompressType.COMPRESS_ONE;
                int size = provider != null ? provider.getCraftingGridSize(entityPlayer, container, 0) : 9;
                // Perform decompression on all valid slots
                for(Slot slot : container.inventorySlots) {
                    if (compressType != CompressType.COMPRESS_ALL && slot != mouseSlot) {
                        continue;
                    }
                    if (slot.inventory instanceof InventoryPlayer && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), mouseSlot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), mouseSlot.getStack())) {
                        if (size == 9 && !slot.getStack().func_190926_b() && slot.getStack().func_190916_E() >= 9) {
                            ItemStack result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 3, slot.getStack()), entityPlayer.world);
                            if (!result.func_190926_b() && !isBlacklisted(result)) {
                                do {
                                    if (entityPlayer.inventory.addItemStackToInventory(result.copy())) {
                                        slot.decrStackSize(9);
                                    } else {
                                        break;
                                    }
                                }
                                while (compressAll && slot.getHasStack() && slot.getStack().func_190916_E() >= 9);
                            } else {
                                result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 2, slot.getStack()), entityPlayer.world);
                                if (!result.func_190926_b() && !isBlacklisted(result)) {
                                    do {
                                        if (entityPlayer.inventory.addItemStackToInventory(result.copy())) {
                                            slot.decrStackSize(4);
                                        } else {
                                            break;
                                        }
                                    }
                                    while (compressAll && slot.getHasStack() && slot.getStack().func_190916_E() >= 4);
                                }
                            }
                        } else if (size >= 4 && !slot.getStack().func_190926_b() && slot.getStack().func_190916_E() >= 4) {
                            ItemStack result = CraftingManager.getInstance().findMatchingRecipe(new InventoryCraftingCompress(container, 2, slot.getStack()), entityPlayer.world);
                            if (!result.func_190926_b() && !isBlacklisted(result)) {
                                do {
                                    if (entityPlayer.inventory.addItemStackToInventory(result.copy())) {
                                        slot.decrStackSize(4);
                                    } else {
                                        break;
                                    }
                                }
                                while (compressAll && slot.getHasStack() && slot.getStack().func_190916_E() >= 4);
                            }
                        }
                    }
                }
            }
            container.detectAndSendChanges();
        });
        return null;
    }

    private boolean isBlacklisted(ItemStack result) {
        return CraftingTweaks.compressBlacklist.contains(result.getItem().getRegistryName().toString());
    }
}
