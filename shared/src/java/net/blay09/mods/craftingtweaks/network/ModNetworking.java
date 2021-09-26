package net.blay09.mods.craftingtweaks.network;


import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerClientboundPacket(id("hello"), HelloMessage.class, HelloMessage::encode, HelloMessage::decode, HelloMessage::handle);
        networking.registerServerboundPacket(id("rotate"), RotateMessage.class, RotateMessage::encode, RotateMessage::decode, RotateMessage::handle);
        networking.registerServerboundPacket(id("clear"), ClearMessage.class, ClearMessage::encode, ClearMessage::decode, ClearMessage::handle);
        networking.registerServerboundPacket(id("balance"), BalanceMessage.class, BalanceMessage::encode, BalanceMessage::decode, BalanceMessage::handle);
        networking.registerServerboundPacket(id("transfer"), TransferStackMessage.class, TransferStackMessage::encode, TransferStackMessage::decode, TransferStackMessage::handle);
        networking.registerServerboundPacket(id("compress"), CompressMessage.class, CompressMessage::encode, CompressMessage::decode, CompressMessage::handle);
        networking.registerServerboundPacket(id("craft_stack"), CraftStackMessage.class, CraftStackMessage::encode, CraftStackMessage::decode, CraftStackMessage::handle);

        SyncConfigMessage.register(id("sync_config"), SyncCraftingTweaksConfigMessage.class, SyncCraftingTweaksConfigMessage::new, CraftingTweaksConfigData.class, CraftingTweaksConfigData::new);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(CraftingTweaks.MOD_ID, name);
    }
}
