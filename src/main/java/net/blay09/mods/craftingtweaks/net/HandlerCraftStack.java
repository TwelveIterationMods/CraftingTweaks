package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerCraftStack implements IMessageHandler<MessageCraftStack, IMessage> {

	@Override
	@Nullable
	public IMessage onMessage(final MessageCraftStack message, final MessageContext ctx) {
		((WorldServer) ctx.getServerHandler().player.world).addScheduledTask(() -> {
			EntityPlayerMP entityPlayer = ctx.getServerHandler().player;
			Container container = entityPlayer.openContainer;
			if (container == null) {
				return;
			}

			if (message.getSlotNumber() < 0 || message.getSlotNumber() >= container.inventorySlots.size()) {
				return;
			}

			TweakProvider<Container> tweakProvider = CraftingTweaks.instance.getProvider(container);
			if (tweakProvider == null) {
				return;
			}

			Slot mouseSlot = container.inventorySlots.get(message.getSlotNumber());
			ItemStack mouseStack = entityPlayer.inventory.getItemStack();
			int maxTries = 64;
			while (maxTries > 0 && mouseSlot.getHasStack() && (mouseStack.isEmpty() || mouseStack.getCount() + mouseSlot.getStack().getCount() <= mouseStack.getMaxStackSize())) {
				container.slotClick(mouseSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
				mouseStack = entityPlayer.inventory.getItemStack();
				maxTries--;
			}
			entityPlayer.connection.sendPacket(new SPacketSetSlot(-1, -1, entityPlayer.inventory.getItemStack()));
		});
		return null;
	}

}
