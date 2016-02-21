package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * An interface for tweak provider implementations. Needs to be registered using CraftingTweaksAPI.registerProvider().
 * Can make use of CraftingTweaksAPI.createDefaultProvider() for standard crafting grids.
 */
public interface TweakProvider {

    /**
     * @return the mod id the provider is for, used to check availability and configuration
     */
    String getModId();

    /**
     * This is called upon registering the provider if the mod id returned in getModId() is loaded
     * @return true if this provider was successfully loaded
     */
    boolean load();

    /**
     * Defaults to true.
     * @return true if this provider is unable to run on client-only instances (i.e. uses phantom items in its grid)
     */
    default boolean requiresServerSide() {
        return true;
    }

    /**
     * Defaults to 1.
     * @param entityPlayer the player who's looking at the grid
     * @param container the container the grid is part of
     * @param id the crafting grid ID this is checked for (usually 0 unless there's more grids in one GUI)
     * @return the slot number within the container's inventorySlots list that marks the beginning of the grid (for client-only instances)
     */
    default int getCraftingGridStart(EntityPlayer entityPlayer, Container container, int id) {
        return 1;
    }

    /**
     * Defaults to 9.
     * @param entityPlayer the player who's looking at the grid
     * @param container the container the grid is part of
     * @param id the crafting grid ID this is checked for (usually 0 unless there's more grids in one GUI)
     * @return the size of this crafting grid within the container's inventorySlots list (for client-only instances)
     */
    default int getCraftingGridSize(EntityPlayer entityPlayer, Container container, int id) {
        return 9;
    }

    /**
     * Clears the grid, transferring items from it into the player inventory.
     * @deprecated use clearGrid with forced parameter instead
     * @param entityPlayer the player who's clearing the grid
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being cleared (usually 0 unless there's more grids in one GUI)
     */
    @Deprecated
    default void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        clearGrid(entityPlayer, container, id, false);
    }

    /**
     * Clears the grid, transferring items from it into the player inventory.
     * @param entityPlayer the player who's clearing the grid
     * @param container the container the grid is part of
     * @param forced if true, drop items to the ground if necessary
     * @param id the crafting grid ID that is being cleared (usually 0 unless there's more grids in one GUI)
     */
    void clearGrid(EntityPlayer entityPlayer, Container container, int id, boolean forced);

    /**
     * Rotates the grid clockwise.
     * @deprecated Use rotateGrid with counterClockwise parameter instead.
     * @param entityPlayer the player who's rotating the grid
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being rotated (usually 0 unless there's more grids in one GUI)
     */
    @Deprecated
    default void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        rotateGrid(entityPlayer, container, id, false);
    }

    /**
     * Rotates the grid clockwise.
     * @param entityPlayer the player who's rotating the grid
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being rotated (usually 0 unless there's more grids in one GUI)
     * @param counterClockwise true if the rotation should happen counter clockwise
     */
    void rotateGrid(EntityPlayer entityPlayer, Container container, int id, boolean counterClockwise);

    /**
     * Balances the grid.
     * @param entityPlayer the player who's balancing the grid
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being balanced (usually 0 unless there's more grids in one GUI)
     */
    void balanceGrid(EntityPlayer entityPlayer, Container container, int id);

    /**
     * Spreads the items in the grid out.
     * @param entityPlayer the player who's spreading the grid
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being spread (usually 0 unless there's more grids in one GUI)
     */
    void spreadGrid(EntityPlayer entityPlayer, Container container, int id);

    /**
     * Checks if the transfer-to-grid feature can be used from the sourceSlot.
     * @param entityPlayer the player who's attempting to transfer items
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being transferred into (usually 0 unless there's more grids in one GUI)
     * @param sourceSlot the slot items are being transferred from
     * @return true if transfer-to-grid is allowed from the sourceSlot, false otherwise
     */
    boolean canTransferFrom(EntityPlayer entityPlayer, Container container, int id, Slot sourceSlot);

    /**
     * Transfers items from sourceSlot into the grid (similar to shift-clicking items into a chest).
     * @param entityPlayer the player who's transferring items
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being transferred into (usually 0 unless there's more grids in one GUI)
     * @param sourceSlot the slot items are being transferred from
     * @return true if items were fully transferred, false otherwise
     */
    boolean transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, Slot sourceSlot);

    /**
     * Puts an item into the grid.
     * @param entityPlayer the player who's putting an item in
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being put into (usually 0 unless there's more grids in one GUI)
     * @param itemStack the item stack that should be put into the grid
     * @param index the slot index within the craft matrix the item should be put into
     * @return a rest stack or null if there is no rest
     */
    ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack, int index);

    /**
     * @param entityPlayer the player that is accessing the container
     * @param container the container the grid is part of
     * @param id the crafting grid ID that is being accessed (usually 0 unless there's more grids in one GUI)
     * @return the craft matrix inventory
     */
    IInventory getCraftMatrix(EntityPlayer entityPlayer, Container container, int id);

    /**
     * Called to add buttons to the GUI. May not be called if buttons are disabled in the configuration.
     * Use CraftingTweaksAPI.create***Button() to create tweak buttons, then add them to the buttonList.
     * @param guiContainer the gui container the buttons are being added to
     * @param buttonList the button list of this gui container
     */
    @SideOnly(Side.CLIENT)
    void initGui(GuiContainer guiContainer, List<GuiButton> buttonList);
}
