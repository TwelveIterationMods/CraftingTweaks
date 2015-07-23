package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageBalance implements IMessage {

    public int id;

    public MessageBalance() {}

    public MessageBalance(int id) {
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(id);
    }

}
