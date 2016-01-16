package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.InternalMethods;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.client.GuiTweakButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    @SideOnly(Side.CLIENT)
    public GuiButton createBalanceButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 48, 0, GuiTweakButton.TweakOption.Balance, id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createRotateButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 16, 0, GuiTweakButton.TweakOption.Rotate, id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createClearButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 32, 0, GuiTweakButton.TweakOption.Clear, id);
    }

}
