package net.blay09.mods.craftingtweaks.net;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.blay09.mods.craftingtweaks.CraftingTweaks;

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
        instance.registerMessage(HandlerCompress.class, MessageCompress.class, 6, Side.SERVER);
    }

}
