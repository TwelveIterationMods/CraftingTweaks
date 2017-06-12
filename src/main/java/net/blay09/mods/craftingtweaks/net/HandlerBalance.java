package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerBalance implements IMessageHandler<MessageBalance, IMessage> {

    @Override
    @Nullable
    public IMessage onMessage(final MessageBalance message, final MessageContext ctx) {
        ((WorldServer) ctx.getServerHandler().player.world).addScheduledTask(() -> {
            EntityPlayer entityPlayer = ctx.getServerHandler().player;
            Container container = entityPlayer.openContainer;
            if(container != null) {
                TweakProvider<Container> tweakProvider = CraftingTweaks.instance.getProvider(container);
                if (tweakProvider != null) {
                    if(message.isSpread()) {
                        tweakProvider.spreadGrid(entityPlayer, container, message.getId());
                    } else {
                        tweakProvider.balanceGrid(entityPlayer, container, message.getId());
                    }
                }
            }
        });
        return null;
    }

}
