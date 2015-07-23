package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class HandlerBalance implements IMessageHandler<MessageBalance, IMessage> {

    @Override
    public IMessage onMessage(MessageBalance message, MessageContext ctx) {
        EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
        Container container = entityPlayer.openContainer;
        if(container != null) {
            TweakProvider tweakProvider = CraftingTweaks.instance.getProvider(container);
            if (tweakProvider != null) {
                tweakProvider.balanceGrid(entityPlayer, container);
            }
        }
        return null;
    }

}
