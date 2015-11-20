package net.blay09.mods.craftingtweaks.addon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
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

public class StevesWorkshopTweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private Method getTable;
    private Method getMainPage;
    private Method getCraftingList;
    private Field inventoryCraftingField;
    private Method isEnabled;
    private Field xField;
    private Field yField;

    @Override
    public boolean load() {
        try {
            Class containerClass = Class.forName("vswe.production.gui.container.ContainerTable");
            getTable = containerClass.getMethod("getTable");
            Class tileEntityClass = Class.forName("vswe.production.tileentity.TileEntityTable");
            getMainPage = tileEntityClass.getMethod("getMainPage");
            Class mainPageClass = Class.forName("vswe.production.page.PageMain");
            getCraftingList = mainPageClass.getMethod("getCraftingList");
            Class unitCraftingClass = Class.forName("vswe.production.page.unit.UnitCrafting");
            inventoryCraftingField = unitCraftingClass.getDeclaredField("inventoryCrafting");
            inventoryCraftingField.setAccessible(true);
            isEnabled = unitCraftingClass.getMethod("isEnabled");
            Class unitClass = Class.forName("vswe.production.page.unit.Unit");
            xField = unitClass.getDeclaredField("x");
            xField.setAccessible(true);
            yField = unitClass.getDeclaredField("y");
            yField.setAccessible(true);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            // We need to catch this because a missing SideOnly annotation inside Steve's Workshop's PageMain class makes Reflection of the class impossible on dedicated servers
            // We could get into an ASM mess to fix this ... or we could just not and let the user know.
            CraftingTweaks.logger.error("Steve's Workshop integration is unavailable on dedicated servers due to technical limitations.");
            CraftingTweaks.logger.error("Feel free to ask its developer nicely about adding support.");
            CraftingTweaks.logger.error("Alternatively, a fork of said mod called Ewy's Workshop will provide proper support.");
        }
        return false;
    }

    @Override
    public ItemStack transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack) {
        try {
            Object tileEntity = getTable.invoke(container);
            Object mainPage = getMainPage.invoke(tileEntity);
            List craftingList = (List) getCraftingList.invoke(mainPage);
            if(id < craftingList.size()) {
                Object unitCrafting = craftingList.get(id);
                IInventory inventoryCrafting = (IInventory) inventoryCraftingField.get(unitCrafting);
                if (inventoryCrafting != null) {
                    return defaultProvider.transferIntoGrid(entityPlayer, container, inventoryCrafting, itemStack);
                }
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
            Object tileEntity = getTable.invoke(container);
            Object mainPage = getMainPage.invoke(tileEntity);
            List craftingList = (List) getCraftingList.invoke(mainPage);
            if(id < craftingList.size()) {
                Object unitCrafting = craftingList.get(id);
                IInventory inventoryCrafting = (IInventory) inventoryCraftingField.get(unitCrafting);
                if (inventoryCrafting != null) {
                    return defaultProvider.putIntoGrid(entityPlayer, container, inventoryCrafting, itemStack, index);
                }
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
            Object tileEntity = getTable.invoke(container);
            Object mainPage = getMainPage.invoke(tileEntity);
            List craftingList = (List) getCraftingList.invoke(mainPage);
            if(id < craftingList.size()) {
                Object unitCrafting = craftingList.get(id);
                return (IInventory) inventoryCraftingField.get(unitCrafting);
            }
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
            Object tileEntity = getTable.invoke(container);
            Object mainPage = getMainPage.invoke(tileEntity);
            List craftingList = (List) getCraftingList.invoke(mainPage);
            if(id < craftingList.size()) {
                Object unitCrafting = craftingList.get(id);
                IInventory inventoryCrafting = (IInventory) inventoryCraftingField.get(unitCrafting);
                if (inventoryCrafting != null) {
                    defaultProvider.clearGrid(entityPlayer, container, inventoryCrafting);
                }
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
            Object tileEntity = getTable.invoke(container);
            Object mainPage = getMainPage.invoke(tileEntity);
            List craftingList = (List) getCraftingList.invoke(mainPage);
            if(id < craftingList.size()) {
                Object unitCrafting = craftingList.get(id);
                IInventory inventoryCrafting = (IInventory) inventoryCraftingField.get(unitCrafting);
                if (inventoryCrafting != null) {
                    defaultProvider.rotateGrid(entityPlayer, container, inventoryCrafting);
                }
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
            Object tileEntity = getTable.invoke(container);
            Object mainPage = getMainPage.invoke(tileEntity);
            List craftingList = (List) getCraftingList.invoke(mainPage);
            if(id < craftingList.size()) {
                Object unitCrafting = craftingList.get(id);
                IInventory inventoryCrafting = (IInventory) inventoryCraftingField.get(unitCrafting);
                if (inventoryCrafting != null) {
                    defaultProvider.balanceGrid(entityPlayer, container, inventoryCrafting);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui(GuiContainer guiContainer, List buttonList) {
        Container container = guiContainer.inventorySlots;
        try {
            final int paddingTop = 4;
            Object tileEntity = getTable.invoke(container);
            Object mainPage = getMainPage.invoke(tileEntity);
            List craftingList = (List) getCraftingList.invoke(mainPage);
            for(int i = 0; i < craftingList.size(); i++) {
                Object unitCrafting = craftingList.get(i);
                if((Boolean) isEnabled.invoke(unitCrafting)) {
                    int x = xField.getInt(unitCrafting);
                    int y = yField.getInt(unitCrafting);
                    if(guiContainer.guiLeft + x < guiContainer.width / 2) {
                        x = guiContainer.guiLeft - 16;
                    } else {
                        x = guiContainer.guiLeft + guiContainer.xSize;
                    }
                    buttonList.add(CraftingTweaksAPI.createRotateButton(i, x, guiContainer.guiTop + y + paddingTop));
                    buttonList.add(CraftingTweaksAPI.createBalanceButton(i, x, guiContainer.guiTop + y + paddingTop + 18));
                    buttonList.add(CraftingTweaksAPI.createClearButton(i, x, guiContainer.guiTop + y + paddingTop + 36));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean areHotkeysEnabled(EntityPlayer entityPlayer, Container container) {
        return true;
    }

    @Override
    public String getModId() {
        return "StevesWorkshop";
    }
}
