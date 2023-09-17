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
     * Creates a Crafting Tweaks button to balance the grid. To be called from within GridGuiHandler.createButtons()
     *
     * @param grid the crafting grid to create the button for
     * @param x    the x position the button should be placed at
     * @param y    the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     * @deprecated Use {@link #createTweakButton(CraftingGrid, int, int, TweakType)} instead.
     */
    @Deprecated
    public static Button createBalanceButton(CraftingGrid grid, int x, int y) {
        return (Button) createTweakButton(grid, x, y, TweakType.Balance);
    }

    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within GridGuiHandler.createButtons()
     *
     * @param grid the crafting grid to create the button for
     * @param x    the x position the button should be placed at
     * @param y    the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     * @deprecated Use {@link #createTweakButton(CraftingGrid, int, int, TweakType)} instead.
     */
    @Deprecated
    public static Button createRotateButton(CraftingGrid grid, int x, int y) {
        return (Button) createTweakButton(grid, x, y, TweakType.Rotate);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within GridGuiHandler.createButtons()
     *
     * @param grid the crafting grid to create the button for
     * @param x    the x position the button should be placed at
     * @param y    the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     * @deprecated Use {@link #createTweakButton(CraftingGrid, int, int, TweakType)} instead.
     */
    @Deprecated
    public static Button createClearButton(CraftingGrid grid, int x, int y) {
        return (Button) createTweakButton(grid, x, y, TweakType.Clear);
    }

    /**
     * Creates a Crafting Tweaks button to balance the grid. To be called from within GridGuiHandler.createButtons().
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     *
     * @param grid   the crafting grid to create the button for
     * @param screen the parent GUI for this button, to which the position will be relative to
     * @param relX   the relative x position the button should be placed at
     * @param relY   the relative y position the button should be placed at
     * @deprecated Use {@link #createTweakButton(CraftingGrid, int, int, TweakType)} instead.
     */
    @Deprecated
    public static Button createBalanceButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int relX, int relY) {
        return (Button) createTweakButtonRelative(grid, screen, relX, relY, TweakType.Balance);
    }


    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within GridGuiHandler.createButtons()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     *
     * @param grid   the crafting grid to create the button for
     * @param screen the parent GUI for this button, to which the position will be relative to
     * @param relX   the relative x position the button should be placed at
     * @param relY   the relative y position the button should be placed at
     * @deprecated Use {@link #createTweakButton(CraftingGrid, int, int, TweakType)} instead.
     */
    @Deprecated
    public static Button createRotateButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int relX, int relY) {
        return (Button) createTweakButtonRelative(grid, screen, relX, relY, TweakType.Rotate);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within GridGuiHandler.createButtons()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     *
     * @param grid   the crafting grid to create the button for
     * @param screen the parent GUI for this button, to which the position will be relative to
     * @param relX   the relative x position the button should be placed at
     * @param relY   the relative y position the button should be placed at
     * @deprecated Use {@link #createTweakButton(CraftingGrid, int, int, TweakType)} instead.
     */
    @Deprecated
    public static Button createClearButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int relX, int relY) {
        return (Button) createTweakButtonRelative(grid, screen, relX, relY, TweakType.Clear);
    }

    /**
     * Creates a Crafting Tweaks button of the specified type. To be called from within GridGuiHandler.createButtons()
     *
     * @param grid  the crafting grid to create the button for
     * @param x     the x position the button should be placed at
     * @param y     the y position the button should be placed at
     * @param tweak the tweak type to create the button for
     */
    public static AbstractWidget createTweakButton(CraftingGrid grid, int x, int y, TweakType tweak) {
        return createTweakButton(grid, x, y, tweak, ButtonStyle.DEFAULT);
    }

    /**
     * Creates a Crafting Tweaks button of the specified type. To be called from within GridGuiHandler.createButtons()
     *
     * @param grid  the crafting grid to create the button for
     * @param x     the x position the button should be placed at
     * @param y     the y position the button should be placed at
     * @param tweak the tweak type to create the button for
     * @param style the style of the button
     */
    public static AbstractWidget createTweakButton(CraftingGrid grid, int x, int y, TweakType tweak, ButtonStyle style) {
        return switch (tweak) {
            case Clear -> internalMethods.createClearButton(grid, null, x, y, style);
            case Rotate -> internalMethods.createRotateButton(grid, null, x, y, style);
            case Balance -> internalMethods.createBalanceButton(grid, null, x, y, style);
        };
    }
    
    /**
     * Creates a Crafting Tweaks button of the specified type. To be called from within GridGuiHandler.createButtons()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     *
     * @param grid  the crafting grid to create the button for
     * @param x     the x position the button should be placed at, relative to the screen
     * @param y     the y position the button should be placed at, relative to the screen
     * @param tweak the tweak type to create the button for
     */
    public static AbstractWidget createTweakButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int x, int y, TweakType tweak) {
        return createTweakButtonRelative(grid, screen, x, y, tweak, ButtonStyle.DEFAULT);
    }

    /**
     * Creates a Crafting Tweaks button of the specified type. To be called from within GridGuiHandler.createButtons()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     *
     * @param grid  the crafting grid to create the button for
     * @param screen the parent GUI for this button, to which the position will be relative to
     * @param x     the x position the button should be placed at, relative to the screen
     * @param y     the y position the button should be placed at, relative to the screen
     * @param tweak the tweak type to create the button for
     * @param style the style of the button
     */
    public static AbstractWidget createTweakButtonRelative(CraftingGrid grid, AbstractContainerScreen<?> screen, int x, int y, TweakType tweak, ButtonStyle style) {
        int guiLeft = ((AbstractContainerScreenAccessor) screen).getLeftPos();
        int guiTop = ((AbstractContainerScreenAccessor) screen).getTopPos();
        return switch (tweak) {
            case Clear -> internalMethods.createClearButton(grid, screen, x + guiLeft, y + guiTop, style);
            case Rotate -> internalMethods.createRotateButton(grid, screen, x + guiLeft, y + guiTop, style);
            case Balance -> internalMethods.createBalanceButton(grid, screen, x + guiLeft, y + guiTop, style);
        };
    }
}
