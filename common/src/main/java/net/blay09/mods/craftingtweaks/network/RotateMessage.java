package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RotateMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<RotateMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "rotate"));

    private final ResourceLocation id;
    private final boolean reverse;

    public RotateMessage(ResourceLocation id, boolean reverse) {
        this.id = id;
        this.reverse = reverse;
    }

    public static RotateMessage decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        boolean counterClockwise = buf.readBoolean();
        return new RotateMessage(id, counterClockwise);
    }

    public static void encode(FriendlyByteBuf buf, RotateMessage message) {
        buf.writeResourceLocation(message.id);
        buf.writeBoolean(message.reverse);
    }

    public static void handle(ServerPlayer player, RotateMessage message) {
        if (player == null) {
            return;
        }

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
