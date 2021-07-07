package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class BalanceMessage {

    private final int id;
    private final boolean spread;

    public BalanceMessage(int id, boolean spread) {
        this.id = id;
        this.spread = spread;
    }

    public static BalanceMessage decode(FriendlyByteBuf buf) {
        int id = buf.readByte();
        boolean spread = buf.readBoolean();
        return new BalanceMessage(id, spread);
    }

    public static void encode(BalanceMessage message, FriendlyByteBuf buf) {
        buf.writeByte(message.id);
        buf.writeBoolean(message.spread);
    }

    public static void handle(ServerPlayer player, BalanceMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu != null) {
            TweakProvider<AbstractContainerMenu> tweakProvider = CraftingTweaksProviderManager.getProvider(menu);
            if (tweakProvider != null) {
                if (message.spread) {
                    tweakProvider.spreadGrid(player, menu, message.id);
                } else {
                    tweakProvider.balanceGrid(player, menu, message.id);
                }
            }
        }
    }
}
