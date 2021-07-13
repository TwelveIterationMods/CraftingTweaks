package net.blay09.mods.craftingtweaks.api;

import java.util.Optional;

public interface GridGuiSettings {
    default boolean isButtonVisible(TweakType tweak) {
        return true;
    }

    default ButtonAlignment getButtonAlignment() {
        return ButtonAlignment.LEFT;
    }

    default Optional<ButtonPosition> getButtonPosition(TweakType tweak) {
        return Optional.empty();
    }
}
