package net.blay09.mods.craftingtweaks.api;

import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.impl.*;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public void registerCraftingGridProvider(CraftingGridProvider provider) {
        CraftingTweaksProviderManager.registerProvider(provider);
    }

    @Override
    public void unregisterCraftingGridProvider(CraftingGridProvider provider) {
        CraftingTweaksProviderManager.unregisterProvider(provider);
    }

    @Override
    public GridTransferHandler<AbstractContainerMenu> defaultTransferHandler() {
        return new DefaultGridTransferHandler();
    }

    @Override
    public GridBalanceHandler<AbstractContainerMenu> defaultBalanceHandler() {
        return new DefaultGridBalanceHandler();
    }

    @Override
    public GridClearHandler<AbstractContainerMenu> defaultClearHandler() {
        return new DefaultGridClearHandler();
    }

    @Override
    public GridRotateHandler<AbstractContainerMenu> defaultRotateHandler() {
        return new DefaultGridRotateHandler();
    }

    @Override
    public GridRotateHandler<AbstractContainerMenu> defaultFourByFourRotateHandler() {
        return new DefaultFourByFourGridRotateHandler();
    }

}
