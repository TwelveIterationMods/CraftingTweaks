package net.blay09.mods.waystones.client;

import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricCraftingTweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CraftingTweaksClient.initialize();
    }
}
