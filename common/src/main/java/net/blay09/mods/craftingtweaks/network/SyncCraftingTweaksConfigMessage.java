package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;
import net.minecraft.resources.ResourceLocation;

public class SyncCraftingTweaksConfigMessage extends SyncConfigMessage<CraftingTweaksConfigData> {

    public static final Type<SyncCraftingTweaksConfigMessage> TYPE = new Type<>(new ResourceLocation(CraftingTweaks.MOD_ID, "config"));

    public SyncCraftingTweaksConfigMessage(CraftingTweaksConfigData craftingTweaksConfigData) {
        super(TYPE, craftingTweaksConfigData);
    }
}
