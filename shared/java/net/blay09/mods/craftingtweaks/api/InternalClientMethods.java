package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jetbrains.annotations.Nullable;

public interface InternalClientMethods {
    Button createBalanceButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y);

    Button createRotateButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y);

    Button createClearButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y);
}
