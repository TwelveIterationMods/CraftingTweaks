package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public interface InternalClientMethods {
    Button createTweakButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y, ButtonStyle style, final TweakType tweakType, final TweakType altTweak);

    <TScreen extends AbstractContainerScreen<TMenu>, TMenu extends AbstractContainerMenu> void registerCraftingGridGuiHandler(Class<TScreen> clazz, GridGuiHandler handler);
}
