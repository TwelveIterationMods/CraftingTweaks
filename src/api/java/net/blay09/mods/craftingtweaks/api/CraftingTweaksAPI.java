package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;

public class CraftingTweaksAPI {

    private static InternalMethods internalMethods;

    /**
     * Internal Method. Stay away.
     * @param internalMethods I said stay away.
     */
    public static void setupAPI(InternalMethods internalMethods) {
        CraftingTweaksAPI.internalMethods = internalMethods;
    }

    /**
     * For simple crafting tables that follow Vanilla's standard, you can use this instead of the registerProvider() method that allows custom implementations.
     * Make sure to set things up properly in the returned SimpleTweakProvider (e.g. setPhantomItems(true) if your grid uses phantom items)
     * @param modid the mod id the container is part of
     * @param containerClass the container containing a default crafting grid
     * @return a SimpleTweakProvider instance that allows further control over the provider settings
     */
    public static <T extends Container> SimpleTweakProvider<T> registerSimpleProvider(String modid, Class<T> containerClass) {
        return internalMethods.registerSimpleProvider(modid, containerClass);
    }

    /**
     * Registers a tweak provider for the supplied container class.
     * For simple crafting tables that follow Vanilla's standard, consider registerWithDefaultProvider instead.
     * @param containerClass the container this provider is used for
     * @param provider the tweak provider implementation for the given container
     */
    public static <T extends Container> void registerProvider(Class<T> containerClass, TweakProvider<T> provider) {
        internalMethods.registerProvider(containerClass, provider);
    }

    /**
     * Returns a default provider implementation you can use within your TweakProvider
     * @return default provider implementation with functions to be called from a TweakProvider
     */
    public static DefaultProviderV2 createDefaultProviderV2() {
        return internalMethods.createDefaultProviderV2();
    }

    /**
     * Creates a Crafting Tweaks button to balance the grid. To be called from within TweakProvider.initGui()
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static Button createBalanceButton(int id, int x, int y) {
        return internalMethods.createBalanceButton(id, null, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to balance the grid. To be called from within TweakProvider.initGui().
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param parentGui the parent GUI for this button, used to obtain the absolute position
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     */
    public static Button createBalanceButton(int id, ContainerScreen<?> parentGui, int x, int y) {
        return internalMethods.createBalanceButton(id, parentGui, x, y);
    }


    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within TweakProvider.initGui()
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static Button createRotateButton(int id, int x, int y) {
        return internalMethods.createRotateButton(id, null, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within TweakProvider.initGui()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param parentGui the parent GUI for this button, used to obtain the absolute position
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     */
    public static Button createRotateButton(int id, ContainerScreen<?> parentGui, int x, int y) {
        return internalMethods.createRotateButton(id, parentGui, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within TweakProvider.initGui()
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static Button createClearButton(int id, int x, int y) {
        return internalMethods.createClearButton(id, null, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within TweakProvider.initGui()
     * Buttons will stay relative to the initial guiLeft/guiTop of the passed in parentGui.
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param parentGui the parent GUI for this button, used to obtain the absolute position
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     */
    public static Button createClearButton(int id, ContainerScreen<?> parentGui, int x, int y) {
        return internalMethods.createClearButton(id, parentGui, x, y);
    }

    /**
     * @deprecated Use createBalanceButton with parentGui and absolute position
     */
    @Deprecated
    public static Button createBalanceButtonRelative(int id, ContainerScreen<?> parentGui, int x, int y) {
        return createBalanceButton(id, parentGui, x - parentGui.getGuiLeft(), y - parentGui.getGuiTop());
    }

    /**
     * @deprecated Use createRotateButton with parentGui and absolute position
     */
    @Deprecated
    public static Button createRotateButtonRelative(int id, ContainerScreen<?> parentGui, int x, int y) {
        return createRotateButton(id, parentGui, x - parentGui.getGuiLeft(), y - parentGui.getGuiTop());
    }

    /**
     * @deprecated Use createClearButton with parentGui and absolute position
     */
    @Deprecated
    public static Button createClearButtonRelative(int id, ContainerScreen<?> parentGui, int x, int y) {
        return createClearButton(id, parentGui, x - parentGui.getGuiLeft(), y - parentGui.getGuiTop());
    }
}
