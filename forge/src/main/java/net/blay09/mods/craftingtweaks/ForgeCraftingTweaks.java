package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.forge.platform.runtime.ForgeLoadContext;
import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

@Mod(CraftingTweaks.MOD_ID)
public class ForgeCraftingTweaks {
    public ForgeCraftingTweaks(FMLJavaModLoadingContext context) {
        final var loadContext = new ForgeLoadContext(context.getModBusGroup());
        Balm.initializeMod(CraftingTweaks.MOD_ID, loadContext, CraftingTweaks::initialize);
        if (FMLEnvironment.dist.isClient()) {
            BalmClient.initializeMod(CraftingTweaks.MOD_ID, loadContext, CraftingTweaksClient::initialize);
        }

        InterModProcessEvent.getBus(context.getModBusGroup()).addListener(IMCHandler::processInterMod);
        context.registerDisplayTest(IExtensionPoint.DisplayTest.IGNORE_ALL_VERSION);
    }

}
