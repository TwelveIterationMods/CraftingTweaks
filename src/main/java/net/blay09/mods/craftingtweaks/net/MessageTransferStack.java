package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTransferStack {

    private final int id;
    private final int slotNumber;

    public MessageTransferStack(int id, int slotNumber) {
        this.id = id;
        this.slotNumber = slotNumber;
    }

    public static void encode(MessageTransferStack message, PacketBuffer buf) {
        buf.writeByte(message.id);
        buf.writeInt(message.slotNumber);
    }

    public static MessageTransferStack decode(PacketBuffer buf) {
        int id = buf.readByte();
        int slotNumber = buf.readInt();
        return new MessageTransferStack(id, slotNumber);
    }

    public static void handle(MessageTransferStack message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            EntityPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            Container container = player.openContainer;
            if (container == null || message.slotNumber < 0 || message.slotNumber >= container.inventorySlots.size()) {
                return;
            }

            TweakProvider<Container> tweakProvider = CraftingTweaksProviderManager.getProvider(container);
            if (tweakProvider == null) {
                return;
            }

            // Check if the slot can be transferred from. SlotCrafting is always blacklisted.
            Slot slot = container.inventorySlots.get(message.slotNumber);
            if (!tweakProvider.canTransferFrom(player, container, message.id, slot) || slot instanceof SlotCrafting) {
                return;
            }

            ItemStack slotStack = slot.getStack();
            if (!slotStack.isEmpty() && slot.canTakeStack(player)) {
                // Perform the grid transfer
                ItemStack oldStack = slotStack.copy();
                if (!tweakProvider.transferIntoGrid(player, container, message.id, slot)) {
                    return;
                }

                // Notify the slot about the changes
                slot.onSlotChange(slotStack, oldStack);
                if (slotStack.getCount() <= 0) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }

                if (slotStack.getCount() != oldStack.getCount()) {
                    slot.onTake(player, slotStack);
                }
            }
        });
    }
}
