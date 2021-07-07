package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TransferStackMessage {

    private final int id;
    private final int slotNumber;

    public TransferStackMessage(int id, int slotNumber) {
        this.id = id;
        this.slotNumber = slotNumber;
    }

    public static void encode(TransferStackMessage message, FriendlyByteBuf buf) {
        buf.writeByte(message.id);
        buf.writeInt(message.slotNumber);
    }

    public static TransferStackMessage decode(FriendlyByteBuf buf) {
        int id = buf.readByte();
        int slotNumber = buf.readInt();
        return new TransferStackMessage(id, slotNumber);
    }

    public static void handle(ServerPlayer player, TransferStackMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu container = player.containerMenu;
        if (container == null || message.slotNumber < 0 || message.slotNumber >= container.slots.size()) {
            return;
        }

        TweakProvider<AbstractContainerMenu> tweakProvider = CraftingTweaksProviderManager.getProvider(container);
        if (tweakProvider == null) {
            return;
        }

        // Check if the slot can be transferred from. SlotCrafting is always blacklisted.
        Slot slot = container.slots.get(message.slotNumber);
        if (!tweakProvider.canTransferFrom(player, container, message.id, slot) || slot instanceof ResultSlot) {
            return;
        }

        ItemStack slotStack = slot.getItem();
        if (!slotStack.isEmpty() && slot.mayPickup(player)) {
            // Perform the grid transfer
            ItemStack oldStack = slotStack.copy();
            if (!tweakProvider.transferIntoGrid(player, container, message.id, slot)) {
                return;
            }

            // Notify the slot about the changes
            slot.onQuickCraft(slotStack, oldStack);
            if (slotStack.getCount() <= 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() != oldStack.getCount()) {
                slot.onTake(player, slotStack);
            }
        }
    }
}
