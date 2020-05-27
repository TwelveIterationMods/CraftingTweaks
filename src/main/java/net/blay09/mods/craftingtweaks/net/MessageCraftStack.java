package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageCraftStack {

    private final int slotNumber;

    public MessageCraftStack(int slotId) {
        this.slotNumber = slotId;
    }

    public static MessageCraftStack decode(PacketBuffer buf) {
        int slotNumber = buf.readByte();
        return new MessageCraftStack(slotNumber);
    }

    public static void encode(MessageCraftStack message, PacketBuffer buf) {
        buf.writeByte(message.slotNumber);
    }

    public static void handle(MessageCraftStack message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            Container container = player.openContainer;
            if (container == null || message.slotNumber < 0 || message.slotNumber >= container.inventorySlots.size()) {
                return;
            }

            TweakProvider<Container> tweakProvider = CraftingTweaksProviderManager.getProvider(container);
            if (tweakProvider == null) {
                return;
            }

            Slot mouseSlot = container.inventorySlots.get(message.slotNumber);
            ItemStack mouseStack = player.inventory.getItemStack();
            int maxTries = 64;
            while (maxTries > 0 && mouseSlot.getHasStack() && (mouseStack.isEmpty() || mouseStack.getCount() + mouseSlot.getStack().getCount() <= mouseStack.getMaxStackSize())) {
                container.slotClick(mouseSlot.slotNumber, 0, ClickType.PICKUP, player);
                mouseStack = player.inventory.getItemStack();
                maxTries--;
            }

            player.connection.sendPacket(new SSetSlotPacket(-1, -1, player.inventory.getItemStack()));
        });
        context.setPacketHandled(true);
    }

}
