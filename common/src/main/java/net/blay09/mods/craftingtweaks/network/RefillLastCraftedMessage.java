package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record RefillLastCraftedMessage(Identifier id, boolean stack) implements CustomPacketPayload {

    public static Type<RefillLastCraftedMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "refill_last_crafted"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RefillLastCraftedMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            RefillLastCraftedMessage::id,
            ByteBufCodecs.BOOL,
            RefillLastCraftedMessage::stack,
            RefillLastCraftedMessage::new
    );

    public static void handle(ServerPlayer player, RefillLastCraftedMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu != null) {
            CraftingTweaksProviderManager.getCraftingGrid(menu, message.id)
                    .ifPresent(grid -> grid.refillHandler().refillLastCrafted(grid, player, menu, message.stack));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
