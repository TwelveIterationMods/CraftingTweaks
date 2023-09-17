package net.blay09.mods.craftingtweaks.api;

import java.util.EnumMap;

public class ButtonStyle {

    private final EnumMap<TweakType, ButtonProperties> properties = new EnumMap<>(TweakType.class);
    private final int spacingX;
    private final int spacingY;

    public ButtonStyle(int spacingX, int spacingY) {
        this.spacingX = spacingX;
        this.spacingY = spacingY;
    }

    public ButtonStyle withTweak(TweakType tweakType, ButtonProperties properties) {
        this.properties.put(tweakType, properties);
        return this;
    }

    public ButtonProperties getTweak(TweakType tweakType) {
        return properties.get(tweakType);
    }

    public int getSpacingX() {
        return spacingX;
    }

    public int getSpacingY() {
        return spacingY;
    }
}
