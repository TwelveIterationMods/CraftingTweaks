package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiRoundMenu extends Gui {

    private static final ResourceLocation texture = new ResourceLocation(CraftingTweaks.MOD_ID, "gui.png");

    private final int centerX;
    private final int centerY;

    public GuiRoundMenu(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void drawMenu(Minecraft mc, int mouseX, int mouseY) {
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.renderEngine.bindTexture(texture);
        float oldZLevel = zLevel;
        zLevel = 500;
        drawTexturedModalRect(centerX - 30, centerY - 30, 0, 48, 60, 60);
        int insideSize = 8;
        int outsideSize = 60;
        boolean insideCenter = (mouseX > centerX - insideSize && mouseX < centerX + insideSize) && (mouseY > centerY - insideSize && mouseY < centerY + insideSize);
        if(mouseX >= centerX - outsideSize && mouseX < centerX + outsideSize && mouseY >= centerY - outsideSize && mouseY < centerY + outsideSize && !insideCenter) {
            double angle = Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX)) + 180;
            if(angle >= 45 &&  angle < 135) {
                drawTexturedModalRect(centerX - 30, centerY - 30 - 10, 0, 108, 60, 41);
            } else if(angle >= 135 && angle < 225) {
                drawTexturedModalRect(centerX - 1, centerY - 30, 101, 108, 41, 60);
            } else if(angle >= 225 && angle < 315) {
                drawTexturedModalRect(centerX - 30, centerY - 1, 0, 149, 60, 41);
            } else if(angle >= 315 || angle < 45) {
                drawTexturedModalRect(centerX - 30 - 10, centerY - 30, 60, 108, 41, 60);
            }
        }
        zLevel = oldZLevel;
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
    }

}
