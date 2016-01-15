package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;

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
     * Registers a tweak provider for the supplied container class.
     * @param containerClass the container this provider is used for
     * @param provider the tweak provider implementation for the given container
     */
    public static void registerProvider(Class<? extends Container> containerClass, TweakProvider provider) {
        internalMethods.registerProvider(containerClass, provider);
    }

    /**
     * Returns a default provider implementation you can use within your TweakProvider
     * @return default provider implementation with functions to be called from a TweakProvider
     */
    public static DefaultProvider createDefaultProvider() {
        return internalMethods.createDefaultProvider();
    }

    /**
     * Creates a Crafting Tweaks button to balance the grid. To be called from within TweakProvider.initGui()
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static GuiButton createBalanceButton(int id, int x, int y) {
        return internalMethods.createBalanceButton(id, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to rotate the grid. To be called from within TweakProvider.initGui()
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static GuiButton createRotateButton(int id, int x, int y) {
        return internalMethods.createRotateButton(id, x, y);
    }

    /**
     * Creates a Crafting Tweaks button to clear the grid. To be called from within TweakProvider.initGui()
     * @param id the crafting grid ID this button is for (usually 0 unless there's more grids in one GUI)
     * @param x the x position the button should be placed at
     * @param y the y position the button should be placed at
     * @return a new GuiButton instance that you should add to the buttonList
     */
    public static GuiButton createClearButton(int id, int x, int y) {
        return internalMethods.createClearButton(id, x, y);
    }

}
