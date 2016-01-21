package net.blay09.mods.craftingtweaks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiTweakButton extends GuiImageButton {

    public enum TweakOption {
        Rotate,
        Balance,
        Clear
    }

    private final TweakOption tweakOption;
    private final int tweakId;
    private final GuiContainer parentGui;

    public GuiTweakButton(int xPosition, int yPosition, int texCoordX, int texCoordY, TweakOption tweakOption, int tweakId) {
        this(null, xPosition, yPosition, texCoordX, texCoordY, tweakOption, tweakId);
    }

    public GuiTweakButton(GuiContainer parentGui, int xPosition, int yPosition, int texCoordX, int texCoordY, TweakOption tweakOption, int tweakId) {
        super(-1, xPosition, yPosition, texCoordX, texCoordY);
        this.parentGui = parentGui;
        this.tweakOption = tweakOption;
        this.tweakId = tweakId;
    }

    public TweakOption getTweakOption() {
        return tweakOption;
    }

    public int getTweakId() {
        return tweakId;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        int oldX = xPosition;
        int oldY = yPosition;
        // If parentGui is set, we only store the relative position in the button for mods that do hacky things where guiLeft/guiTop constantly changes
        if(parentGui != null) {
            xPosition += parentGui.guiLeft;
            yPosition += parentGui.guiTop;
        }
        boolean result = super.mousePressed(mc, mouseX, mouseY);
        xPosition = oldX;
        yPosition = oldY;
        return result;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        int oldX = xPosition;
        int oldY = yPosition;
        // If parentGui is set, we only store the relative position in the button for mods that do hacky things where guiLeft/guiTop constantly changes
        if(parentGui != null) {
            xPosition += parentGui.guiLeft;
            yPosition += parentGui.guiTop;
        }
        super.drawButton(mc, mouseX, mouseY);
        xPosition = oldX;
        yPosition = oldY;
    }
}
