package net.blay09.mods.craftingtweaks.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.fabricmc.api.ModInitializer;

public class FabricCraftingTweaks implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(CraftingTweaks.MOD_ID, EmptyLoadContext.INSTANCE, CraftingTweaks::initialize);
    }
}
