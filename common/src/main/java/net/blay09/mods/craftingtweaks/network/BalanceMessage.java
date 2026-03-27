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

public record BalanceMessage(Identifier id, boolean spread) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BalanceMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "balance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BalanceMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            BalanceMessage::id,
            ByteBufCodecs.BOOL,
            BalanceMessage::spread,
            BalanceMessage::new
    );

    public static void handle(ServerPlayer player, BalanceMessage message) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu != null) {
            CraftingTweaksProviderManager.getCraftingGrid(menu, message.id).ifPresent(grid -> {
                if (message.spread) {
                    grid.balanceHandler().spreadGrid(grid, player, menu);
                } else {
                    grid.balanceHandler().balanceGrid(grid, player, menu);
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
