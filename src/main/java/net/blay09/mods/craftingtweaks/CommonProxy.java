package net.blay09.mods.craftingtweaks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.blay09.mods.craftingtweaks.net.MessageHello;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommonProxy {

    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void receivedHello(EntityPlayer entityPlayer) {
    }

    @SubscribeEvent
    public void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        NetworkHandler.instance.sendTo(new MessageHello(NetworkHandler.PROTOCOL_VERSION), (EntityPlayerMP) event.player);
    }

}
