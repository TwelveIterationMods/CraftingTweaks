package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public interface InternalMethods {
    void registerProvider(Class<? extends Container> containerClass, TweakProvider provider);
    SimpleTweakProvider registerSimpleProvider(String modid, Class<? extends Container> containerClass);
    DefaultProvider createDefaultProvider();
    DefaultProviderV2 createDefaultProviderV2();
    GuiButton createBalanceButton(int id, GuiContainer parentGui, int x, int y);
    GuiButton createRotateButton(int id, GuiContainer parentGui, int x, int y);
    GuiButton createClearButton(int id, GuiContainer parentGui, int x, int y);
}
