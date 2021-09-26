package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RotateMessage {

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

    public static void encode(RotateMessage message, FriendlyByteBuf buf) {
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
}
