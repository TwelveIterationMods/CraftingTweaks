package net.blay09.mods.craftingtweaks.api;

public class ButtonStateProperties {
    private final int textureX;
    private final int textureY;

    public ButtonStateProperties(int textureX, int textureY) {
        this.textureX = textureX;
        this.textureY = textureY;
    }

    public int getTextureX() {
        return textureX;
    }

    public int getTextureY() {
        return textureY;
    }
}
