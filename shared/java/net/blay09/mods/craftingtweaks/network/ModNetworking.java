package net.blay09.mods.craftingtweaks.network;


import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking extends ForbicNetworking {

    public static void init() {
        // TODO CraftingTweaks.isServerSideInstalled = !CraftingTweaks.TEST_CLIENT_SIDE && it.equals("1.0");

        registerServerboundPacket(id("rotate"), RotateMessage.class, RotateMessage::encode, RotateMessage::decode, RotateMessage::handle);
        registerServerboundPacket(id("clear"), ClearMessage.class, ClearMessage::encode, ClearMessage::decode, ClearMessage::handle);
        registerServerboundPacket(id("balance"), BalanceMessage.class, BalanceMessage::encode, BalanceMessage::decode, BalanceMessage::handle);
        registerServerboundPacket(id("transfer"), TransferStackMessage.class, TransferStackMessage::encode, TransferStackMessage::decode, TransferStackMessage::handle);
        registerServerboundPacket(id("compress"), CompressMessage.class, CompressMessage::encode, CompressMessage::decode, CompressMessage::handle);
        registerServerboundPacket(id("craft_stack"), CraftStackMessage.class, CraftStackMessage::encode, CraftStackMessage::decode, CraftStackMessage::handle);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(CraftingTweaks.MOD_ID, name);
    }
}
