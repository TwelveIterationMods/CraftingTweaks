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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.List;

public class BackpacksTweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private Field craftingGridField;

    @Override
    public boolean load() {
        try {
            Class clazz = Class.forName("de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack");
            craftingGridField = clazz.getDeclaredField("craftingGrid");
            craftingGridField.setAccessible(true);
            return true;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, Slot sourceSlot) {
        try {
            IInventory craftMatrix = (IInventory) craftingGridField.get(container);
            return defaultProvider.transferIntoGrid(entityPlayer, container, craftMatrix, sourceSlot);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack, int index) {
        try {
            IInventory craftMatrix = (IInventory) craftingGridField.get(container);
            return defaultProvider.putIntoGrid(entityPlayer, container, craftMatrix, itemStack, index);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return itemStack;
        }
    }

    @Override
    public IInventory getCraftMatrix(EntityPlayer entityPlayer, Container container, int id) {
        try {
            return (IInventory) craftingGridField.get(container);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) craftingGridField.get(container);
            for(int i = 0; i < 9; i++) {
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
            IInventory craftMatrix = (IInventory) craftingGridField.get(container);
            defaultProvider.rotateGrid(entityPlayer, container, craftMatrix);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {}

    @Override
    public boolean canTransferFrom(EntityPlayer entityPlayer, Container container, int id, Slot slot) {
        return defaultProvider.canTransferFrom(entityPlayer, container, id, slot);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List<GuiButton> buttonList) {
        final int paddingTop = 16;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft + 8, guiContainer.guiTop + paddingTop + 19));
    }

    @Override
    public String getModId() {
        return "Backpack";
    }

}
