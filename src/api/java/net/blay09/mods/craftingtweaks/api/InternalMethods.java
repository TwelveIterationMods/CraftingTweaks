package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;

public interface InternalMethods {
    DefaultProvider createDefaultProvider();
    GuiButton createBalanceButton(int id, int x, int y);
    GuiButton createRotateButton(int id, int x, int y);
    GuiButton createClearButton(int id, int x, int y);
}
