package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.EnumFacing;

public class Compatiblity {

    public static void vanilla() {
        SimpleTweakProvider provider = CraftingTweaksAPI.registerSimpleProvider("minecraft", ContainerWorkbench.class);
        provider.setTweakRotate(true, true, 0, 0);
        provider.setTweakBalance(true, true, 0, 0);
        provider.setTweakClear(true, true, 0, 0);
        provider.setAlignToGrid(EnumFacing.WEST);
    }

}
