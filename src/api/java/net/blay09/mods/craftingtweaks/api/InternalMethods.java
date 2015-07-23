package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;

public interface InternalMethods {
    void registerProvider(Class<? extends Container> containerClass, TweakProvider provider);
    DefaultProvider createDefaultProvider();
    GuiButton createBalanceButton(int id, int x, int y);
    GuiButton createRotateButton(int id, int x, int y);
    GuiButton createClearButton(int id, int x, int y);
}
