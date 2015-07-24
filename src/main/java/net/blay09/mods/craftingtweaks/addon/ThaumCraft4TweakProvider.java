package net.blay09.mods.craftingtweaks.addon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

import java.lang.reflect.Field;
import java.util.List;

public class ThaumCraft4TweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private Field tileEntityField;
    private boolean isLoaded;

    public ThaumCraft4TweakProvider() {
        try {
            Class containerClass = Class.forName("thaumcraft.common.container.ContainerArcaneWorkbench");
            tileEntityField = containerClass.getDeclaredField("tileEntity");
            tileEntityField.setAccessible(true);
            isLoaded = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) tileEntityField.get(container);
            defaultProvider.clearGrid(entityPlayer, container, craftMatrix, 0, 9);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) tileEntityField.get(container);
            defaultProvider.rotateGrid(entityPlayer, container, craftMatrix, 0, 9, defaultProvider.getRotationHandler());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) tileEntityField.get(container);
            defaultProvider.balanceGrid(entityPlayer, container, craftMatrix, 0, 9);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingTop = 46;
        final int paddingLeft = 4;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft - 16 + paddingLeft, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(0, guiContainer.guiLeft - 16 + paddingLeft, guiContainer.guiTop + paddingTop + 18));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft - 16 + paddingLeft, guiContainer.guiTop + paddingTop + 36));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean areHotkeysEnabled(EntityPlayer entityPlayer, Container container) {
        return true;
    }

}
