package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageHello implements IMessage {

    private int version;

    public MessageHello() {
    }

    public MessageHello(int version) {
        this.version = version;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        version = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(version);
    }
}
