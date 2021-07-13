package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface GridClearHandler<TMenu extends AbstractContainerMenu> {

    /**
     * Clears the grid, transferring items from it into the player inventory.
     * @param player the player who's clearing the grid
     * @param menu   the menu the grid is part of
     * @param forced if true, clear grid at all costs, even if it means dropping items to the ground
     */
    void clearGrid(CraftingGrid grid, Player player, TMenu menu, boolean forced);

}
