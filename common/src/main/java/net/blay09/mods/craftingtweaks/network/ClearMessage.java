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

public record ClearMessage(Identifier id, boolean forced) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClearMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "clear"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClearMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            ClearMessage::id,
            ByteBufCodecs.BOOL,
            ClearMessage::forced,
            ClearMessage::new
    );

    public static void handle(ServerPlayer player, ClearMessage message) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu != null) {
            CraftingTweaksProviderManager.getCraftingGrid(menu, message.id)
                    .ifPresent(grid -> grid.clearHandler().clearGrid(grid, player, menu, message.forced));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
