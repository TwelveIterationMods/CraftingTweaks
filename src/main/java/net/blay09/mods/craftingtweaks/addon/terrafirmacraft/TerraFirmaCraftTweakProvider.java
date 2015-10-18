package net.blay09.mods.craftingtweaks.addon.terrafirmacraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProvider;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;

import java.lang.reflect.Field;
import java.util.List;

public class TerraFirmaCraftTweakProvider implements TweakProvider {

    private final DefaultProvider defaultProvider = CraftingTweaksAPI.createDefaultProvider();

    private final RotationHandler smallRotationHandler = new RotationHandler() {
        @Override
        public boolean ignoreSlotId(int slotId) {
            return false;
        }

        @Override
        public int rotateSlotId(int slotId) {
            switch(slotId) {
                case 0: return 1;
                case 1: return 3;
                case 2: return 0;
                case 3: return 2;
            }
            return 0;
        }
    };

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        defaultProvider.clearGrid(entityPlayer, container, ((ContainerPlayer) container).craftMatrix);
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        if(entityPlayer.getEntityData().hasKey("craftingTable")) {
            defaultProvider.rotateGrid(entityPlayer, container, ((ContainerPlayer) container).craftMatrix);
        } else {
            defaultProvider.rotateGrid(entityPlayer, container, ((ContainerPlayer) container).craftMatrix, 0, 4, smallRotationHandler);
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        defaultProvider.balanceGrid(entityPlayer, container, ((ContainerPlayer) container).craftMatrix);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List buttonList) {
        final int paddingLeft = 1;
        final int paddingTop = -16;
        buttonList.add(CraftingTweaksAPI.createRotateButton(0, guiContainer.guiLeft + guiContainer.xSize / 2 + paddingLeft, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createBalanceButton(0, guiContainer.guiLeft + guiContainer.xSize / 2 + paddingLeft + 18, guiContainer.guiTop + paddingTop));
        buttonList.add(CraftingTweaksAPI.createClearButton(0, guiContainer.guiLeft + guiContainer.xSize / 2 + paddingLeft + 36, guiContainer.guiTop + paddingTop));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean areHotkeysEnabled(EntityPlayer entityPlayer, Container container) {
        return true;
    }

    @Override
    public String getModId() {
        return "terrafirmacraft";
    }
}
