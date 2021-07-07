package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;

public class Compatibility {

    public static void vanilla() {
        SimpleTweakProvider<CraftingMenu> providerWorkbench = CraftingTweaksAPI.registerSimpleProvider("minecraft", CraftingMenu.class);
        providerWorkbench.setTweakRotate(true, true, 0, 0);
        providerWorkbench.setTweakBalance(true, true, 0, 0);
        providerWorkbench.setTweakClear(true, true, 0, 0);
        providerWorkbench.setAlignToGrid(Direction.WEST);

        SimpleTweakProvider<InventoryMenu> providerPlayer = CraftingTweaksAPI.registerSimpleProvider("minecraft", InventoryMenu.class);
        providerPlayer.setGrid(1, 4);
        providerPlayer.setTweakRotate(true, false, 0, 0);
        providerPlayer.setTweakBalance(true, false, 0, 0);
        providerPlayer.setTweakClear(true, false, 0, 0);
    }

}
