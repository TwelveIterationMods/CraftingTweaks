package net.blay09.mods.craftingtweaks.net;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(CraftingTweaks.MOD_ID);
    public static final int PROTOCOL_VERSION = 1;

    public static void init() {
        instance.registerMessage(HandlerRotate.class, MessageRotate.class, 0, Side.SERVER);
        instance.registerMessage(HandlerClear.class, MessageClear.class, 1, Side.SERVER);
        instance.registerMessage(HandlerBalance.class, MessageBalance.class, 2, Side.SERVER);
        instance.registerMessage(HandlerHello.class, MessageHello.class, 3, Side.SERVER);
        instance.registerMessage(HandlerHello.class, MessageHello.class, 4, Side.CLIENT);
        instance.registerMessage(HandlerTransferStack.class, MessageTransferStack.class, 5, Side.SERVER);
    }

}
