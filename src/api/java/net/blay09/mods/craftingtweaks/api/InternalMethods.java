package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nullable;

public interface InternalMethods {
    <T extends Container> void registerProvider(Class<T> containerClass, TweakProvider<T> provider);

    <T extends Container> SimpleTweakProvider<T> registerSimpleProvider(String modid, Class<T> containerClass);

    DefaultProviderV2 createDefaultProviderV2();

    Button createBalanceButton(int id, @Nullable ContainerScreen<?> parentGui, int x, int y);

    Button createRotateButton(int id, @Nullable ContainerScreen<?> parentGui, int x, int y);

    Button createClearButton(int id, @Nullable ContainerScreen<?> parentGui, int x, int y);
}
