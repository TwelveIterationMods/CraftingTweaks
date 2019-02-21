package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    public static SimpleChannel channel;

    public static void init() {
        channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(CraftingTweaks.MOD_ID, "network"), () -> "1.0", it -> {
            CraftingTweaks.isServerSideInstalled = !CraftingTweaks.TEST_CLIENT_SIDE && it.equals("1.0");
            return true;
        }, it -> true);

        channel.registerMessage(0, MessageRotate.class, MessageRotate::encode, MessageRotate::decode, MessageRotate::handle);
        channel.registerMessage(1, MessageClear.class, MessageClear::encode, MessageClear::decode, MessageClear::handle);
        channel.registerMessage(2, MessageBalance.class, MessageBalance::encode, MessageBalance::decode, MessageBalance::handle);
        channel.registerMessage(3, MessageTransferStack.class, MessageTransferStack::encode, MessageTransferStack::decode, MessageTransferStack::handle);
        channel.registerMessage(4, MessageCompress.class, MessageCompress::encode, MessageCompress::decode, MessageCompress::handle);
        channel.registerMessage(5, MessageCraftStack.class, MessageCraftStack::encode, MessageCraftStack::decode, MessageCraftStack::handle);
    }

}
