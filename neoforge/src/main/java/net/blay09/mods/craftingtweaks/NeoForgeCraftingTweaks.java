package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(CraftingTweaks.MOD_ID)
public class NeoForgeCraftingTweaks {
    public NeoForgeCraftingTweaks(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        Balm.initializeMod(CraftingTweaks.MOD_ID, context, CraftingTweaks::initialize);
        modEventBus.addListener(IMCHandler::processInterMod);
    }

}
