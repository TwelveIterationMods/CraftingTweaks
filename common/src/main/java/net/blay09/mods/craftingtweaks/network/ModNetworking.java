package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.craftingtweaks.CraftingTweaks;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.allowClientAndServerOnly(CraftingTweaks.MOD_ID);

        networking.registerClientboundPacket(HelloMessage.TYPE, HelloMessage.class, HelloMessage.STREAM_CODEC, HelloMessage::handle);
        networking.registerServerboundPacket(RotateMessage.TYPE, RotateMessage.class, RotateMessage.STREAM_CODEC, RotateMessage::handle);
        networking.registerServerboundPacket(ClearMessage.TYPE, ClearMessage.class, ClearMessage.STREAM_CODEC, ClearMessage::handle);
        networking.registerServerboundPacket(BalanceMessage.TYPE, BalanceMessage.class, BalanceMessage.STREAM_CODEC, BalanceMessage::handle);
        networking.registerServerboundPacket(TransferStackMessage.TYPE, TransferStackMessage.class, TransferStackMessage.STREAM_CODEC, TransferStackMessage::handle);
        networking.registerServerboundPacket(CompressMessage.TYPE, CompressMessage.class, CompressMessage.STREAM_CODEC, CompressMessage::handle);
        networking.registerServerboundPacket(CraftStackMessage.TYPE, CraftStackMessage.class, CraftStackMessage.STREAM_CODEC, CraftStackMessage::handle);
        networking.registerServerboundPacket(RefillLastCraftedMessage.TYPE, RefillLastCraftedMessage.class, RefillLastCraftedMessage.STREAM_CODEC, RefillLastCraftedMessage::handle);
    }

}
