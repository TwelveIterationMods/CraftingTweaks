package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.ButtonProperties;
import net.blay09.mods.craftingtweaks.api.ButtonState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GuiImageButton extends Button {

    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "gui.png");

    protected ButtonProperties properties;

    public GuiImageButton(int x, int y, ButtonProperties properties) {
        super(x, y, properties.getWidth(), properties.getHeight(), Component.empty(), it -> {
        }, Button.DEFAULT_NARRATION);
        this.properties = properties;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        isHovered = active && visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
        guiGraphics.setColor(1f, 1f, 1f, 1f);
        var state = isHovered ? ButtonState.HOVER : ButtonState.NORMAL;
        if(!active) {
            state = ButtonState.DISABLED;
        }
        var stateProperties = properties.getState(state);
        guiGraphics.blit(texture, getX(), getY(), stateProperties.getTextureX(), stateProperties.getTextureY(), width, height);
    }

}
