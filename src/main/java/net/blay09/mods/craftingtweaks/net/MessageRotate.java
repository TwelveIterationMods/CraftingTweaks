package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRotate {

    private final int id;
    private final boolean counterClockwise;

    public MessageRotate(int id, boolean counterClockwise) {
        this.id = id;
        this.counterClockwise = counterClockwise;
    }

    public static MessageRotate decode(PacketBuffer buf) {
        int id = buf.readByte();
        boolean counterClockwise = buf.readBoolean();
        return new MessageRotate(id, counterClockwise);
    }

    public static void encode(MessageRotate message, PacketBuffer buf) {
        buf.writeByte(message.id);
        buf.writeBoolean(message.counterClockwise);
    }

    public static void handle(MessageRotate message, Supplier<NetworkEvent.Context> contextSupplier) {
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
                    tweakProvider.rotateGrid(player, container, message.id, message.counterClockwise);
                }
            }
        });
    }
}
