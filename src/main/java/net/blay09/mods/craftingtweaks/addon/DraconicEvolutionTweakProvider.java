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

public class DraconicEvolutionTweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();
    private boolean isLoaded;
    private Field craftMatrixField;

    public DraconicEvolutionTweakProvider() {
        try {
            Class clazz = Class.forName("com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest");
            craftMatrixField = clazz.getField("craftMatrix");
            isLoaded = true;
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchFieldException ignored) {}
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) craftMatrixField.get(container);
            defaultProvider.clearGrid(entityPlayer, container, craftMatrix);
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
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        try {
            IInventory craftMatrix = (IInventory) craftMatrixField.get(container);
            defaultProvider.balanceGrid(entityPlayer, container, craftMatrix);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingBottom = 24;
        final int paddingRight = 38;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft + guiContainer.xSize - paddingRight - 36, guiContainer.guiTop + guiContainer.ySize - paddingBottom));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(0, guiContainer.guiLeft + guiContainer.xSize - paddingRight - 18, guiContainer.guiTop + guiContainer.ySize - paddingBottom));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft + guiContainer.xSize - paddingRight, guiContainer.guiTop + guiContainer.ySize - paddingBottom));
    }

}
