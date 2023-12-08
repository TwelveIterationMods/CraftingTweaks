package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CraftingTweaks.MOD_ID)
public class NeoForgeCraftingTweaks {
    public NeoForgeCraftingTweaks() {
        Balm.initialize(CraftingTweaks.MOD_ID, CraftingTweaks::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(CraftingTweaks.MOD_ID, CraftingTweaksClient::initialize));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(IMCHandler::processInterMod);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
    }

}
