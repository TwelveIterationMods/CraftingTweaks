package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;

public class Compatiblity {

    public static void vanilla() {
        SimpleTweakProvider provider = CraftingTweaksAPI.registerSimpleProvider("minecraft", ContainerWorkbench.class);
        provider.setTweakRotate(true, true, 10, 17);
        provider.setTweakBalance(true, true, 10, 17 + 18);
        provider.setTweakClear(true, true, 10, 17 + 36);
    }

    public static void jacb() {
        SimpleTweakProvider provider = registerSimpleProvider("jacb", "tv.vanhal.jacb.gui.BenchContainer");
        if(provider != null) {
            provider.setTweakRotate(true, true, 10, 17);
            provider.setTweakBalance(true, true, 10, 17 + 18);
            provider.setTweakClear(true, true, 10, 17 + 36);
        }
    }

    public static void backpack() {
        SimpleTweakProvider provider = registerSimpleProvider("Backpack", "de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack");
        if(provider != null) {
            provider.setPhantomItems(true);
            provider.setTweakRotate(true, true, 8, 35);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, false, 0, 0);
        }
    }

    public static void thaumcraft() {
        SimpleTweakProvider provider = registerSimpleProvider("Thaumcraft", "thaumcraft.common.container.ContainerArcaneWorkbench");
        if(provider != null) {
            provider.setTweakRotate(true, true, -12, 46);
            provider.setTweakBalance(true, true, -12, 64);
            provider.setTweakClear(true, true, -12, 82);
        }
    }

    @SuppressWarnings("unchecked")
    private static SimpleTweakProvider registerSimpleProvider(String modid, String className) {
        try {
            return CraftingTweaksAPI.registerSimpleProvider(modid, (Class<? extends Container>) Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
