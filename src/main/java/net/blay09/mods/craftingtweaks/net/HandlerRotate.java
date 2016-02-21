package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRotate implements IMessageHandler<MessageRotate, IMessage> {

    @Override
    public IMessage onMessage(final MessageRotate message, final MessageContext ctx) {
        CraftingTweaks.proxy.addScheduledTask(() -> {
            EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
            Container container = entityPlayer.openContainer;
            if(container != null) {
                TweakProvider tweakProvider = CraftingTweaks.instance.getProvider(container);
                if (tweakProvider != null) {
                    tweakProvider.rotateGrid(entityPlayer, container, message.getId(), message.isCounterClockwise());
                }
            }
        });
        return null;
    }

}
