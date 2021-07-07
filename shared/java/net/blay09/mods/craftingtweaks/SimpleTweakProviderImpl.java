package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.RotationHandler;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleTweakProviderImpl<T extends AbstractContainerMenu> implements SimpleTweakProvider<T> {

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

    private Predicate<AbstractContainerMenu> isContainerValidPredicate;
    private Function<AbstractContainerMenu, Integer> getGridStartFunction;

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
    public void clearGrid(Player player, T menu, int id, boolean forced) {
        if (tweakClear.enabled) {
            defaultProvider.clearGrid(this, id, player, menu, phantomItems, forced);
        }
    }

    @Override
    public void rotateGrid(Player player, T menu, int id, boolean counterClockwise) {
        if (tweakRotate.enabled) {
            defaultProvider.rotateGrid(this, id, player, menu, getCraftingGridSize(player, menu, id) == 4 ? smallRotationHandler : defaultProvider.getRotationHandler(), counterClockwise);
        }
    }

    @Override
    public void balanceGrid(Player player, T menu, int id) {
        if (tweakBalance.enabled) {
            defaultProvider.balanceGrid(this, id, player, menu);
        }
    }

    @Override
    public void spreadGrid(Player player, T menu, int id) {
        if (tweakBalance.enabled) {
            defaultProvider.spreadGrid(this, id, player, menu);
        }
    }

    @Override
    public boolean canTransferFrom(Player player, T menu, int id, Slot sourceSlot) {
        return defaultProvider.canTransferFrom(player, menu, sourceSlot);
    }

    @Override
    public boolean transferIntoGrid(Player player, T menu, int id, Slot sourceSlot) {
        return defaultProvider.transferIntoGrid(this, id, player, menu, sourceSlot);
    }

    @Override
    public ItemStack putIntoGrid(Player player, T menu, int id, ItemStack itemStack, int index) {
        return defaultProvider.putIntoGrid(this, id, player, menu, itemStack, index);
    }

    @Override
    public Container getCraftMatrix(Player player, T menu, int id) {
        return menu.slots.get(getCraftingGridStart(player, menu, id)).container;
    }

    @Override
    public boolean requiresServerSide() {
        return phantomItems;
    }

    @Override
    public int getCraftingGridStart(Player player, T menu, int id) {
        if (getGridStartFunction != null) {
            return getGridStartFunction.apply(menu);
        }
        return gridSlotNumber;
    }

    @Override
    public int getCraftingGridSize(Player player, T menu, int id) {
        return gridSize;
    }

    @Override
    public void initGui(AbstractContainerScreen<T> screen, Consumer<Widget> addWidgetFunc) {
        if (!hideButtons) {
            int index = 0;
            if (tweakRotate.enabled && tweakRotate.showButton) {
                int buttonX = tweakRotate.buttonX;
                int buttonY = tweakRotate.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(screen, index);
                    buttonY = getButtonY(screen, index);
                }
                addWidgetFunc.accept(CraftingTweaksAPI.createRotateButtonRelative(0, screen, buttonX, buttonY));
                index++;
            }

            if (tweakBalance.enabled && tweakBalance.showButton) {
                int buttonX = tweakBalance.buttonX;
                int buttonY = tweakBalance.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(screen, index);
                    buttonY = getButtonY(screen, index);
                }
                addWidgetFunc.accept(CraftingTweaksAPI.createBalanceButtonRelative(0, screen, buttonX, buttonY));
                index++;
            }

            if (tweakClear.enabled && tweakClear.showButton) {
                int buttonX = tweakClear.buttonX;
                int buttonY = tweakClear.buttonY;
                if (alignToGrid != null) {
                    buttonX = getButtonX(screen, index);
                    buttonY = getButtonY(screen, index);
                }
                addWidgetFunc.accept(CraftingTweaksAPI.createClearButtonRelative(0, screen, buttonX, buttonY));
            }
        }
    }

    private int getButtonX(AbstractContainerScreen<T> guiContainer, int index) {
        Slot firstSlot = guiContainer.getMenu().slots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.getMenu(), 0));
        switch (alignToGrid) {
            case NORTH:
            case UP:
            case SOUTH:
            case DOWN:
                return firstSlot.x + 18 * index;
            case EAST:
                return firstSlot.x + 18 * 3 + 1;
            case WEST:
                return firstSlot.x - 19;
        }
        return 0;
    }

    private int getButtonY(AbstractContainerScreen<T> guiContainer, int index) {
        Slot firstSlot = guiContainer.getMenu().slots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.getMenu(), 0));
        switch (alignToGrid) {
            case NORTH:
            case UP:
                return firstSlot.y - 18 - 1;
            case SOUTH:
            case DOWN:
                return firstSlot.y + 18 * 3 + 1;
            case EAST:
            case WEST:
                return firstSlot.y + 18 * index;
        }
        return 0;
    }

    @Override
    public void setContainerValidPredicate(Predicate<AbstractContainerMenu> predicate) {
        this.isContainerValidPredicate = predicate;
    }

    @Override
    public void setGetGridStartFunction(Function<AbstractContainerMenu, Integer> function) {
        this.getGridStartFunction = function;
    }

    @Override
    public boolean isValidContainer(AbstractContainerMenu menu) {
        return isContainerValidPredicate == null || isContainerValidPredicate.test(menu);
    }
}
