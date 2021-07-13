package net.blay09.mods.craftingtweaks.compat;

import net.blay09.mods.craftingtweaks.api.CraftingGridBuilder;
import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;

public class VanillaCraftingGridProvider implements CraftingGridProvider {
    @Override
    public String getModId() {
        return "minecraft";
    }

    @Override
    public void onInitialize() {
    }

    @Override
    public void buildCraftingGrids(CraftingGridBuilder builder, AbstractContainerMenu menu) {
        if (menu instanceof CraftingMenu) {
            builder.addGrid(1, 9);
        } else if (menu instanceof InventoryMenu) {
            builder.addGrid(1, 4)
                    .hideAllTweakButtons();
        }
    }
}
