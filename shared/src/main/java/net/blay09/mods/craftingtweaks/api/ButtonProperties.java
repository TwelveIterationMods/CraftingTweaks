package net.blay09.mods.craftingtweaks.api;

import java.util.EnumMap;

public class ButtonProperties {
    private final EnumMap<ButtonState, ButtonStateProperties> properties = new EnumMap<>(ButtonState.class);

    private final int width;
    private final int height;

    public ButtonProperties(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ButtonProperties withState(ButtonState buttonState, ButtonStateProperties properties) {
        this.properties.put(buttonState, properties);
        return this;
    }

    public ButtonProperties withState(ButtonState state, int textureX, int textureY) {
        return withState(state, new ButtonStateProperties(textureX, textureY));
    }

    public ButtonStateProperties getState(ButtonState buttonState) {
        return properties.get(buttonState);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
