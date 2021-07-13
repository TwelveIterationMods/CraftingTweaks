package net.blay09.mods.craftingtweaks.api;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class CraftingTweaksClientAPI {

    private static InternalClientMethods internalMethods;

    /**
     * Internal Method. Stay away.
     * @param internalMethods I said stay away.
     */
    public static void setupAPI(InternalClientMethods internalMethods) {
        CraftingTweaksClientAPI.internalMethods = internalMethods;
    }

    /**
     * Creates a Crafting Tweaks button to balance the grid. To be called from within TweakProvider.initGui()
     * @param grid the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static Button createBalanceButton(CraftingGrid grid, int x, int y) {
        return internalMethods.createBalanceButton(grid, null, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within TweakProvider.initGui()
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static Button createRotateButton(CraftingGrid grid, int x, int y) {
        return internalMethods.createRotateButton(grid, null, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within TweakProvider.initGui()
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static Button createClearButton(CraftingGrid grid, int x, int y) {
        return internalMethods.createClearButton(grid, null, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to balance the grid. To be called from within TweakProvider.initGui().
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param parentGui the parent GUI for this button, used to obtain the absolute position
     * @param relX the relative x position the button should be placed at
     * @param relY the relative y position the button should be placed at
     */
    public static Button createBalanceButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> parentGui, int relX, int relY) {
        int guiLeft = ((AbstractContainerScreenAccessor) parentGui).getLeftPos();
        int guiTop = ((AbstractContainerScreenAccessor) parentGui).getTopPos();
        return internalMethods.createBalanceButton(grid, parentGui, relX + guiLeft, relY + guiTop);
    }


    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within TweakProvider.initGui()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param parentGui the parent GUI for this button, used to obtain the absolute position
     * @param relX the relative x position the button should be placed at
     * @param relY the relative y position the button should be placed at
     */
    public static Button createRotateButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> parentGui, int relX, int relY) {
        int guiLeft = ((AbstractContainerScreenAccessor) parentGui).getLeftPos();
        int guiTop = ((AbstractContainerScreenAccessor) parentGui).getTopPos();
        return internalMethods.createRotateButton(grid, parentGui, relX + guiLeft, relY + guiTop);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within TweakProvider.initGui()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param parentGui the parent GUI for this button, used to obtain the absolute position
     * @param relX the relative x position the button should be placed at
     * @param relY the relative y position the button should be placed at
     */
    public static Button createClearButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> parentGui, int relX, int relY) {
        int guiLeft = ((AbstractContainerScreenAccessor) parentGui).getLeftPos();
        int guiTop = ((AbstractContainerScreenAccessor) parentGui).getTopPos();
        return internalMethods.createClearButton(grid, parentGui, relX + guiLeft, relY + guiTop);
    }
}
