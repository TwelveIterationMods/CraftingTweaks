package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;

public class SyncCraftingTweaksConfigMessage extends SyncConfigMessage<CraftingTweaksConfigData> {
    public SyncCraftingTweaksConfigMessage(CraftingTweaksConfigData craftingTweaksConfigData) {
        super(craftingTweaksConfigData);
    }
}
