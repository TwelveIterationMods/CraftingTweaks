package net.blay09.mods.craftingtweaks.client;

public class GuiTweakButton extends GuiImageButton {

    public enum TweakOption {
        Rotate,
        Balance,
        Clear
    }

    private final TweakOption tweakOption;
    private final int tweakId;

    public GuiTweakButton(int xPosition, int yPosition, int texCoordX, int texCoordY, TweakOption tweakOption, int tweakId) {
        super(-1, xPosition, yPosition, texCoordX, texCoordY);
        this.tweakOption = tweakOption;
        this.tweakId = tweakId;
    }

    public TweakOption getTweakOption() {
        return tweakOption;
    }

    public int getTweakId() {
        return tweakId;
    }

}
