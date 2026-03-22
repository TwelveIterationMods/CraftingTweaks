package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = CraftingTweaks.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeCraftingTweaksClient {
    public NeoForgeCraftingTweaksClient(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        BalmClient.initializeMod(CraftingTweaks.MOD_ID, context, CraftingTweaksClient::initialize);
    }

}
