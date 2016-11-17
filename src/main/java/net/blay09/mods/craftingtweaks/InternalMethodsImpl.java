package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.*;
import net.blay09.mods.craftingtweaks.client.GuiTweakButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public <T extends Container> void registerProvider(Class<T> containerClass, TweakProvider<T> provider) {
        CraftingTweaks.instance.registerProvider(containerClass, provider);
    }

    @Override
    public <T extends Container> SimpleTweakProvider<T> registerSimpleProvider(String modid, Class<T> containerClass) {
        SimpleTweakProvider<T> simpleTweakProvider = new SimpleTweakProviderImpl<>(modid);
        CraftingTweaks.instance.registerProvider(containerClass, simpleTweakProvider);
        return simpleTweakProvider;
    }

    @Override
    public DefaultProviderV2 createDefaultProviderV2() {
        return new DefaultProviderV2Impl();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createBalanceButton(int id, @Nullable GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 48, 0, GuiTweakButton.TweakOption.Balance, id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createRotateButton(int id, @Nullable GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 16, 0, GuiTweakButton.TweakOption.Rotate, id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createClearButton(int id, @Nullable GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 32, 0, GuiTweakButton.TweakOption.Clear, id);
    }

}
