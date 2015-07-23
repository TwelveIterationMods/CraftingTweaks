package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.InternalMethods;
import net.blay09.mods.craftingtweaks.client.GuiTweakButton;
import net.minecraft.client.gui.GuiButton;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public DefaultProvider createDefaultProvider() {
        return new DefaultProviderImpl();
    }

    @Override
    public GuiButton createBalanceButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 48, 0, GuiTweakButton.TweakOption.Balance, id);
    }

    @Override
    public GuiButton createRotateButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 16, 0, GuiTweakButton.TweakOption.Rotate, id);
    }

    @Override
    public GuiButton createClearButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 32, 0, GuiTweakButton.TweakOption.Clear, id);
    }

}
