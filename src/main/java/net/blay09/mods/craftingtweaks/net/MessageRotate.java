package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageRotate implements IMessage {

    private int id;
    private boolean counterClockwise;

    public MessageRotate() {}

    public MessageRotate(int id, boolean counterClockwise) {
        this.id = id;
        this.counterClockwise = counterClockwise;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readByte();
        counterClockwise = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeBoolean(counterClockwise);
    }

    public int getId() {
        return id;
    }

    public boolean isCounterClockwise() {
        return counterClockwise;
    }
}
