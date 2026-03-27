package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record RotateMessage(Identifier id, boolean reverse) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RotateMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CraftingTweaks.MOD_ID,
            "rotate"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RotateMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            RotateMessage::id,
            ByteBufCodecs.BOOL,
            RotateMessage::reverse,
            RotateMessage::new
    );

    public static void handle(ServerPlayer player, RotateMessage message) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu != null) {
            CraftingTweaksProviderManager.getCraftingGrid(menu, message.id)
                    .ifPresent(grid -> grid.rotateHandler().rotateGrid(grid, player, menu, message.reverse));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
