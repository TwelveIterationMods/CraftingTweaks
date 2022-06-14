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
        });
        this.texCoordX = texCoordX;
        this.texCoordY = texCoordY;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        isHovered = active && visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        blit(matrixStack, x, y, texCoordX, active ? texCoordY + (isHovered ? 16 : 0) : texCoordY + 32, 16, 16); // blit

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

}
