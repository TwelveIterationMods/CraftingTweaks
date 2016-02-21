package net.blay09.mods.craftingtweaks.addon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.List;

public class GanysDualWorktableTweakProvider implements TweakProvider {

    private final DefaultProviderV2 defaultProvider = CraftingTweaksAPI.createDefaultProviderV2();

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public boolean requiresServerSide() {
        return false;
    }

    @Override
    public int getCraftingGridStart(EntityPlayer entityPlayer, Container container, int id) {
        return id == 0 ? 2 : 11;
    }

    @Override
    public int getCraftingGridSize(EntityPlayer entityPlayer, Container container, int id) {
        return 9;
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        defaultProvider.clearGrid(this, id, entityPlayer, container, false);
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        defaultProvider.rotateGrid(this, id, entityPlayer, container);
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        defaultProvider.balanceGrid(this, id, entityPlayer, container);
    }

    @Override
    public boolean canTransferFrom(EntityPlayer entityPlayer, Container container, int id, Slot sourceSlot) {
        return defaultProvider.canTransferFrom(entityPlayer, container, sourceSlot);
    }

    @Override
    public boolean transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, Slot sourceSlot) {
        return defaultProvider.transferIntoGrid(this, id, entityPlayer, container, sourceSlot);
    }

    @Override
    public ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack, int index) {
        return defaultProvider.putIntoGrid(this, id, entityPlayer, container, itemStack, index);
    }

    @Override
    public IInventory getCraftMatrix(EntityPlayer entityPlayer, Container container, int id) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingTop = 16;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop + 18));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop + 36));
        buttonList.add(CraftingTweaksAPI.createRotateButton(1, guiContainer.guiLeft + guiContainer.xSize, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(1, guiContainer.guiLeft + guiContainer.xSize, guiContainer.guiTop + paddingTop + 18));
        buttonList.add(CraftingTweaksAPI.createClearButton(1, guiContainer.guiLeft + guiContainer.xSize, guiContainer.guiTop + paddingTop + 36));
    }

    @Override
    public String getModId() {
        return "ganyssurface";
    }
}
