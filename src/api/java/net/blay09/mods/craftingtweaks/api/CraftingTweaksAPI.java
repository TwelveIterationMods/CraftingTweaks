package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;

public class CraftingTweaksAPI {

    private static InternalMethods internalMethods;

    public static void setupAPI(InternalMethods internalMethods) {
        CraftingTweaksAPI.internalMethods = internalMethods;
    }

    public static void registerProvider(Class<? extends Container> containerClass, TweakProvider provider) {
        internalMethods.registerProvider(containerClass, provider);
    }

    public static DefaultProvider createDefaultProvider() {
        return internalMethods.createDefaultProvider();
    }

    public static GuiButton createBalanceButton(int id, int x, int y) {
        return internalMethods.createBalanceButton(id, x, y);
    }

    public static GuiButton createRotateButton(int id, int x, int y) {
        return internalMethods.createRotateButton(id, x, y);
    }

    public static GuiButton createClearButton(int id, int x, int y) {
        return internalMethods.createClearButton(id, x, y);
    }

}
