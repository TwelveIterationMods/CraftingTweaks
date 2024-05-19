package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CraftingTweaks.MOD_ID)
public class NeoForgeCraftingTweaks {
    public NeoForgeCraftingTweaks(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initialize(CraftingTweaks.MOD_ID, context, CraftingTweaks::initialize);
        modEventBus.addListener(IMCHandler::processInterMod);
    }

}
