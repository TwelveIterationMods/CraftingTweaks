package net.blay09.mods.waystones;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.fabricmc.api.ModInitializer;

public class FabricCraftingTweaks implements ModInitializer {
    @Override
    public void onInitialize() {
        CraftingTweaks.initialize();
    }
}
