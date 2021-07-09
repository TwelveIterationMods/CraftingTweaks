package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * An interface for tweak provider implementations. Needs to be registered using CraftingTweaksAPI.registerProvider().
 * Can make use of CraftingTweaksAPI.createDefaultProvider() for standard crafting grids.
 */
public interface TweakProvider<T extends AbstractContainerMenu> {

    /**
     * @return the mod id the provider is for, used to check availability and configuration
     */
    String getModId();

    /**
     * This is called upon registering the provider if the mod id returned in getModId() is loaded
     *
     * @return true if this provider was successfully loaded
     */
    boolean load();

    /**
     * Defaults to true.
     *
     * @return true if this provider is unable to run on client-only instances (i.e. uses phantom items in its grid)
     */
    default boolean requiresServerSide() {
        return true;
    }

    /**
     * Defaults to 1.
     *
     * @param player the player who's looking at the grid
     * @param menu   the menu the grid is part of
     * @param id     the crafting grid ID this is checked for (usually 0 unless there's more grids in one GUI)
     * @return the slot number within the container's inventorySlots list that marks the beginning of the grid (for client-only instances)
     */
    default int getCraftingGridStart(Player player, T menu, int id) {
        return 1;
    }

    /**
     * Defaults to 9.
     *
     * @param player the player who's looking at the grid
     * @param menu   the menu the grid is part of
     * @param id     the crafting grid ID this is checked for (usually 0 unless there's more grids in one GUI)
     * @return the size of this crafting grid within the container's inventorySlots list (for client-only instances)
     */
    default int getCraftingGridSize(Player player, T menu, int id) {
        return 9;
    }

    /**
     * Clears the grid, transferring items from it into the player inventory.
     *
     * @param player the player who's clearing the grid
     * @param menu   the menu the grid is part of
     * @param forced if true, drop items to the ground if necessary
     * @param id     the crafting grid ID that is being cleared (usually 0 unless there's more grids in one GUI)
     */
    void clearGrid(Player player, T menu, int id, boolean forced);

    /**
     * Rotates the grid clockwise.
     *
     * @param player           the player who's rotating the grid
     * @param menu             the menu the grid is part of
     * @param id               the crafting grid ID that is being rotated (usually 0 unless there's more grids in one GUI)
     * @param counterClockwise true if the rotation should happen counter clockwise
     */
    void rotateGrid(Player player, T menu, int id, boolean counterClockwise);

    /**
     * Balances the grid.
     *
     * @param player the player who's balancing the grid
     * @param menu   the menu the grid is part of
     * @param id     the crafting grid ID that is being balanced (usually 0 unless there's more grids in one GUI)
     */
    void balanceGrid(Player player, T menu, int id);

    /**
     * Spreads the items in the grid out.
     *
     * @param player the player who's spreading the grid
     * @param menu   the menu the grid is part of
     * @param id     the crafting grid ID that is being spread (usually 0 unless there's more grids in one GUI)
     */
    void spreadGrid(Player player, T menu, int id);

    /**
     * Checks if the transfer-to-grid feature can be used from the sourceSlot.
     *
     * @param player     the player who's attempting to transfer items
     * @param menu       the menu the grid is part of
     * @param id         the crafting grid ID that is being transferred into (usually 0 unless there's more grids in one GUI)
     * @param sourceSlot the slot items are being transferred from
     * @return true if transfer-to-grid is allowed from the sourceSlot, false otherwise
     */
    boolean canTransferFrom(Player player, T menu, int id, Slot sourceSlot);

    /**
     * Transfers items from sourceSlot into the grid (similar to shift-clicking items into a chest).
     *
     * @param player     the player who's transferring items
     * @param menu       the menu the grid is part of
     * @param id         the crafting grid ID that is being transferred into (usually 0 unless there's more grids in one GUI)
     * @param sourceSlot the slot items are being transferred from
     * @return true if items were fully transferred, false otherwise
     */
    boolean transferIntoGrid(Player player, T menu, int id, Slot sourceSlot);

    /**
     * Puts an item into the grid.
     *
     * @param player    the player who's putting an item in
     * @param menu      the menu the grid is part of
     * @param id        the crafting grid ID that is being put into (usually 0 unless there's more grids in one GUI)
     * @param itemStack the item stack that should be put into the grid
     * @param index     the slot index within the craft matrix the item should be put into
     * @return a rest stack or null if there is no rest
     */
    ItemStack putIntoGrid(Player player, T menu, int id, ItemStack itemStack, int index);

    /**
     * @param player the player that is accessing the container
     * @param menu   the menu the grid is part of
     * @param id     the crafting grid ID that is being accessed (usually 0 unless there's more grids in one GUI)
     * @return the craft matrix inventory
     */
    @Nullable
    Container getCraftMatrix(Player player, T menu, int id);

    /**
     * Called to add buttons to the GUI. May not be called if buttons are disabled in the configuration.
     * Use CraftingTweaksAPI.create***Button() to create tweak buttons, then add them to the buttonList.
     *
     * @param screen        the gui container the buttons are being added to
     * @param addWidgetFunc function to call for adding widgets to the screen
     */
    void initGui(AbstractContainerScreen<T> screen, Consumer<AbstractWidget> addWidgetFunc);

    /**
     * @param menu the menu to test
     * @return true if the container contains a valid crafting grid
     */
    default boolean isValidContainer(AbstractContainerMenu menu) {
        return true;
    }
}
