package net.blay09.mods.craftingtweaks.api;

import java.util.Optional;

public interface GridGuiSettings {
    default boolean isButtonVisible(TweakType tweak) {
        return true;
    }

    default ButtonAlignment getButtonAlignment() {
        return ButtonAlignment.LEFT;
    }

    default int getButtonAlignmentOffsetX() {
        return 0;
    }

    default int getButtonAlignmentOffsetY() {
        return 0;
    }

    default ButtonStyle getButtonStyle() {
        return CraftingTweaksButtonStyles.DEFAULT;
    }

    default Optional<ButtonPosition> getButtonPosition(TweakType tweak) {
        return Optional.empty();
    }
}
