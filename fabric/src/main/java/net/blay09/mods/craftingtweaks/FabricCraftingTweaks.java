package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.api.Balm;
import net.fabricmc.api.ModInitializer;

public class FabricCraftingTweaks implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(CraftingTweaks.MOD_ID, CraftingTweaks::initialize);
    }
}
