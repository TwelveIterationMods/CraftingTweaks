package net.blay09.mods.craftingtweaks.fabric;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.fabricmc.api.ModInitializer;

public class FabricCraftingTweaks implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(CraftingTweaks.MOD_ID, FabricLoadContext.INSTANCE, CraftingTweaks::initialize);
    }
}
