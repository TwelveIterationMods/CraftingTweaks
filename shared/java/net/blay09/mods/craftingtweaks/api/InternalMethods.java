package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public interface InternalMethods {
    <T extends AbstractContainerMenu> void registerProvider(Class<T> containerClass, TweakProvider<T> provider);

    <T extends AbstractContainerMenu> SimpleTweakProvider<T> registerSimpleProvider(String modid, Class<T> containerClass);

    DefaultProviderV2 createDefaultProviderV2();

    Button createBalanceButton(int id, @Nullable AbstractContainerScreen<?> parentGui, int x, int y);

    Button createRotateButton(int id, @Nullable AbstractContainerScreen<?> parentGui, int x, int y);

    Button createClearButton(int id, @Nullable AbstractContainerScreen<?> parentGui, int x, int y);
}
