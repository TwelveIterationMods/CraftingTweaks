package net.blay09.mods.craftingtweaks.api;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CraftingTweaksClientAPI {

    private static InternalClientMethods internalMethods;

    /**
     * Internal Method. Stay away.
     *
     * @param internalMethods I said stay away.
     */
    public static void setupAPI(InternalClientMethods internalMethods) {
        CraftingTweaksClientAPI.internalMethods = internalMethods;
    }

    public static <TMenu extends AbstractContainerMenu, TScreen extends AbstractContainerScreen<TMenu>> void registerCraftingGridGuiHandler(Class<TScreen> clazz, GridGuiHandler handler) {
        internalMethods.registerCraftingGridGuiHandler(clazz, handler);
    }

    /**
     * Creates a Crafting Tweaks button to balance the grid. To be called from within TweakProvider.initGui()
     *
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static Button createBalanceButton(CraftingGrid grid, int x, int y) {
        return internalMethods.createBalanceButton(grid, null, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within TweakProvider.initGui()
     *
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static Button createRotateButton(CraftingGrid grid, int x, int y) {
        return internalMethods.createRotateButton(grid, null, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within TweakProvider.initGui()
     *
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
     *
     * @param screen the parent GUI for this button, used to obtain the absolute position
     * @param relX   the relative x position the button should be placed at
     * @param relY   the relative y position the button should be placed at
     */
    public static Button createBalanceButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int relX, int relY) {
        int guiLeft = ((AbstractContainerScreenAccessor) screen).getLeftPos();
        int guiTop = ((AbstractContainerScreenAccessor) screen).getTopPos();
        return internalMethods.createBalanceButton(grid, screen, relX + guiLeft, relY + guiTop);
    }


    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within TweakProvider.initGui()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     *
     * @param screen the parent GUI for this button, used to obtain the absolute position
     * @param relX   the relative x position the button should be placed at
     * @param relY   the relative y position the button should be placed at
     */
    public static Button createRotateButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int relX, int relY) {
        int guiLeft = ((AbstractContainerScreenAccessor) screen).getLeftPos();
        int guiTop = ((AbstractContainerScreenAccessor) screen).getTopPos();
        return internalMethods.createRotateButton(grid, screen, relX + guiLeft, relY + guiTop);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within TweakProvider.initGui()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     *
     * @param screen the parent GUI for this button, used to obtain the absolute position
     * @param relX   the relative x position the button should be placed at
     * @param relY   the relative y position the button should be placed at
     */
    public static Button createClearButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int relX, int relY) {
        int guiLeft = ((AbstractContainerScreenAccessor) screen).getLeftPos();
        int guiTop = ((AbstractContainerScreenAccessor) screen).getTopPos();
        return internalMethods.createClearButton(grid, screen, relX + guiLeft, relY + guiTop);
    }

    public static AbstractWidget createTweakButton(CraftingGrid grid, int x, int y, TweakType tweak) {
        return switch (tweak) {
            case Clear -> createClearButton(grid, x, y);
            case Rotate -> createRotateButton(grid, x, y);
            case Balance -> createBalanceButton(grid, x, y);
        };
    }

    public static AbstractWidget createTweakButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int x, int y, TweakType tweak) {
        return switch (tweak) {
            case Clear -> createClearButtonRelative(grid, screen, x, y);
            case Rotate -> createRotateButtonRelative(grid, screen, x, y);
            case Balance -> createBalanceButtonRelative(grid, screen, x, y);
        };
    }
}
