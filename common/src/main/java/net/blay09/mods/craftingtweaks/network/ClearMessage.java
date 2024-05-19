package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ClearMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<ClearMessage> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation("craftingtweaks", "clear"));

    private final ResourceLocation id;
    private final boolean forced;

    public ClearMessage(ResourceLocation id, boolean forced) {
        this.id = id;
        this.forced = forced;
    }

    public static ClearMessage decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        boolean force = buf.readBoolean();
        return new ClearMessage(id, force);
    }

    public static void encode(FriendlyByteBuf buf, ClearMessage message) {
        buf.writeResourceLocation(message.id);
        buf.writeBoolean(message.forced);
    }

    public static void handle(ServerPlayer player, ClearMessage message) {
        if (player == null) {
            return;
        }

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
