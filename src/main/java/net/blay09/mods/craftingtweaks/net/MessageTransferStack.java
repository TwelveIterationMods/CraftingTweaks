package net.blay09.mods.craftingtweaks.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageTransferStack implements IMessage {

    public int id;
    public int slotNumber;

    public MessageTransferStack() {}

    public MessageTransferStack(int id, int slotNumber) {
        this.id = id;
        this.slotNumber = slotNumber;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readByte();
        slotNumber = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeInt(slotNumber);
    }

}
