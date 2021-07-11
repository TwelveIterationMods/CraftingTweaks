package net.blay09.mods.craftingtweaks.config;

import net.blay09.mods.craftingtweaks.network.SyncCraftingTweaksConfigMessage;
import net.blay09.mods.balm.config.BalmConfigHolder;

public class CraftingTweaksConfig {

    public static CraftingTweaksConfigData getActive() {
        return BalmConfigHolder.getActive(CraftingTweaksConfigData.class);
    }

    public static void initialize() {
        BalmConfigHolder.registerConfig(CraftingTweaksConfigData.class, SyncCraftingTweaksConfigMessage::new);
    }

    public static void setHideButtons(boolean hideButtons) {
        BalmConfigHolder.updateConfig(CraftingTweaksConfigData.class, config -> {
            config.client.hideButtons = hideButtons;
        });
    }
}
