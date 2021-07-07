package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ClearMessage {

    private final int id;
    private final boolean forced;

    public ClearMessage(int id, boolean forced) {
        this.id = id;
        this.forced = forced;
    }

    public static ClearMessage decode(FriendlyByteBuf buf) {
        int id = buf.readByte();
        boolean force = buf.readBoolean();
        return new ClearMessage(id, force);
    }

    public static void encode(ClearMessage message, FriendlyByteBuf buf) {
        buf.writeByte(message.id);
        buf.writeBoolean(message.forced);
    }

    public static void handle(ServerPlayer player, ClearMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu container = player.containerMenu;
        if (container != null) {
            TweakProvider<AbstractContainerMenu> tweakProvider = CraftingTweaksProviderManager.getProvider(container);
            if (tweakProvider != null) {
                tweakProvider.clearGrid(player, container, message.id, message.forced);
            }
        }
    }

}
