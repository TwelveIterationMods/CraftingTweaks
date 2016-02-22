package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public interface InternalMethods {
    <T extends Container> void registerProvider(Class<T> containerClass, TweakProvider<T> provider);
    <T extends Container> SimpleTweakProvider<T> registerSimpleProvider(String modid, Class<T> containerClass);
    DefaultProvider createDefaultProvider();
    DefaultProviderV2 createDefaultProviderV2();
    GuiButton createBalanceButton(int id, GuiContainer parentGui, int x, int y);
    GuiButton createRotateButton(int id, GuiContainer parentGui, int x, int y);
    GuiButton createClearButton(int id, GuiContainer parentGui, int x, int y);
}
