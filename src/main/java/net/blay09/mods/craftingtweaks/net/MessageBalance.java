package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageBalance implements IMessage {

    private int id;
    private boolean spread;

    public MessageBalance() {}

    public MessageBalance(int id, boolean spread) {
        this.id = id;
        this.spread = spread;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readByte();
        spread = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeBoolean(spread);
    }

    public int getId() {
        return id;
    }

    public boolean isSpread() {
        return spread;
    }
}
