package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerClear implements IMessageHandler<MessageClear, IMessage> {

    @Override
    @Nullable
    public IMessage onMessage(final MessageClear message, final MessageContext ctx) {
        CraftingTweaks.proxy.addScheduledTask(() -> {
            EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
            Container container = entityPlayer.openContainer;
            if(container != null) {
                TweakProvider<Container> tweakProvider = CraftingTweaks.instance.getProvider(container);
                if (tweakProvider != null) {
                    tweakProvider.clearGrid(entityPlayer, container, message.getId(), message.isForced());
                }
            }
        });
        return null;
    }

}
