package net.blay09.mods.craftingtweaks.api;

public interface CraftingGridDecorator {
    CraftingGridDecorator disableTweak(TweakType tweak);
    CraftingGridDecorator disableAllTweaks();
    CraftingGridDecorator usePhantomItems();
    CraftingGridDecorator hideTweakButton(TweakType tweak);
    CraftingGridDecorator hideAllTweakButtons();
    CraftingGridDecorator setButtonAlignment(ButtonAlignment alignment);
    CraftingGridDecorator setButtonPosition(TweakType tweak, int x, int y);
}
