package net.blay09.mods.craftingtweaks.net;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.craftingtweaks.CompressType;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageCompress implements IMessage {

    private int slotNumber;
    private CompressType type;

    public MessageCompress() {
    }

    public MessageCompress(int slotNumber, CompressType type) {
        this.slotNumber = slotNumber;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotNumber = buf.readInt();
        type = CompressType.values()[buf.readByte()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotNumber);
        type = CompressType.values()[buf.readByte()];
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public CompressType getType() {
        return type;
    }
}
