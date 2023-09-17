package net.blay09.mods.craftingtweaks.api;

import java.util.EnumMap;

public class ButtonStyle {

    private final EnumMap<TweakType, ButtonProperties> properties = new EnumMap<>(TweakType.class);

    public ButtonStyle withTweak(TweakType tweakType, ButtonProperties properties) {
        this.properties.put(tweakType, properties);
        return this;
    }

    public ButtonProperties getTweak(TweakType tweakType) {
        return properties.get(tweakType);
    }
}
