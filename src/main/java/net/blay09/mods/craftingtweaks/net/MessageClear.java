package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageClear {

    private final int id;
    private final boolean forced;

    public MessageClear(int id, boolean forced) {
        this.id = id;
        this.forced = forced;
    }

    public static MessageClear decode(PacketBuffer buf) {
        int id = buf.readByte();
        boolean force = buf.readBoolean();
        return new MessageClear(id, force);
    }

    public static void encode(MessageClear message, PacketBuffer buf) {
        buf.writeByte(message.id);
        buf.writeBoolean(message.forced);
    }

    public static void handle(MessageClear message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            Container container = player.openContainer;
            if (container != null) {
                TweakProvider<Container> tweakProvider = CraftingTweaksProviderManager.getProvider(container);
                if (tweakProvider != null) {
                    tweakProvider.clearGrid(player, container, message.id, message.forced);
                }
            }
        });
    }

}
