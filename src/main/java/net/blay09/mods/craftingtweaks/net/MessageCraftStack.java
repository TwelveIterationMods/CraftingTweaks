package net.blay09.mods.craftingtweaks.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageCraftStack implements IMessage {

    private int slotNumber;

    public MessageCraftStack() {}

    public MessageCraftStack(int slotId) {
        this.slotNumber = slotId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotNumber = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(slotNumber);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

}
