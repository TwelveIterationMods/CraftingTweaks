package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageBalance {

    private final int id;
    private final boolean spread;

    public MessageBalance(int id, boolean spread) {
        this.id = id;
        this.spread = spread;
    }

    public static MessageBalance decode(PacketBuffer buf) {
        int id = buf.readByte();
        boolean spread = buf.readBoolean();
        return new MessageBalance(id, spread);
    }

    public static void encode(MessageBalance message, PacketBuffer buf) {
        buf.writeByte(message.id);
        buf.writeBoolean(message.spread);
    }

    public static void handle(MessageBalance message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            EntityPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            Container container = player.openContainer;
            if (container != null) {
                TweakProvider<Container> tweakProvider = CraftingTweaksProviderManager.getProvider(container);
                if (tweakProvider != null) {
                    if (message.spread) {
                        tweakProvider.spreadGrid(player, container, message.id);
                    } else {
                        tweakProvider.balanceGrid(player, container, message.id);
                    }
                }
            }
        });
    }
}
