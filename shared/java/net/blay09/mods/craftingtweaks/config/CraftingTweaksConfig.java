package net.blay09.mods.craftingtweaks.config;

import net.blay09.mods.forbic.config.ForbicConfigHolder;

public class CraftingTweaksConfig {

    public static CraftingTweaksConfigData getActive() {
        return ForbicConfigHolder.getActive(CraftingTweaksConfigData.class);
    }

    public static void initialize() {

    }
}
