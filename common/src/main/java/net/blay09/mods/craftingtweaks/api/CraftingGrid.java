package net.blay09.mods.craftingtweaks.api;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.Nullable;

public interface CraftingGrid {

    Identifier getId();

    default @Nullable Container getCraftingMatrix(Player player, AbstractContainerMenu menu) {
        return menu.getSlot(getGridStartSlot(player, menu)).container;
    }

    default int getGridStartSlot(Player player, AbstractContainerMenu menu) {
        return 1;
    }

    default int getGridSize(Player player, AbstractContainerMenu menu) {
        return 9;
    }

    default int getGridWidth(Player player, AbstractContainerMenu menu) {
        return (int) Mth.sqrt(getGridSize(player, menu));
    }

    default int getGridHeight(Player player, AbstractContainerMenu menu) {
        return (int) Mth.sqrt(getGridSize(player, menu));
    }

    default boolean isTweakActive(TweakType tweak) {
        return true;
    }

    default GridTransferHandler<AbstractContainerMenu> transferHandler() {
        return CraftingTweaksDefaultHandlers.defaultTransferHandler();
    }

    default GridRotateHandler<AbstractContainerMenu> rotateHandler() {
        return CraftingTweaksDefaultHandlers.defaultRectangularRotateHandler();
    }

    default GridClearHandler<AbstractContainerMenu> clearHandler() {
        return CraftingTweaksDefaultHandlers.defaultClearHandler();
    }

    default GridBalanceHandler<AbstractContainerMenu> balanceHandler() {
        return CraftingTweaksDefaultHandlers.defaultBalanceHandler();
    }

    default GridRefillHandler<AbstractContainerMenu> refillHandler() {
        return CraftingTweaksDefaultHandlers.defaultRefillHandler();
    }

}
