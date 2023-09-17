package net.blay09.mods.craftingtweaks.api;

import java.util.EnumMap;

public class ButtonStyle {

    private final EnumMap<TweakType, ButtonProperties> properties = new EnumMap<>(TweakType.class);
    private final int spacingX;
    private final int spacingY;
    private final int marginX;
    private final int marginY;

    public ButtonStyle(int spacingX, int spacingY, int marginX, int marginY) {
        this.spacingX = spacingX;
        this.spacingY = spacingY;
        this.marginX = marginX;
        this.marginY = marginY;
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

    public int getMarginX() {
        return marginX;
    }

    public int getMarginY() {
        return marginY;
    }
}
