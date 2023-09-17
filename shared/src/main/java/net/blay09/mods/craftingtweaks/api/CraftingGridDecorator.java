package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface CraftingGridDecorator {
    CraftingGridDecorator disableTweak(TweakType tweak);
    CraftingGridDecorator disableAllTweaks();
    CraftingGridDecorator usePhantomItems();
    CraftingGridDecorator rotateHandler(GridRotateHandler<AbstractContainerMenu> rotateHandler);
    CraftingGridDecorator balanceHandler(GridBalanceHandler<AbstractContainerMenu> balanceHandler);
    CraftingGridDecorator clearHandler(GridClearHandler<AbstractContainerMenu> clearHandler);
    CraftingGridDecorator transferHandler(GridTransferHandler<AbstractContainerMenu> transferHandler);
    CraftingGridDecorator hideTweakButton(TweakType tweak);
    CraftingGridDecorator hideAllTweakButtons();
    CraftingGridDecorator setButtonAlignment(ButtonAlignment alignment);

    CraftingGridDecorator setButtonStyle(ButtonStyle style);

    CraftingGridDecorator setButtonPosition(TweakType tweak, int x, int y);
}
