package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface GridTransferHandler<TMenu extends AbstractContainerMenu> {
    ItemStack putIntoGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, int slotId, ItemStack itemStack);

    /**
     * Default implementation for transferring an item into the crafting grid
     * @param player the player who's putting the item in
     * @param menu the menu the grid is part of
     * @param fromSlot the slot inside the container the item is coming from
     * @return true if items were transferred, false otherwise
     */
    boolean transferIntoGrid(CraftingGrid grid, Player player, TMenu menu, Slot fromSlot);

    boolean canTransferFrom(Player player, TMenu menu, Slot fromSlot, CraftingGrid toGrid);
}
