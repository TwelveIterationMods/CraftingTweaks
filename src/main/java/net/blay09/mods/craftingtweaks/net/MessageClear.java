package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageClear implements IMessage {

    private int id;
    private boolean force;

    public MessageClear() {}

    public MessageClear(int id, boolean force) {
        this.id = id;
        this.force = force;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readByte();
        force = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeBoolean(force);
    }

    public int getId() {
        return id;
    }

    public boolean isForce() {
        return force;
    }
}
