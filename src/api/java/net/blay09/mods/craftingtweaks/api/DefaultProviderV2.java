package net.blay09.mods.craftingtweaks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public interface DefaultProviderV2 {

    /**
     * Returns the rotation handler the default provider uses (handles rotations for vanilla-style standard grids)
     * @return the default rotation handler for vanilla-style grids
     */
    RotationHandler getRotationHandler();

    /**
     * Default implementation for grid rotation. For custom rotation handling, check the overloaded version.
     * @param provider the TweakProvider invoking this function
     * @param id the crafting grid id invoking this function (usually 0 unless the container has multiple grids)
     * @param entityPlayer the player who's rotating the grid
     * @param container the container the grid is part of
     * @param counterClockwise true if the rotation should happen counter clockwise
     */
    <T extends Container> void rotateGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, boolean counterClockwise);

    /**
     * Default implementation for grid rotation with custom rotation handling.
     * @param provider the TweakProvider invoking this function
     * @param id the crafting grid id invoking this function (usually 0 unless the container has multiple grids)
     * @param entityPlayer the player who's rotating the grid
     * @param container the container the grid is part of
     * @param rotationHandler the rotation handler that determines where items are going to end up
     * @param counterClockwise true if the rotation should happen counter clockwise
     */
    <T extends Container> void rotateGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, RotationHandler rotationHandler, boolean counterClockwise);

    /**
     * Default implementation for grid clearing.
     * @deprecated Use clearGrid with forced parameter instead.
     * @param provider the TweakProvider invoking this function
     * @param id the crafting grid id invoking this function (usually 0 unless the container has multiple grids)
     * @param entityPlayer the player who's clearing the grid
     * @param container the container the grid is part of
     * @param phantomItems true if the grid contains phantom items (i.e. they should be deleted, not put into the player's inventory)
     */
    @Deprecated
    default <T extends Container> void clearGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, boolean phantomItems) {
        clearGrid(provider, id, entityPlayer, container, phantomItems, false);
    }

    /**
     * Default implementation for grid clearing.
     * @param provider the TweakProvider invoking this function
     * @param id the crafting grid id invoking this function (usually 0 unless the container has multiple grids)
     * @param entityPlayer the player who's clearing the grid
     * @param container the container the grid is part of
     * @param phantomItems true if the grid contains phantom items (i.e. they should be deleted, not put into the player's inventory)
     * @param forced if this is true, items will be dropped to the ground if necessary
     */
    <T extends Container> void clearGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, boolean phantomItems, boolean forced);

    /**
     * Default implementation for grid balancing.
     * @param provider the TweakProvider invoking this function
     * @param id the crafting grid id invoking this function (usually 0 unless the container has multiple grids)
     * @param entityPlayer the player who's balancing the grid
     * @param container the container the grid is part of
     */
    <T extends Container> void balanceGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container);

    /**
     * Default implementation for grid spreading.
     * @param provider the TweakProvider invoking this function
     * @param id the crafting grid id invoking this function (usually 0 unless the container has multiple grids)
     * @param entityPlayer the player who's spreading the grid
     * @param container the container the grid is part of
     */
    <T extends Container> void spreadGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container);

    /**
     * Default implementation for putting an item into a specific slot within a the grid.
     * @param provider the TweakProvider invoking this function
     * @param id the crafting grid id invoking this function (usually 0 unless the container has multiple grids)
     * @param entityPlayer the player who's putting the item in
     * @param container the container the grid is part of
     * @param itemStack the item stack that's being put into the grid
     * @param index the slot index within the craft matrix the item is being put into
     * @return a rest stack that didn't fit in or null if there is no rest
     */
    <T extends Container> ItemStack putIntoGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, ItemStack itemStack, int index);

    /**
     * Default implementation for transferring an item into the crafting grid
     * @param provider the TweakProvider invoking this function
     * @param id the crafting grid id invoking this function (usually 0 unless the container has multiple grids)
     * @param entityPlayer the player who's putting the item in
     * @param container the container the grid is part of
     * @param sourceSlot the source slot inside the container the item is coming from
     * @return true if items were transferred, false otherwise
     */
    <T extends Container> boolean transferIntoGrid(TweakProvider<T> provider, int id, EntityPlayer entityPlayer, T container, Slot sourceSlot);

    /**
     * Default implementation for checking if transfer-to-grid is allowed from this slot. Checks if this slot is part of the player inventory.
     * @param entityPlayer the player who wants to transfer items into the grid
     * @param container the container the grid is part of
     * @param sourceSlot the source slot inside the container the item is coming from
     * @return true if items are allowed to be transferred from this slot, false otherwise
     */
    <T extends Container> boolean canTransferFrom(EntityPlayer entityPlayer, T container, Slot sourceSlot);
}
