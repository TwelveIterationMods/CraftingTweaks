package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageCompress {

    private final int slotNumber;
    private final CompressType type;

    public MessageCompress(int slotNumber, CompressType type) {
        this.slotNumber = slotNumber;
        this.type = type;
    }

    public static MessageCompress decode(PacketBuffer buf) {
        int slotNumber = buf.readInt();
        CompressType type = CompressType.values()[buf.readByte()];
        return new MessageCompress(slotNumber, type);
    }

    public static void encode(MessageCompress message, PacketBuffer buf) {
        buf.writeInt(message.slotNumber);
        buf.writeByte(message.type.ordinal());
    }

    public static void handle(MessageCompress message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            Container container = player.openContainer;
            if (container == null) {
                return;
            }

            CompressType compressType = message.type;
            Slot mouseSlot = container.inventorySlots.get(message.slotNumber);
            if (!(mouseSlot.inventory instanceof PlayerInventory)) {
                return;
            }

            ItemStack mouseStack = mouseSlot.getStack();
            if (mouseStack.isEmpty()) {
                return;
            }

            TweakProvider<Container> provider = CraftingTweaksProviderManager.getProvider(container);
            if (!CraftingTweaksConfig.COMMON.compressAnywhere.get() && provider == null) {
                return;
            }

            if (compressType == CompressType.DECOMPRESS_ALL || compressType == CompressType.DECOMPRESS_STACK || compressType == CompressType.DECOMPRESS_ONE) {
                boolean decompressAll = compressType != CompressType.DECOMPRESS_ONE;
                // Perform decompression on all valid slots
                for (Slot slot : container.inventorySlots) {
                    if (compressType != CompressType.DECOMPRESS_ALL && slot != mouseSlot) {
                        continue;
                    }

                    if (slot.inventory instanceof PlayerInventory && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), mouseSlot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), mouseSlot.getStack())) {
                        ItemStack result = findMatchingResult(new InventoryCraftingDecompress(container, slot.getStack()), player);
                        if (!result.isEmpty() && !isBlacklisted(result) && !slot.getStack().isEmpty() && slot.getStack().getCount() >= 1) {
                            do {
                                if (player.inventory.addItemStackToInventory(result.copy())) {
                                    slot.decrStackSize(1);
                                } else {
                                    break;
                                }
                            } while (decompressAll && slot.getHasStack() && slot.getStack().getCount() >= 1);
                        }
                    }
                }
            } else {
                boolean compressAll = compressType != CompressType.COMPRESS_ONE;
                int size = provider != null ? provider.getCraftingGridSize(player, container, 0) : 9;
                // Perform decompression on all valid slots
                for (Slot slot : container.inventorySlots) {
                    if (compressType != CompressType.COMPRESS_ALL && slot != mouseSlot) {
                        continue;
                    }

                    if (slot.inventory instanceof PlayerInventory && slot.getHasStack() && ItemStack.areItemsEqual(slot.getStack(), mouseSlot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), mouseSlot.getStack())) {
                        if (size == 9 && !slot.getStack().isEmpty() && slot.getStack().getCount() >= 9) {
                            ItemStack result = findMatchingResult(new InventoryCraftingCompress(container, 3, slot.getStack()), player);
                            if (!result.isEmpty() && !isBlacklisted(result)) {
                                do {
                                    if (player.inventory.addItemStackToInventory(result.copy())) {
                                        slot.decrStackSize(9);
                                    } else {
                                        break;
                                    }
                                }
                                while (compressAll && slot.getHasStack() && slot.getStack().getCount() >= 9);
                            } else {
                                result = findMatchingResult(new InventoryCraftingCompress(container, 2, slot.getStack()), player);
                                if (!result.isEmpty() && !isBlacklisted(result)) {
                                    do {
                                        if (player.inventory.addItemStackToInventory(result.copy())) {
                                            slot.decrStackSize(4);
                                        } else {
                                            break;
                                        }
                                    }
                                    while (compressAll && slot.getHasStack() && slot.getStack().getCount() >= 4);
                                }
                            }
                        } else if (size >= 4 && !slot.getStack().isEmpty() && slot.getStack().getCount() >= 4) {
                            ItemStack result = findMatchingResult(new InventoryCraftingCompress(container, 2, slot.getStack()), player);
                            if (!result.isEmpty() && !isBlacklisted(result)) {
                                do {
                                    if (player.inventory.addItemStackToInventory(result.copy())) {
                                        slot.decrStackSize(4);
                                    } else {
                                        break;
                                    }
                                }
                                while (compressAll && slot.getHasStack() && slot.getStack().getCount() >= 4);
                            }
                        }
                    }
                }
            }
            container.detectAndSendChanges();
        });
        context.setPacketHandled(true);
    }

    private static <T extends CraftingInventory & IRecipeHolder> ItemStack findMatchingResult(T craftingInventory, ServerPlayerEntity player) {
        RecipeManager recipeManager = Objects.requireNonNull(player.getServer()).getRecipeManager();
        IRecipe<CraftingInventory> recipe = recipeManager.getRecipe(IRecipeType.CRAFTING, craftingInventory, player.world).orElse(null);
        if (recipe != null && craftingInventory.canUseRecipe(player.world, player, recipe)) {
            return recipe.getCraftingResult(craftingInventory);
        }

        return ItemStack.EMPTY;
    }

    private static boolean isBlacklisted(ItemStack result) {
        ResourceLocation registryName = result.getItem().getRegistryName();
        return registryName != null && CraftingTweaksConfig.COMMON.compressBlacklist.get().contains(registryName.toString());
    }
}
