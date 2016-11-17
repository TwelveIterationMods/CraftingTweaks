package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import javax.annotation.Nullable;

public interface InternalMethods {
    <T extends Container> void registerProvider(Class<T> containerClass, TweakProvider<T> provider);
    <T extends Container> SimpleTweakProvider<T> registerSimpleProvider(String modid, Class<T> containerClass);
    DefaultProviderV2 createDefaultProviderV2();
    GuiButton createBalanceButton(int id, @Nullable GuiContainer parentGui, int x, int y);
    GuiButton createRotateButton(int id, @Nullable GuiContainer parentGui, int x, int y);
    GuiButton createClearButton(int id, @Nullable GuiContainer parentGui, int x, int y);
}
