package net.blay09.mods.craftingtweaks.fabric.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricCraftingTweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(CraftingTweaks.MOD_ID, FabricLoadContext.INSTANCE, CraftingTweaksClient::initialize);
    }

}
