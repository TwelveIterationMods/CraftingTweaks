package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(CraftingTweaks.MOD_ID)
public class ForgeCraftingTweaks {
    public ForgeCraftingTweaks(FMLJavaModLoadingContext context) {
        Balm.initialize(CraftingTweaks.MOD_ID, EmptyLoadContext.INSTANCE, CraftingTweaks::initialize);
        if (FMLEnvironment.dist.isClient())
            BalmClient.initialize(CraftingTweaks.MOD_ID, EmptyLoadContext.INSTANCE, CraftingTweaksClient::initialize);

        context.getModEventBus().addListener(IMCHandler::processInterMod);
        context.registerDisplayTest(IExtensionPoint.DisplayTest.IGNORE_ALL_VERSION);
    }

}
