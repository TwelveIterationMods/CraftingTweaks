package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.EnumFacing;

public class Compatibility {

    public static void vanilla() {
        SimpleTweakProvider<ContainerWorkbench> providerWorkbench = CraftingTweaksAPI.registerSimpleProvider("minecraft", ContainerWorkbench.class);
        providerWorkbench.setTweakRotate(true, true, 0, 0);
        providerWorkbench.setTweakBalance(true, true, 0, 0);
        providerWorkbench.setTweakClear(true, true, 0, 0);
        providerWorkbench.setAlignToGrid(EnumFacing.WEST);

        SimpleTweakProvider<ContainerPlayer> providerPlayer = CraftingTweaksAPI.registerSimpleProvider("minecraft", ContainerPlayer.class);
        providerPlayer.setGrid(1, 4);
        providerPlayer.setTweakRotate(true, false, 0, 0);
        providerPlayer.setTweakBalance(true, false, 0, 0);
        providerPlayer.setTweakClear(true, false, 0, 0);
    }

}
