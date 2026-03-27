package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.GridTransferHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record TransferStackMessage(Identifier id, int slotNumber) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TransferStackMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "transfer_stack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TransferStackMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            TransferStackMessage::id,
            ByteBufCodecs.INT,
            TransferStackMessage::slotNumber,
            TransferStackMessage::new
    );

    public static void handle(ServerPlayer player, TransferStackMessage message) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null || message.slotNumber < 0 || message.slotNumber >= menu.slots.size()) {
            return;
        }

        CraftingTweaksProviderManager.getCraftingGrid(menu, message.id).ifPresent(grid -> {
            GridTransferHandler<AbstractContainerMenu> transferHandler = grid.transferHandler();
            // Check if the slot can be transferred from. SlotCrafting is always blacklisted.
            Slot slot = menu.slots.get(message.slotNumber);
            if (!transferHandler.canTransferFrom(player, menu, slot, grid) || slot instanceof ResultSlot) {
                return;
            }

            ItemStack slotStack = slot.getItem();
            if (!slotStack.isEmpty() && slot.mayPickup(player)) {
                // Perform the grid transfer
                ItemStack oldStack = slotStack.copy();
                if (!transferHandler.transferIntoGrid(grid, player, menu, slot)) {
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
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
