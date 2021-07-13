package net.blay09.mods.craftingtweaks.api;

import java.util.Optional;

public interface GridGuiSettingsProvider {
    boolean isButtonVisible(TweakType tweak);

    ButtonAlignment getButtonAlignment();

    default Optional<ButtonPosition> getButtonPosition(TweakType tweak) {
        return Optional.empty();
    }
}
