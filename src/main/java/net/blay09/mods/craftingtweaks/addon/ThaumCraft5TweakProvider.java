package net.blay09.mods.craftingtweaks.addon;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.List;

public class ThaumCraft5TweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private Field tileEntityField;
    private Field inventoryField;

    @Override
    public boolean load() {
        try {
            Class containerClass = Class.forName("thaumcraft.common.container.ContainerArcaneWorkbench");
            tileEntityField = containerClass.getDeclaredField("tileEntity");
            tileEntityField.setAccessible(true);
            Class tileEntityClass = Class.forName("thaumcraft.common.tiles.crafting.TileArcaneWorkbench");
            inventoryField = tileEntityClass.getField("inventory");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack) {
        try {
            IInventory craftMatrix = (IInventory) inventoryField.get(tileEntityField.get(container));
            return defaultProvider.transferIntoGrid(entityPlayer, container, craftMatrix, itemStack);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack, int index) {
        try {
            IInventory craftMatrix = (IInventory) inventoryField.get(tileEntityField.get(container));
            return defaultProvider.putIntoGrid(entityPlayer, container, craftMatrix, itemStack, index);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return itemStack;
        }
    }

    @Override
    public IInventory getCraftMatrix(EntityPlayer entityPlayer, Container container, int id) {
        try {
            return (IInventory) inventoryField.get(tileEntityField.get(container));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) inventoryField.get(tileEntityField.get(container));
            defaultProvider.clearGrid(entityPlayer, container, craftMatrix, 0, 9);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) inventoryField.get(tileEntityField.get(container));
            defaultProvider.rotateGrid(entityPlayer, container, craftMatrix, 0, 9, defaultProvider.getRotationHandler());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) inventoryField.get(tileEntityField.get(container));
            defaultProvider.balanceGrid(entityPlayer, container, craftMatrix, 0, 9);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canTransferFrom(EntityPlayer entityPlayer, Container container, int id, Slot slot) {
        return defaultProvider.canTransferFrom(entityPlayer, container, id, slot);
    }

    @Override
    public void initGui(GuiContainer guiContainer, List<GuiButton> buttonList) {
        final int paddingTop = 46;
        final int paddingLeft = 4;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft - 16 + paddingLeft, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(0, guiContainer.guiLeft - 16 + paddingLeft, guiContainer.guiTop + paddingTop + 18));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft - 16 + paddingLeft, guiContainer.guiTop + paddingTop + 36));
    }

    @Override
    public String getModId() {
        return "Thaumcraft";
    }
}
