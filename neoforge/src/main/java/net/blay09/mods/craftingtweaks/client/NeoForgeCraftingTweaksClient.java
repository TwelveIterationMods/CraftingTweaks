package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = CraftingTweaks.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeCraftingTweaksClient {
    public NeoForgeCraftingTweaksClient(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        BalmClient.initialize(CraftingTweaks.MOD_ID, context, CraftingTweaksClient::initialize);
    }

}
