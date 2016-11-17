package net.blay09.mods.craftingtweaks;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class SimpleTweakProviderImpl<T extends Container> implements SimpleTweakProvider<T> {

    private final RotationHandler smallRotationHandler = new RotationHandler() {
        @Override
        public boolean ignoreSlotId(int slotId) {
            return false;
        }

        @Override
        public int rotateSlotId(int slotId, boolean counterClockwise) {
            if (!counterClockwise) {
                switch (slotId) {
                    case 0:
                        return 1;
                    case 1:
                        return 3;
                    case 2:
                        return 0;
                    case 3:
                        return 2;
                }
            } else {
                switch (slotId) {
                    case 1:
                        return 0;
                    case 3:
                        return 1;
                    case 0:
                        return 2;
                    case 2:
                        return 3;
                }
            }
            return 0;
        }
    };

    private Predicate<Container> isContainerValidPredicate;
    private Function<Container, Integer> getGridStartFunction;

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

    public void setAlignToGrid(@Nullable EnumFacing direction) {
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
    public void clearGrid(EntityPlayer entityPlayer, T container, int id, boolean forced) {
        if (tweakClear.enabled) {
            defaultProvider.clearGrid(this, id, entityPlayer, container, phantomItems, forced);
        }
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, T container, int id, boolean counterClockwise) {
        if (tweakRotate.enabled) {
            defaultProvider.rotateGrid(this, id, entityPlayer, container, getCraftingGridSize(entityPlayer, container, id) == 4 ? smallRotationHandler : defaultProvider.getRotationHandler(), counterClockwise);
        }
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, T container, int id) {
        if (tweakBalance.enabled) {
            defaultProvider.balanceGrid(this, id, entityPlayer, container);
        }
    }

    @Override
    public void spreadGrid(EntityPlayer entityPlayer, T container, int id) {
        if (tweakBalance.enabled) {
            defaultProvider.spreadGrid(this, id, entityPlayer, container);
        }
    }

    @Override
    public boolean canTransferFrom(EntityPlayer entityPlayer, T container, int id, Slot sourceSlot) {
        return defaultProvider.canTransferFrom(entityPlayer, container, sourceSlot);
    }

    @Override
    public boolean transferIntoGrid(EntityPlayer entityPlayer, T container, int id, Slot sourceSlot) {
        return defaultProvider.transferIntoGrid(this, id, entityPlayer, container, sourceSlot);
    }

    @Override
    public ItemStack putIntoGrid(EntityPlayer entityPlayer, T container, int id, ItemStack itemStack, int index) {
        return defaultProvider.putIntoGrid(this, id, entityPlayer, container, itemStack, index);
    }

    @Override
    public IInventory getCraftMatrix(EntityPlayer entityPlayer, T container, int id) {
        return container.inventorySlots.get(getCraftingGridStart(entityPlayer, container, id)).inventory;
    }

    @Override
    public boolean requiresServerSide() {
        return phantomItems;
    }

    @Override
    public int getCraftingGridStart(EntityPlayer entityPlayer, T container, int id) {
        if(getGridStartFunction != null) {
            Integer result = getGridStartFunction.apply(container);
            return result != null ? result : 0;
        }
        return gridSlotNumber;
    }

    @Override
    public int getCraftingGridSize(EntityPlayer entityPlayer, T container, int id) {
        return gridSize;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui(GuiContainer guiContainer, List<GuiButton> buttonList) {
        if (!hideButtons) {
            int index = 0;
            if (tweakRotate.enabled && tweakRotate.showButton) {
                int buttonX = tweakRotate.buttonX;
                int buttonY = tweakRotate.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                buttonList.add(CraftingTweaksAPI.createRotateButtonRelative(0, guiContainer, buttonX, buttonY));
                index++;
            }
            if (tweakBalance.enabled && tweakBalance.showButton) {
                int buttonX = tweakBalance.buttonX;
                int buttonY = tweakBalance.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                buttonList.add(CraftingTweaksAPI.createBalanceButtonRelative(0, guiContainer, buttonX, buttonY));
                index++;
            }
            if (tweakClear.enabled && tweakClear.showButton) {
                int buttonX = tweakClear.buttonX;
                int buttonY = tweakClear.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                buttonList.add(CraftingTweaksAPI.createClearButtonRelative(0, guiContainer, buttonX, buttonY));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    private int getButtonX(GuiContainer guiContainer, int index) {
        Slot firstSlot = guiContainer.inventorySlots.inventorySlots.get(getCraftingGridStart(FMLClientHandler.instance().getClientPlayerEntity(), (T) guiContainer.inventorySlots, 0));
        switch (alignToGrid) {
            case NORTH:
            case UP:
            case SOUTH:
            case DOWN:
                return firstSlot.xDisplayPosition + 18 * index;
            case EAST:
                return firstSlot.xDisplayPosition + 18 * 3 + 1;
            case WEST:
                return firstSlot.xDisplayPosition - 19;
        }
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    private int getButtonY(GuiContainer guiContainer, int index) {
        Slot firstSlot = guiContainer.inventorySlots.inventorySlots.get(getCraftingGridStart(FMLClientHandler.instance().getClientPlayerEntity(), (T) guiContainer.inventorySlots, 0));
        switch (alignToGrid) {
            case NORTH:
            case UP:
                return firstSlot.yDisplayPosition - 18 - 1;
            case SOUTH:
            case DOWN:
                return firstSlot.yDisplayPosition + 18 * 3 + 1;
            case EAST:
            case WEST:
                return firstSlot.yDisplayPosition + 18 * index;
        }
        return 0;
    }

    @Override
    public void setContainerValidPredicate(Predicate<Container> predicate) {
        this.isContainerValidPredicate = predicate;
    }

    @Override
    public void setGetGridStartFunction(Function<Container, Integer> function) {
        this.getGridStartFunction = function;
    }

    @Override
    public boolean isValidContainer(Container container) {
        return isContainerValidPredicate == null || isContainerValidPredicate.apply(container);
    }
}
