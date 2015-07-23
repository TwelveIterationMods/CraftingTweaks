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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class AppliedEnergistics2TweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private boolean isLoaded;
    private Field partCraftingTerminalField;
    private Method getInventoryByName;

    public AppliedEnergistics2TweakProvider() {
        try {
            Class containerClass = Class.forName("appeng.container.implementations.ContainerCraftingTerm");
            partCraftingTerminalField = containerClass.getField("ct");
            Class partCraftingTerminalClass = Class.forName("appeng.parts.reporting.PartCraftingTerminal");
            getInventoryByName = partCraftingTerminalClass.getMethod("getInventoryByName", String.class);
            isLoaded = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
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
            Object ct = partCraftingTerminalField.get(container);
            IInventory craftMatrix = (IInventory) getInventoryByName.invoke(ct, "crafting");
            if(craftMatrix != null) {
                defaultProvider.clearGrid(entityPlayer, container, craftMatrix);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            Object ct = partCraftingTerminalField.get(container);
            IInventory craftMatrix = (IInventory) getInventoryByName.invoke(ct, "crafting");
            if(craftMatrix != null) {
                defaultProvider.rotateGrid(entityPlayer, container, craftMatrix);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            Object ct = partCraftingTerminalField.get(container);
            IInventory craftMatrix = (IInventory) getInventoryByName.invoke(ct, "crafting");
            if(craftMatrix != null) {
                defaultProvider.balanceGrid(entityPlayer, container, craftMatrix);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingLeft = 32;
        final int paddingTop = 105;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft + paddingLeft - 16, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(0, guiContainer.guiLeft + paddingLeft - 16, guiContainer.guiTop + paddingTop + 18));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft + paddingLeft - 16, guiContainer.guiTop + paddingTop + 36));
    }

}
