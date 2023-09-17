package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public interface InternalClientMethods {
    Button createBalanceButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y, ButtonStyle style);

    Button createRotateButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y, ButtonStyle style);

    Button createClearButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y, ButtonStyle style);

    <TScreen extends AbstractContainerScreen<TMenu>, TMenu extends AbstractContainerMenu> void registerCraftingGridGuiHandler(Class<TScreen> clazz, GridGuiHandler handler);
}
