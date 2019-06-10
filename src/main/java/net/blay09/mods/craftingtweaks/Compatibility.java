package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.Direction;

public class Compatibility {

    public static void vanilla() {
        SimpleTweakProvider<WorkbenchContainer> providerWorkbench = CraftingTweaksAPI.registerSimpleProvider("minecraft", WorkbenchContainer.class);
        providerWorkbench.setTweakRotate(true, true, 0, 0);
        providerWorkbench.setTweakBalance(true, true, 0, 0);
        providerWorkbench.setTweakClear(true, true, 0, 0);
        providerWorkbench.setAlignToGrid(Direction.WEST);

        SimpleTweakProvider<PlayerContainer> providerPlayer = CraftingTweaksAPI.registerSimpleProvider("minecraft", PlayerContainer.class);
        providerPlayer.setGrid(1, 4);
        providerPlayer.setTweakRotate(true, false, 0, 0);
        providerPlayer.setTweakBalance(true, false, 0, 0);
        providerPlayer.setTweakClear(true, false, 0, 0);
    }

}
