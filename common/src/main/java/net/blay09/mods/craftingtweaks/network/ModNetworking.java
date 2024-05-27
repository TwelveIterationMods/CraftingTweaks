package net.blay09.mods.craftingtweaks.network;


import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.allowClientAndServerOnly(CraftingTweaks.MOD_ID);

        networking.registerClientboundPacket(HelloMessage.TYPE, HelloMessage.class, HelloMessage::encode, HelloMessage::decode, HelloMessage::handle);
        networking.registerServerboundPacket(RotateMessage.TYPE, RotateMessage.class, RotateMessage::encode, RotateMessage::decode, RotateMessage::handle);
        networking.registerServerboundPacket(ClearMessage.TYPE, ClearMessage.class, ClearMessage::encode, ClearMessage::decode, ClearMessage::handle);
        networking.registerServerboundPacket(BalanceMessage.TYPE, BalanceMessage.class, BalanceMessage::encode, BalanceMessage::decode, BalanceMessage::handle);
        networking.registerServerboundPacket(TransferStackMessage.TYPE, TransferStackMessage.class, TransferStackMessage::encode, TransferStackMessage::decode, TransferStackMessage::handle);
        networking.registerServerboundPacket(CompressMessage.TYPE, CompressMessage.class, CompressMessage::encode, CompressMessage::decode, CompressMessage::handle);
        networking.registerServerboundPacket(CraftStackMessage.TYPE, CraftStackMessage.class, CraftStackMessage::encode, CraftStackMessage::decode, CraftStackMessage::handle);
        networking.registerServerboundPacket(RefillLastCraftedMessage.TYPE, RefillLastCraftedMessage.class, RefillLastCraftedMessage::encode, RefillLastCraftedMessage::decode, RefillLastCraftedMessage::handle);

        SyncConfigMessage.register(SyncCraftingTweaksConfigMessage.TYPE, SyncCraftingTweaksConfigMessage.class, SyncCraftingTweaksConfigMessage::new, CraftingTweaksConfigData.class, CraftingTweaksConfigData::new);
    }

}
