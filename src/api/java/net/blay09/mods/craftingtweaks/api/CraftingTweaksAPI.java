package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;

public class CraftingTweaksAPI {

    private static InternalMethods internalMethods;

    public static void setupAPI(InternalMethods internalMethods) {
        CraftingTweaksAPI.internalMethods = internalMethods;
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
