package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class SimpleTweakProviderImpl implements SimpleTweakProvider {

    public static class TweakSettings {
        public final boolean enabled;
        public final boolean showButton;
        public final int buttonX;
        public final int buttonY;

        public TweakSettings(boolean enabled, boolean showButton, int buttonX, int buttonY) {
            this.enabled = enabled;
            this.showButton = showButton;
            this.buttonX = buttonX;
            this.buttonY = buttonY;
        }
    }

    private final String modid;
    private final DefaultProviderV2 defaultProvider = CraftingTweaksAPI.createDefaultProviderV2();
    private int gridSlotNumber = 1;
    private int gridSize = 9;
    private boolean hideButtons;
    private boolean phantomItems;
    private TweakSettings tweakRotate = new TweakSettings(true, true, -16, 16);
    private TweakSettings tweakBalance = new TweakSettings(true, true, -16, 16 + 18);
    private TweakSettings tweakClear = new TweakSettings(true, true, -16, 16 + 18 + 18);
    private EnumFacing alignToGrid;

    public SimpleTweakProviderImpl(String modid) {
        this.modid = modid;
    }

    public void setAlignToGrid(EnumFacing direction) {
        this.alignToGrid = direction;
    }

    @Override
    public void setTweakRotate(boolean enabled, boolean showButton, int x, int y) {
        tweakRotate = new TweakSettings(enabled, showButton, x, y);
    }

    @Override
    public void setTweakBalance(boolean enabled, boolean showButton, int x, int y) {
        tweakBalance = new TweakSettings(enabled, showButton, x, y);
    }

    @Override
    public void setTweakClear(boolean enabled, boolean showButton, int x, int y) {
        tweakClear = new TweakSettings(enabled, showButton, x, y);
    }

    @Override
    public void setPhantomItems(boolean phantomItems) {
        this.phantomItems = phantomItems;
    }

    @Override
    public void setGrid(int slotNumber, int size) {
        gridSlotNumber = slotNumber;
        gridSize = size;
    }

    @Override
    public void setHideButtons(boolean hideButtons) {
        this.hideButtons = hideButtons;
    }

    @Override
    public String getModId() {
        return modid;
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id) {
        if(tweakClear.enabled) {
            defaultProvider.clearGrid(this, id, entityPlayer, container, phantomItems);
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id) {
        if(tweakRotate.enabled) {
            defaultProvider.rotateGrid(this, id, entityPlayer, container);
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        if(tweakBalance.enabled) {
            defaultProvider.balanceGrid(this, id, entityPlayer, container);
        }
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
        return container.inventorySlots.get(getCraftingGridStart(id)).inventory;
    }

    @Override
    public boolean requiresServerSide() {
        return phantomItems;
    }

    @Override
    public int getCraftingGridStart(int id) {
        return gridSlotNumber;
    }

    @Override
    public int getCraftingGridSize(int id) {
        return gridSize;
    }

    @Override
    public void initGui(GuiContainer guiContainer, List<GuiButton> buttonList) {
        if(!hideButtons) {
            int index = 0;
            if(tweakRotate.enabled && tweakRotate.showButton) {
                int buttonX = guiContainer.guiLeft + tweakRotate.buttonX;
                int buttonY = guiContainer.guiTop + tweakRotate.buttonY;
                if(alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                buttonList.add(CraftingTweaksAPI.createRotateButton(0, buttonX, buttonY));
                index++;
            }
            if(tweakBalance.enabled && tweakBalance.showButton) {
                int buttonX = guiContainer.guiLeft + tweakBalance.buttonX;
                int buttonY = guiContainer.guiTop + tweakBalance.buttonY;
                if(alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                buttonList.add(CraftingTweaksAPI.createBalanceButton(0, buttonX, buttonY));
                index++;
            }
            if(tweakClear.enabled && tweakClear.showButton) {
                int buttonX = guiContainer.guiLeft + tweakClear.buttonX;
                int buttonY = guiContainer.guiTop + tweakClear.buttonY;
                if(alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                buttonList.add(CraftingTweaksAPI.createClearButton(0, buttonX, buttonY));
            }
        }
    }

    private int getButtonX(GuiContainer guiContainer, int index) {
        Slot firstSlot = guiContainer.inventorySlots.inventorySlots.get(getCraftingGridStart(0));
        switch(alignToGrid) {
            case NORTH:
            case UP:
            case SOUTH:
            case DOWN:
                return guiContainer.guiLeft + firstSlot.xDisplayPosition + 18 * index;
            case EAST:
                return guiContainer.guiLeft + firstSlot.xDisplayPosition + 18 * 3 + 1;
            case WEST:
                return guiContainer.guiLeft + firstSlot.xDisplayPosition - 19;
        }
        return 0;
    }

    private int getButtonY(GuiContainer guiContainer, int index) {
        Slot firstSlot = guiContainer.inventorySlots.inventorySlots.get(getCraftingGridStart(0));
        switch(alignToGrid) {
            case NORTH:
            case UP:
                return guiContainer.guiTop + firstSlot.yDisplayPosition - 18 - 1;
            case SOUTH:
            case DOWN:
                return guiContainer.guiTop + firstSlot.yDisplayPosition + 18 * 3 + 1;
            case EAST:
            case WEST:
                return guiContainer.guiTop + firstSlot.yDisplayPosition + 18 * index;
        }
        return 0;
    }

}
