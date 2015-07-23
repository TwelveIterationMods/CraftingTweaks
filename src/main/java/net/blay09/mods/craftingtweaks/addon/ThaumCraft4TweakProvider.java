package net.blay09.mods.craftingtweaks.addon;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.DefaultProviderImpl;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.List;

public class ThaumCraft4TweakProvider implements TweakProvider {

    private Field tileEntityField;
    private boolean isLoaded;

    public ThaumCraft4TweakProvider() {
        try {
            Class containerClass = Class.forName("thaumcraft.common.container.ContainerArcaneWorkbench");
            tileEntityField = containerClass.getDeclaredField("tileEntity");
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
            for(int i = 0; i < 9; i++) {
                ItemStack itemStack = craftMatrix.getStackInSlot(i);
                if(itemStack != null) {
                    if (entityPlayer.inventory.addItemStackToInventory(itemStack)) {
                        craftMatrix.setInventorySlotContents(i, null);
                    }
                }
                container.detectAndSendChanges();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) tileEntityField.get(container);
            IInventory matrixClone = new InventoryBasic("", false, 9);
            for(int i = 0; i < matrixClone.getSizeInventory(); i++) {
                matrixClone.setInventorySlotContents(i, craftMatrix.getStackInSlot(i));
            }
            for(int i = 0; i < matrixClone.getSizeInventory(); i++) {
                if(i == 4) {
                    continue;
                }
                craftMatrix.setInventorySlotContents(DefaultProviderImpl.rotateSlotId(i), matrixClone.getStackInSlot(i));
            }
            container.detectAndSendChanges();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) tileEntityField.get(container);
            ArrayListMultimap<String, ItemStack> itemMap = ArrayListMultimap.create();
            Multiset<String> itemCount = HashMultiset.create();
            for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                ItemStack itemStack = craftMatrix.getStackInSlot(i);
                if(itemStack != null && itemStack.getMaxStackSize() > 1) {
                    String key = itemStack.getUnlocalizedName() + "@" + itemStack.getItemDamage();
                    itemMap.put(key, itemStack);
                    itemCount.add(key, itemStack.stackSize);
                }
            }
            for(String key : itemMap.keySet()) {
                List<ItemStack> balanceList = itemMap.get(key);
                int totalCount = itemCount.count(key);
                int countPerStack = totalCount / balanceList.size();
                int restCount = totalCount % balanceList.size();
                for(ItemStack itemStack : balanceList) {
                    itemStack.stackSize = countPerStack;
                }
                int idx = 0;
                while(restCount > 0) {
                    ItemStack itemStack = balanceList.get(idx);
                    if(itemStack.stackSize < itemStack.getMaxStackSize()) {
                        itemStack.stackSize++;
                        restCount--;
                    }
                    idx++;
                    if(idx >= balanceList.size()) {
                        idx = 0;
                    }
                }
            }
            container.detectAndSendChanges();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingTop = 16;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop + 18));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop + 36));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean areHotkeysEnabled(EntityPlayer entityPlayer, Container container) {
        return true;
    }

}
