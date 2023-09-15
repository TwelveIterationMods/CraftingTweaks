package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface InternalMethods {
    void registerCraftingGridProvider(CraftingGridProvider provider);
    void unregisterCraftingGridProvider(CraftingGridProvider provider);

    GridTransferHandler<AbstractContainerMenu> defaultTransferHandler();

    GridBalanceHandler<AbstractContainerMenu> defaultBalanceHandler();

    GridClearHandler<AbstractContainerMenu> defaultClearHandler();

    GridRotateHandler<AbstractContainerMenu> defaultRotateHandler();

    GridRotateHandler<AbstractContainerMenu> defaultFourByFourRotateHandler();

    GridRotateHandler<AbstractContainerMenu> defaultRectangularRotateHandler();
}
