package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RotateMessage {

    private final int id;
    private final boolean counterClockwise;

    public RotateMessage(int id, boolean counterClockwise) {
        this.id = id;
        this.counterClockwise = counterClockwise;
    }

    public static RotateMessage decode(FriendlyByteBuf buf) {
        int id = buf.readByte();
        boolean counterClockwise = buf.readBoolean();
        return new RotateMessage(id, counterClockwise);
    }

    public static void encode(RotateMessage message, FriendlyByteBuf buf) {
        buf.writeByte(message.id);
        buf.writeBoolean(message.counterClockwise);
    }

    public static void handle(ServerPlayer player, RotateMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu container = player.containerMenu;
        if (container != null) {
            TweakProvider<AbstractContainerMenu> tweakProvider = CraftingTweaksProviderManager.getProvider(container);
            if (tweakProvider != null) {
                tweakProvider.rotateGrid(player, container, message.id, message.counterClockwise);
            }
        }
    }
}
