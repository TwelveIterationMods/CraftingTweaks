package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record CraftStackMessage(int slotNumber) implements CustomPacketPayload {

    public static CustomPacketPayload.Type<CraftStackMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CraftingTweaks.MOD_ID,
            "craft_stack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftStackMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CraftStackMessage::slotNumber,
            CraftStackMessage::new
    );

    public static void handle(ServerPlayer player, CraftStackMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null || message.slotNumber < 0 || message.slotNumber >= menu.slots.size()) {
            return;
        }

        Slot mouseSlot = menu.slots.get(message.slotNumber);
        ItemStack mouseStack = menu.getCarried();
        int maxTries = 64;
        while (maxTries > 0 && mouseSlot.hasItem() && (mouseStack.isEmpty() || mouseStack.getCount() + mouseSlot.getItem()
                .getCount() <= mouseStack.getMaxStackSize())) {
            menu.clicked(mouseSlot.index, 0, ClickType.PICKUP, player);
            mouseStack = menu.getCarried();
            maxTries--;
        }

        player.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, -1, menu.getCarried()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
