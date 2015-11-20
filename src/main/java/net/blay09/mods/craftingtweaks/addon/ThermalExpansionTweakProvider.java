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
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.List;

public class ThermalExpansionTweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private Field craftMatrixField;

    @Override
    public boolean load() {
        try {
            Class clazz = Class.forName("cofh.thermalexpansion.gui.container.device.ContainerWorkbench");
            craftMatrixField = clazz.getField("craftMatrix");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ItemStack transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack) {
        try {
            IInventory craftMatrix = (IInventory) craftMatrixField.get(container);
            return defaultProvider.transferIntoGrid(entityPlayer, container, craftMatrix, itemStack);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return itemStack;
        }
    }

    @Override
    public ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack, int index) {
        try {
            IInventory craftMatrix = (IInventory) craftMatrixField.get(container);
            return defaultProvider.putIntoGrid(entityPlayer, container, craftMatrix, itemStack, index);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return itemStack;
        }
    }

    @Override
    public IInventory getCraftMatrix(EntityPlayer entityPlayer, Container container, int id) {
        try {
            return (IInventory) craftMatrixField.get(container);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) craftMatrixField.get(container);
            for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                craftMatrix.setInventorySlotContents(i, null);
            }
            container.detectAndSendChanges();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) craftMatrixField.get(container);
            defaultProvider.rotateGrid(entityPlayer, container, craftMatrix);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingTop = 16 + 18 + 3;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.width / 2 + 10, guiContainer.guiTop + paddingTop));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean areHotkeysEnabled(EntityPlayer entityPlayer, Container container) {
        return true;
    }

    @Override
    public String getModId() {
        return "ThermalExpansion";
    }
}
