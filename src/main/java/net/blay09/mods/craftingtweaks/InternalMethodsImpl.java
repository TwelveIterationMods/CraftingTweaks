package net.blay09.mods.craftingtweaks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.api.*;
import net.blay09.mods.craftingtweaks.client.GuiTweakButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public void registerProvider(Class<? extends Container> containerClass, TweakProvider provider) {
        CraftingTweaks.instance.registerProvider(containerClass, provider);
    }

    @Override
    public SimpleTweakProvider registerSimpleProvider(String modid, Class<? extends Container> containerClass) {
        SimpleTweakProvider simpleTweakProvider = new SimpleTweakProviderImpl(modid);
        CraftingTweaks.instance.registerProvider(containerClass, simpleTweakProvider);
        return simpleTweakProvider;
    }

    @Override
    public DefaultProvider createDefaultProvider() {
        return new DefaultProviderImpl();
    }

    @Override
    public DefaultProviderV2 createDefaultProviderV2() {
        return new DefaultProviderV2Impl();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createBalanceButton(int id, GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 48, 0, GuiTweakButton.TweakOption.Balance, id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createRotateButton(int id, GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 16, 0, GuiTweakButton.TweakOption.Rotate, id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createClearButton(int id, GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 32, 0, GuiTweakButton.TweakOption.Clear, id);
    }

}
