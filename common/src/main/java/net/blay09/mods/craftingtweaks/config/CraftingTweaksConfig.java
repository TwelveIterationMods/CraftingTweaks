package net.blay09.mods.craftingtweaks.config;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.common.config.ConfigLocalization;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.network.SyncCraftingTweaksConfigMessage;

public class CraftingTweaksConfig {

    public static CraftingTweaksConfigData getActive() {
        return Balm.getConfig().getActive(CraftingTweaksConfigData.class);
    }

    public static void initialize() {
        ConfigLocalization.enableModernTranslationKeys(CraftingTweaks.MOD_ID);
        Balm.getConfig().registerConfig(CraftingTweaksConfigData.class, SyncCraftingTweaksConfigMessage::new);
    }

}
