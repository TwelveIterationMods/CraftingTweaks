package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;

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
    private Direction alignToGrid;

    public SimpleTweakProviderImpl(String modid) {
        this.modid = modid;
    }

    public void setAlignToGrid(@Nullable Direction direction) {
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
    public void clearGrid(PlayerEntity entityPlayer, T container, int id, boolean forced) {
        if (tweakClear.enabled) {
            defaultProvider.clearGrid(this, id, entityPlayer, container, phantomItems, forced);
        }
    }

    @Override
    public void rotateGrid(PlayerEntity entityPlayer, T container, int id, boolean counterClockwise) {
        if (tweakRotate.enabled) {
            defaultProvider.rotateGrid(this, id, entityPlayer, container, getCraftingGridSize(entityPlayer, container, id) == 4 ? smallRotationHandler : defaultProvider.getRotationHandler(), counterClockwise);
        }
    }

    @Override
    public void balanceGrid(PlayerEntity entityPlayer, T container, int id) {
        if (tweakBalance.enabled) {
            defaultProvider.balanceGrid(this, id, entityPlayer, container);
        }
    }

    @Override
    public void spreadGrid(PlayerEntity entityPlayer, T container, int id) {
        if (tweakBalance.enabled) {
            defaultProvider.spreadGrid(this, id, entityPlayer, container);
        }
    }

    @Override
    public boolean canTransferFrom(PlayerEntity entityPlayer, T container, int id, Slot sourceSlot) {
        return defaultProvider.canTransferFrom(entityPlayer, container, sourceSlot);
    }

    @Override
    public boolean transferIntoGrid(PlayerEntity entityPlayer, T container, int id, Slot sourceSlot) {
        return defaultProvider.transferIntoGrid(this, id, entityPlayer, container, sourceSlot);
    }

    @Override
    public ItemStack putIntoGrid(PlayerEntity entityPlayer, T container, int id, ItemStack itemStack, int index) {
        return defaultProvider.putIntoGrid(this, id, entityPlayer, container, itemStack, index);
    }

    @Override
    public IInventory getCraftMatrix(PlayerEntity entityPlayer, T container, int id) {
        return container.inventorySlots.get(getCraftingGridStart(entityPlayer, container, id)).inventory;
    }

    @Override
    public boolean requiresServerSide() {
        return phantomItems;
    }

    @Override
    public int getCraftingGridStart(PlayerEntity entityPlayer, T container, int id) {
        if (getGridStartFunction != null) {
            return getGridStartFunction.apply(container);
        }
        return gridSlotNumber;
    }

    @Override
    public int getCraftingGridSize(PlayerEntity entityPlayer, T container, int id) {
        return gridSize;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initGui(ContainerScreen<T> guiContainer, GuiScreenEvent.InitGuiEvent event) {
        if (!hideButtons) {
            int index = 0;
            if (tweakRotate.enabled && tweakRotate.showButton) {
                int buttonX = tweakRotate.buttonX;
                int buttonY = tweakRotate.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                event.addWidget(CraftingTweaksAPI.createRotateButton(0, guiContainer, buttonX + guiContainer.getGuiLeft(), buttonY + guiContainer.getGuiTop()));
                index++;
            }

            if (tweakBalance.enabled && tweakBalance.showButton) {
                int buttonX = tweakBalance.buttonX;
                int buttonY = tweakBalance.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                event.addWidget(CraftingTweaksAPI.createBalanceButton(0, guiContainer, buttonX + guiContainer.getGuiLeft(), buttonY + guiContainer.getGuiTop()));
                index++;
            }

            if (tweakClear.enabled && tweakClear.showButton) {
                int buttonX = tweakClear.buttonX;
                int buttonY = tweakClear.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(guiContainer, index);
                    buttonY = getButtonY(guiContainer, index);
                }
                event.addWidget(CraftingTweaksAPI.createClearButton(0, guiContainer, buttonX + guiContainer.getGuiLeft(), buttonY + guiContainer.getGuiTop()));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private int getButtonX(ContainerScreen<T> guiContainer, int index) {
        Slot firstSlot = guiContainer.getContainer().inventorySlots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.getContainer(), 0));
        switch (alignToGrid) {
            case NORTH:
            case UP:
            case SOUTH:
            case DOWN:
                return firstSlot.xPos + 18 * index;
            case EAST:
                return firstSlot.xPos + 18 * 3 + 1;
            case WEST:
                return firstSlot.xPos - 19;
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    private int getButtonY(ContainerScreen<T> guiContainer, int index) {
        Slot firstSlot = guiContainer.getContainer().inventorySlots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.getContainer(), 0));
        switch (alignToGrid) {
            case NORTH:
            case UP:
                return firstSlot.yPos - 18 - 1;
            case SOUTH:
            case DOWN:
                return firstSlot.yPos + 18 * 3 + 1;
            case EAST:
            case WEST:
                return firstSlot.yPos + 18 * index;
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
        return isContainerValidPredicate == null || isContainerValidPredicate.test(container);
    }
}
