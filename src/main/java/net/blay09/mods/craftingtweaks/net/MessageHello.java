package net.blay09.mods.craftingtweaks.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
