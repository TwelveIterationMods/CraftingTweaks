package net.blay09.mods.craftingtweaks.config;

import net.blay09.mods.craftingtweaks.network.SyncCraftingTweaksConfigMessage;
import net.blay09.mods.forbic.config.ForbicConfigHolder;

public class CraftingTweaksConfig {

    public static CraftingTweaksConfigData getActive() {
        return ForbicConfigHolder.getActive(CraftingTweaksConfigData.class);
    }

    public static void initialize() {
        ForbicConfigHolder.registerConfig(CraftingTweaksConfigData.class, SyncCraftingTweaksConfigMessage::new);
    }

    public static void setHideButtons(boolean hideButtons) {
        ForbicConfigHolder.updateConfig(CraftingTweaksConfigData.class, config -> {
            config.client.hideButtons = hideButtons;
        });
    }
}
