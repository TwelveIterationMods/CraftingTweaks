package net.blay09.mods.craftingtweaks.network;


import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;
import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.balm.network.SyncConfigMessage;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking extends BalmNetworking {

    public static void initialize() {
        registerClientboundPacket(id("hello"), HelloMessage.class, HelloMessage::encode, HelloMessage::decode, HelloMessage::handle);
        registerServerboundPacket(id("rotate"), RotateMessage.class, RotateMessage::encode, RotateMessage::decode, RotateMessage::handle);
        registerServerboundPacket(id("clear"), ClearMessage.class, ClearMessage::encode, ClearMessage::decode, ClearMessage::handle);
        registerServerboundPacket(id("balance"), BalanceMessage.class, BalanceMessage::encode, BalanceMessage::decode, BalanceMessage::handle);
        registerServerboundPacket(id("transfer"), TransferStackMessage.class, TransferStackMessage::encode, TransferStackMessage::decode, TransferStackMessage::handle);
        registerServerboundPacket(id("compress"), CompressMessage.class, CompressMessage::encode, CompressMessage::decode, CompressMessage::handle);
        registerServerboundPacket(id("craft_stack"), CraftStackMessage.class, CraftStackMessage::encode, CraftStackMessage::decode, CraftStackMessage::handle);

        SyncConfigMessage.register(id("sync_config"), SyncCraftingTweaksConfigMessage.class, SyncCraftingTweaksConfigMessage::new, CraftingTweaksConfigData.class, CraftingTweaksConfigData::new);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(CraftingTweaks.MOD_ID, name);
    }
}
