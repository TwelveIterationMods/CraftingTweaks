package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiImageButton extends GuiButton {

    private static final ResourceLocation texture = new ResourceLocation(CraftingTweaks.MOD_ID, "gui.png");

    protected int texCoordX;
    protected int texCoordY;

    public GuiImageButton(int id, int x, int y, int texCoordX, int texCoordY) {
        super(id, x, y, 16, 16, "");
        this.texCoordX = texCoordX;
        this.texCoordY = texCoordY;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        hovered = this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        drawTexturedModalRect(x, y, texCoordX, enabled ? texCoordY + (hovered ? 16 : 0) : texCoordY + 32, 16, 16);
    }

}
