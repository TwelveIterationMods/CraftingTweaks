package net.blay09.mods.craftingtweaks.addons;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class CraftingTweaksAddons {

    public static final Logger logger = LogManager.getLogger();

    public static void loadAddons() {
        progressiveAutomation();

        if (ModList.get().isLoaded("storagesilo")) {
            registerProvider("uk.binarycraft.storagesilo.blocks.craftingsilo.ContainerCraftingSilo", new ProviderCraftingSilo());
        }
    }

    private static void progressiveAutomation() {
        SimpleTweakProvider provider = registerSimpleProvider("progressiveAutomation", "com.vanhal.progressiveAutomation.gui.container.ContainerCrafter");
        if (provider != null) {
            provider.setGrid(2, 9);
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(Direction.WEST);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static SimpleTweakProvider registerSimpleProvider(String modid, String className) {
        try {
            if (ModList.get().isLoaded(modid)) {
                return CraftingTweaksAPI.registerSimpleProvider(modid, (Class<? extends Container>) Class.forName(className));
            }
        } catch (ClassNotFoundException e) {
            logger.error("Could not register Crafting Tweaks addon for {} - internal names have changed.", modid);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void registerProvider(String className, TweakProvider provider) {
        try {
            CraftingTweaksAPI.registerProvider((Class<? extends Container>) Class.forName(className), provider);
        } catch (ClassNotFoundException e) {
            logger.error("Could not register Crafting Tweaks addon for {} - internal names have changed.", provider.getModId());
        }
    }
}
