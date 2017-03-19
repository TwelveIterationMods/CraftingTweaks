package net.blay09.mods.craftingtweaks.addons;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class CraftingTweaksAddons {

    public static final Logger logger = LogManager.getLogger();

    public static void postInit(FMLPostInitializationEvent event) {
        jacb();
        progressiveautomation();

        if(Loader.isModLoaded("storagesilo")) {
            registerProvider("uk.binarycraft.storagesilo.blocks.craftingsilo.ContainerCraftingSilo", new ProviderCraftingSilo());
        }
    }

    private static void jacb() {
        SimpleTweakProvider provider = registerSimpleProvider("jacb", "tv.vanhal.jacb.gui.BenchContainer");
        if(provider != null) {
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    private static void progressiveautomation() {
        SimpleTweakProvider provider = registerSimpleProvider("progressiveautomation", "com.vanhal.progressiveautomation.gui.container.ContainerCrafter");
        if(provider != null) {
            provider.setGrid(2, 9);
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static SimpleTweakProvider registerSimpleProvider(String modid, String className) {
        try {
            if(Loader.isModLoaded(modid)) {
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
