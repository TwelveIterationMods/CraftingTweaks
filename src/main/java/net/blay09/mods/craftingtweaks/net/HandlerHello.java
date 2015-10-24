package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.blay09.mods.craftingtweaks.CraftingTweaks;

public class HandlerHello implements IMessageHandler<MessageHello, IMessage> {

    @Override
    public IMessage onMessage(MessageHello message, MessageContext ctx) {
        CraftingTweaks.proxy.receivedHello(ctx.side == Side.SERVER ? ctx.getServerHandler().playerEntity : null);
        return ctx.side == Side.CLIENT ? new MessageHello(NetworkHandler.PROTOCOL_VERSION) : null;
    }

}
