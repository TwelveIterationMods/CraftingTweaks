package net.blay09.mods.craftingtweaks.addon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;

import java.util.List;

public class VanillaTweakProviderImpl implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        ContainerWorkbench workbench = (ContainerWorkbench) container;
        defaultProvider.clearGrid(entityPlayer, container, workbench.craftMatrix);
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        ContainerWorkbench workbench = (ContainerWorkbench) container;
        defaultProvider.rotateGrid(entityPlayer, container, workbench.craftMatrix);
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        ContainerWorkbench workbench = (ContainerWorkbench) container;
        defaultProvider.balanceGrid(entityPlayer, container, workbench.craftMatrix);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingTop = 16;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop + 18));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop + 36));
    }

}
