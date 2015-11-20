package net.blay09.mods.craftingtweaks.addon.appliedenergistics2;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class AE2PatternTerminalTweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private Field partPatternTerminalField;
    private Method getInventoryByName;

    private Field searchFieldField;
    private Field craftingGridOffsetXField;
    private Field craftingGridOffsetYField;

    @Override
    public boolean load() {
        try {
            Class containerClass = Class.forName("appeng.container.implementations.ContainerPatternTerm");
            partPatternTerminalField = containerClass.getField("ct");
            Class partPatternTerminalClass = Class.forName("appeng.parts.reporting.PartPatternTerminal");
            getInventoryByName = partPatternTerminalClass.getMethod("getInventoryByName", String.class);

            if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                Class guiScreenClass = Class.forName("appeng.client.gui.implementations.GuiMEMonitorable");
                searchFieldField = guiScreenClass.getDeclaredField("searchField");
                searchFieldField.setAccessible(true);
                craftingGridOffsetXField = guiScreenClass.getField("CraftingGridOffsetX");
                craftingGridOffsetYField = guiScreenClass.getField("CraftingGridOffsetY");
            }
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ItemStack transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack) {
        try {
            Object ct = partPatternTerminalField.get(container);
            IInventory craftMatrix = (IInventory) getInventoryByName.invoke(ct, "crafting");
            if(craftMatrix != null) {
                return defaultProvider.transferIntoGrid(entityPlayer, container, craftMatrix, itemStack);
            }
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
            Object ct = partPatternTerminalField.get(container);
            IInventory craftMatrix = (IInventory) getInventoryByName.invoke(ct, "crafting");
            if(craftMatrix != null) {
                return defaultProvider.putIntoGrid(entityPlayer, container, craftMatrix, itemStack, index);
            }
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
            Object ct = partPatternTerminalField.get(container);
            return (IInventory) getInventoryByName.invoke(ct, "crafting");
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
            Object ct = partPatternTerminalField.get(container);
            IInventory craftMatrix = (IInventory) getInventoryByName.invoke(ct, "crafting");
            for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
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
            Object ct = partPatternTerminalField.get(container);
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
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List buttonList) {
        try {
            int offsetX = craftingGridOffsetXField.getInt(null) + 79;
            int offsetY = craftingGridOffsetYField.getInt(null) + 42;
            buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft + offsetX, guiContainer.guiTop + offsetY));
            buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft + offsetX + 17, guiContainer.guiTop + offsetY));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean areHotkeysEnabled(EntityPlayer entityPlayer, Container container) {
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
        try {
            GuiTextField textField = (GuiTextField) searchFieldField.get(guiScreen);
            return !textField.isFocused();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getModId() {
        return "appliedenergistics2";
    }
}
