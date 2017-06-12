package net.blay09.mods.craftingtweaks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@SuppressWarnings("unused")
public class CommonProxy {

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

}
