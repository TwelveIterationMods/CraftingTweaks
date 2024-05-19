package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface GridBalanceHandler<TMenu extends AbstractContainerMenu> {
    /**
     * Balances the grid.
     *
     * @param player the player who's balancing the grid
     * @param menu   the menu the grid is part of
     */
    void balanceGrid(CraftingGrid grid, Player player, TMenu menu);

    /**
     * Spreads the items in the grid out.
     *
     * @param player the player who's spreading the grid
     * @param menu   the menu the grid is part of
     */
    void spreadGrid(CraftingGrid grid, Player player, TMenu menu);
}
