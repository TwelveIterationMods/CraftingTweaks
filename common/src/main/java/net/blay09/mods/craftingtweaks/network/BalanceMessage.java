package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class BalanceMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<BalanceMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "balance"));

    private final ResourceLocation id;
    private final boolean spread;

    public BalanceMessage(ResourceLocation id, boolean spread) {
        this.id = id;
        this.spread = spread;
    }

    public static BalanceMessage decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        boolean spread = buf.readBoolean();
        return new BalanceMessage(id, spread);
    }

    public static void encode(FriendlyByteBuf buf, BalanceMessage message) {
        buf.writeResourceLocation(message.id);
        buf.writeBoolean(message.spread);
    }

    public static void handle(ServerPlayer player, BalanceMessage message) {
        if (player == null) {
            return;
        }

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
