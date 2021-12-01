package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface GridRotateHandler<TMenu extends AbstractContainerMenu> {
    void rotateGrid(CraftingGrid grid, Player player, TMenu menu, boolean reverse);
}
