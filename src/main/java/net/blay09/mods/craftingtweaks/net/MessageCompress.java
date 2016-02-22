package net.blay09.mods.craftingtweaks.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageCompress implements IMessage {

    private int slotNumber;
    private boolean isDecompress;
    private boolean compressAll;

    public MessageCompress() {
    }

    public MessageCompress(int slotNumber, boolean isDecompress, boolean compressAll) {
        this.slotNumber = slotNumber;
        this.isDecompress = isDecompress;
        this.compressAll = compressAll;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotNumber = buf.readInt();
        isDecompress = buf.readBoolean();
        compressAll = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotNumber);
        buf.writeBoolean(isDecompress);
        buf.writeBoolean(compressAll);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isDecompress() {
        return isDecompress;
    }

    public boolean isCompressAll() {
        return compressAll;
    }
}
