package net.blay09.mods.craftingtweaks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @deprecated Use DefaultProviderV2 instead.
 */
@Deprecated
public interface DefaultProvider {

    /**
     * Returns the rotation handler the default provider uses (handles rotations for vanilla-style standard grids)
     * @return the default rotation handler for vanilla-style grids
     */
    RotationHandler getRotationHandler();

    /**
     * Default implementation for grid rotation. For custom slot numbers and rotation handling, check the overloaded version.
     * @param entityPlayer the player who's rotating the grid
     * @param container the container the grid is part of
     * @param craftMatrix the craft matrix inventory that's being rotated
     */
    void rotateGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);

    /**
     * Default implementation for grid rotation with custom slot numbers and rotation handling.
     * @param entityPlayer the player who's rotating the grid
     * @param container the container the grid is part of
     * @param craftMatrix the craft matrix inventory that's being rotated
     * @param start the start slot of the crafting grid within the craft matrix
     * @param size the size of the crafting grid within the craft matrix
     * @param rotationHandler the rotation handler that determines where items are going to end up
     */
    void rotateGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size, RotationHandler rotationHandler);

    /**
     * Default implementation for grid clearing. For custom slot numbers, check the overloaded version.
     * @param entityPlayer the player who's clearing the grid
     * @param container the container the grid is part of
     * @param craftMatrix the craft matrix inventory that's being cleared
     */
    void clearGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);

    /**
     * Default implementation for grid clearing with custom slot numbers.
     * @param entityPlayer the player who's clearing the grid
     * @param container the container the grid is part of
     * @param craftMatrix the craft matrix inventory that's being cleared
     * @param start the start slot of the crafting grid within the craft matrix
     * @param size the size of the crafting grid within the craft matrix
     */
    void clearGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size);

    /**
     * Default implementation for grid balancing. For custom slot numbers, check the overloaded version.
     * @param entityPlayer the player who's balancing the grid
     * @param container the container the grid is part of
     * @param craftMatrix the craft matrix inventory that's being balanced
     */
    void balanceGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);

    /**
     * Default implementation for grid balancing with custom slot numbers.
     * @param entityPlayer the player who's balancing the grid
     * @param container the container the grid is part of
     * @param craftMatrix the craft matrix inventory that's being balanced
     * @param start the start slot of the crafting grid within the craft matrix
     * @param size the size of the crafting grid within the craft matrix
     */
    void balanceGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size);

    /**
     * Default implementation for putting an item into a specific slot within a the grid.
     * @param entityPlayer the player who's putting the item in
     * @param container the container the grid is part of
     * @param craftMatrix the craft matrix inventory that's being put into
     * @param itemStack the item stack that's being put into the grid
     * @param index the slot index within the craft matrix the item is being put into
     * @return a rest stack that didn't fit in or null if there is no rest
     */
    ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, ItemStack itemStack, int index);

    /**
     * Default implementation for transferring an item into the crafting grid
     * @param entityPlayer the player who's putting the item in
     * @param container the container the grid is part of
     * @param craftMatrix the craft matrix inventory that's being put into
     * @param sourceSlot the source slot inside the container the item is coming from
     * @return true if items were transferred, false otherwise
     */
    boolean transferIntoGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, Slot sourceSlot);

    /**
     * Default implementation for checking if transfer-to-grid is allowed from this slot. Checks if this slot is part of the player inventory.
     * @param entityPlayer the player who wants to transfer items into the grid
     * @param container the container the grid is part of
     * @param sourceSlot the source slot inside the container the item is coming from
     * @return true if items are allowed to be transferred from this slot, false otherwise
     */
    boolean canTransferFrom(EntityPlayer entityPlayer, Container container, Slot sourceSlot);

    /**
     * Default implementation for checking if transfer-to-grid is allowed from this slot. Checks if this slot is part of the player inventory.
     * @param entityPlayer the player who wants to transfer items into the grid
     * @param container the container the grid is part of
     * @param id unused
     * @param sourceSlot the source slot inside the container the item is coming from
     * @deprecated Use canTransferFrom() without the id parameter instead
     * @return true if items are allowed to be transferred from this slot, false otherwise
     */
    @Deprecated
    boolean canTransferFrom(EntityPlayer entityPlayer, Container container, int id, Slot sourceSlot);
}
