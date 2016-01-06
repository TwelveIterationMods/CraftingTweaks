package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiImageButton extends GuiButton {

    private static final ResourceLocation texture = new ResourceLocation(CraftingTweaks.MOD_ID, "gui.png");

    private final int texCoordX;
    private final int texCoordY;

    public GuiImageButton(int id, int x, int y, int texCoordX, int texCoordY) {
        super(id, x, y, 16, 16, "");
        this.texCoordX = texCoordX;
        this.texCoordY = texCoordY;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        boolean isHovered = this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        mc.renderEngine.bindTexture(texture);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        drawTexturedModalRect(xPosition, yPosition, texCoordX, texCoordY + (isHovered ? 16 : 0), 16, 16);
    }
}
