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
     * For simple crafting tables that follow Vanilla's standard, you can use this instead of the registerProvider() method that allows custom implementations.
     * The following requirements must be met:
     * [*] Your container contains only one crafting grid
     * [*] Your container does not use phantom items
     * If your container does not meet these requirements, use registerProvider() and provide a custom implementation (also see createDefaultProvider)
     * @param modid the mod id the container is part of
     * @param containerClass the container containing a default crafting grid
     * @return a SimpleTweakProvider instance that allows further control over the provider settings
     */
    public static SimpleTweakProvider registerSimpleProvider(String modid, Class<? extends Container> containerClass) {
        return internalMethods.registerSimpleProvider(modid, containerClass);
    }

    /**
     * Registers a tweak provider for the supplied container class.
     * For simple crafting tables that follow Vanilla's standard, consider registerWithDefaultProvider instead.
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
