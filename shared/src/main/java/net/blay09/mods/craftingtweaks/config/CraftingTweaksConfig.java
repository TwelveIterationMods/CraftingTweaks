package net.blay09.mods.craftingtweaks.config;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingtweaks.network.SyncCraftingTweaksConfigMessage;

public class CraftingTweaksConfig {

    public static CraftingTweaksConfigData getActive() {
        return Balm.getConfig().getActive(CraftingTweaksConfigData.class);
    }

    public static void initialize() {
        Balm.getConfig().registerConfig(CraftingTweaksConfigData.class, SyncCraftingTweaksConfigMessage::new);
    }

}
