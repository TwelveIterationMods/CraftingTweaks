package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class CraftingTweaksDefaultHandlers {

    private static InternalMethods internalMethods;

    /**
     * Internal Method. Stay away.
     * @param internalMethods I said stay away.
     */
    public static void setupAPI(InternalMethods internalMethods) {
        CraftingTweaksDefaultHandlers.internalMethods = internalMethods;
    }

    public static GridTransferHandler<AbstractContainerMenu> defaultTransferHandler() {
        return internalMethods.defaultTransferHandler();
    }

    public static GridBalanceHandler<AbstractContainerMenu> defaultBalanceHandler() {
        return internalMethods.defaultBalanceHandler();
    }

    public static GridClearHandler<AbstractContainerMenu> defaultClearHandler() {
        return internalMethods.defaultClearHandler();
    }

    public static GridRotateHandler<AbstractContainerMenu> defaultRotateHandler() {
        return internalMethods.defaultRotateHandler();
    }

    public static GridRotateHandler<AbstractContainerMenu> defaultFourByFourRotateHandler() {
        return internalMethods.defaultFourByFourRotateHandler();
    }
}
