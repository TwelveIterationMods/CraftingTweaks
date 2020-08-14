package net.blay09.mods.craftingtweaks.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class GuiImageButton extends Button {

    private static final ResourceLocation texture = new ResourceLocation(CraftingTweaks.MOD_ID, "gui.png");

    protected int texCoordX;
    protected int texCoordY;

    public GuiImageButton(int x, int y, int texCoordX, int texCoordY) {
        super(x, y, 16, 16, new StringTextComponent(""), it -> {
        }, (button, matrixStack, mouseX, mouseY) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.currentScreen != null && button instanceof ITooltipProvider) {
                mc.currentScreen.func_243308_b(matrixStack, ((ITooltipProvider) button).getTooltip(), mouseX, mouseY);
            }
        });
        this.texCoordX = texCoordX;
        this.texCoordY = texCoordY;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        isHovered = active && visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        blit(matrixStack, x, y, texCoordX, active ? texCoordY + (isHovered ? 16 : 0) : texCoordY + 32, 16, 16); // blit

        if (this.isHovered()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

}
