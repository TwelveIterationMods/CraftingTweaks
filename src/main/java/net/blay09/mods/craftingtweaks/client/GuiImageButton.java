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
                mc.currentScreen.func_238654_b_(matrixStack, ((ITooltipProvider) button).getTooltip(), mouseX, mouseY, mc.fontRenderer);
            }
        });
        this.texCoordX = texCoordX;
        this.texCoordY = texCoordY;
    }

    @Override
    public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        final boolean active = field_230693_o_;
        final boolean visible = field_230694_p_;
        final int x = field_230690_l_;
        final int y = field_230691_m_;
        final int width = field_230688_j_;
        final int height = field_230689_k_;
        // field_230692_n_ isHovered
        field_230692_n_ = active && visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        func_238474_b_(matrixStack, x, y, texCoordX, field_230693_o_ ? texCoordY + (field_230692_n_ ? 16 : 0) : texCoordY + 32, 16, 16);

        if (this.func_230449_g_()) {
            this.func_230443_a_(matrixStack, mouseX, mouseY);
        }
    }

}
