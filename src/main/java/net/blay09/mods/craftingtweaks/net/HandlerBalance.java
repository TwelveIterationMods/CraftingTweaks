package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerBalance implements IMessageHandler<MessageBalance, IMessage> {

    @Override
    public IMessage onMessage(final MessageBalance message, final MessageContext ctx) {
        CraftingTweaks.proxy.addScheduledTask(() -> {
            EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
            Container container = entityPlayer.openContainer;
            if(container != null) {
                TweakProvider tweakProvider = CraftingTweaks.instance.getProvider(container);
                if (tweakProvider != null) {
                    tweakProvider.balanceGrid(entityPlayer, container, message.id);
                }
            }
        });
        return null;
    }

}
