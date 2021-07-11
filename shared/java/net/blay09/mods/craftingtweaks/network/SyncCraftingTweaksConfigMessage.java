package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;
import net.blay09.mods.balm.network.SyncConfigMessage;

public class SyncCraftingTweaksConfigMessage extends SyncConfigMessage<CraftingTweaksConfigData> {
    public SyncCraftingTweaksConfigMessage(CraftingTweaksConfigData craftingTweaksConfigData) {
        super(craftingTweaksConfigData);
    }
}
