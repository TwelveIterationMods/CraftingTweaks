package net.blay09.mods.craftingtweaks.addon.forestry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ForestryCarpenterOldTweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private Field tileEntityField;
    private Method getCraftingInventory;

    @Override
    public boolean load() {
        try {
            Class containerClass = Class.forName("forestry.core.gui.ContainerTile");
            tileEntityField = containerClass.getDeclaredField("tile");
            tileEntityField.setAccessible(true);
            Class tileClass = Class.forName("forestry.factory.gadgets.MachineCarpenter");
            getCraftingInventory = tileClass.getMethod("getCraftingInventory");
            return true;
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchFieldException ignored) {
        } catch (NoSuchMethodException ignored) {
        }
        return false;
    }

    @Override
    public ItemStack transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack) {
        try {
            IInventory craftMatrix = (IInventory) getCraftingInventory.invoke(tileEntityField.get(container));
            return defaultProvider.transferIntoGrid(entityPlayer, container, craftMatrix, itemStack);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return itemStack;
    }

    @Override
    public ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack, int index) {
        try {
            IInventory craftMatrix = (IInventory) getCraftingInventory.invoke(tileEntityField.get(container));
            return defaultProvider.putIntoGrid(entityPlayer, container, craftMatrix, itemStack, index);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return itemStack;
    }

    @Override
    public IInventory getCraftMatrix(EntityPlayer entityPlayer, Container container, int id) {
        try {
            return (IInventory) getCraftingInventory.invoke(tileEntityField.get(container));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) getCraftingInventory.invoke(tileEntityField.get(container));
            for(int i = 0; i < 9; i++) {
                craftMatrix.setInventorySlotContents(i, null);
            }
            container.detectAndSendChanges();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) getCraftingInventory.invoke(tileEntityField.get(container));
            defaultProvider.rotateGrid(entityPlayer, container, craftMatrix, 0, 9, defaultProvider.getRotationHandler());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingTop = 73;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft + 19, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft + 37, guiContainer.guiTop + paddingTop));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean areHotkeysEnabled(EntityPlayer entityPlayer, Container container) {
        return true;
    }

    @Override
    public String getModId() {
        return "Forestry";
    }
}
