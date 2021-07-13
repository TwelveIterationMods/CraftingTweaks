package net.blay09.mods.craftingtweaks.api;

public interface CraftingGridDecorator {
    void disableTweak(TweakType tweak);
    void disableAllTweaks();
    void usePhantomItems();
    void hideTweakButton(TweakType tweak);
    void hideAllTweakButtons();
    void setButtonAlignment(ButtonAlignment alignment);
    void setButtonPosition(TweakType tweak, int x, int y);
}
