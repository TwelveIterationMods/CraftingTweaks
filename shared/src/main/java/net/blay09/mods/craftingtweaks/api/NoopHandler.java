package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NoopHandler implements GridBalanceHandler<AbstractContainerMenu>, GridTransferHandler<AbstractContainerMenu>, GridClearHandler<AbstractContainerMenu>, GridRotateHandler<AbstractContainerMenu> {
    @Override
    public void balanceGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu) {
    }

    @Override
    public void clearGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, boolean forced) {
    }

    @Override
    public ItemStack putIntoGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, int slotId, ItemStack itemStack) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean transferIntoGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, Slot fromSlot) {
        return false;
    }

    @Override
    public boolean canTransferFrom(Player player, AbstractContainerMenu menu, Slot fromSlot, CraftingGrid toGrid) {
        return false;
    }

    @Override
    public void spreadGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu) {
    }

    @Override
    public void rotateGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, boolean reverse) {
    }
}
