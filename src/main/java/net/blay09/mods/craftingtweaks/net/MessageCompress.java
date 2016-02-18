package net.blay09.mods.craftingtweaks.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageCompress implements IMessage {

    private int slotNumber;
    private boolean isDecompress;

    public MessageCompress() {
    }

    public MessageCompress(int slotNumber, boolean isDecompress) {
        this.slotNumber = slotNumber;
        this.isDecompress = isDecompress;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotNumber = buf.readInt();
        isDecompress = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotNumber);
        buf.writeBoolean(isDecompress);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isDecompress() {
        return isDecompress;
    }

}
