package net.blay09.mods.craftingtweaks.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface CraftingGrid {

    ResourceLocation getId();

    default Container getCraftingMatrix(Player player, AbstractContainerMenu menu) {
        return menu.slots.get(getGridStartSlot(player, menu)).container;
    }

    default int getGridStartSlot(Player player, AbstractContainerMenu menu) {
        return 1;
    }

    default int getGridSize(Player player, AbstractContainerMenu menu) {
        return 9;
    }

    default boolean isTweakActive(TweakType tweak) {
        return true;
    }

    default GridTransferHandler<AbstractContainerMenu> transferHandler() {
        return CraftingTweaksDefaultHandlers.defaultTransferHandler();
    }

    default GridRotateHandler<AbstractContainerMenu> rotateHandler() {
        return CraftingTweaksDefaultHandlers.defaultRotateHandler();
    }

    default GridClearHandler<AbstractContainerMenu> clearHandler() {
        return CraftingTweaksDefaultHandlers.defaultClearHandler();
    }

    default GridBalanceHandler<AbstractContainerMenu> balanceHandler() {
        return CraftingTweaksDefaultHandlers.defaultBalanceHandler();
    }

}
