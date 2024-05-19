package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.GridTransferHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TransferStackMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<TransferStackMessage> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation("craftingtweaks", "transfer_stack"));

    private final ResourceLocation id;
    private final int slotNumber;

    public TransferStackMessage(ResourceLocation id, int slotNumber) {
        this.id = id;
        this.slotNumber = slotNumber;
    }

    public static void encode(FriendlyByteBuf buf, TransferStackMessage message) {
        buf.writeResourceLocation(message.id);
        buf.writeInt(message.slotNumber);
    }

    public static TransferStackMessage decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        int slotNumber = buf.readInt();
        return new TransferStackMessage(id, slotNumber);
    }

    public static void handle(ServerPlayer player, TransferStackMessage message) {
        if (player == null) {
            return;
        }

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
