package net.blay09.mods.craftingtweaks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GuiImageButton extends Button {

    private static final ResourceLocation texture = new ResourceLocation(CraftingTweaks.MOD_ID, "gui.png");

    protected int texCoordX;
    protected int texCoordY;

    public GuiImageButton(int x, int y, int texCoordX, int texCoordY) {
        super(x, y, 16, 16, Component.empty(), it -> {
        }, Button.DEFAULT_NARRATION);
        this.texCoordX = texCoordX;
        this.texCoordY = texCoordY;
    }

    @Override
    public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        isHovered = active && visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        blit(poseStack, getX(), getY(), texCoordX, active ? texCoordY + (isHovered ? 16 : 0) : texCoordY + 32, 16, 16);
    }

}
