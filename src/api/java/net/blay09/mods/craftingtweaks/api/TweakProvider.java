package net.blay09.mods.craftingtweaks.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import java.util.List;

public interface TweakProvider {

    void clearGrid(EntityPlayer entityPlayer, Container container);
    void rotateGrid(EntityPlayer entityPlayer, Container container);
    void balanceGrid(EntityPlayer entityPlayer, Container container);

    @SideOnly(Side.CLIENT)
    void initGui(GuiContainer guiContainer, List buttonList);
}
