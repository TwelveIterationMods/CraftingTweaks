//package net.blay09.mods.craftingtweaks.api;
//
//import net.blay09.mods.craftingtweaks.apiv2.CraftingTweaksAPI;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.components.AbstractWidget;
//import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//import net.minecraft.core.Direction;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.inventory.Slot;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
//public class SimpleTweakProviderImpl<T extends AbstractContainerMenu> implements SimpleTweakProvider<T> {
//
//    private Predicate<AbstractContainerMenu> isContainerValidPredicate;
//    private Function<AbstractContainerMenu, Integer> getGridStartFunction;
//
//    @Override
//    public void initGui(AbstractContainerScreen<T> screen, Consumer<AbstractWidget> addWidgetFunc) {
//        if (!hideButtons) {
//            int index = 0;
//            if (tweakRotate.enabled && tweakRotate.showButton) {
//                int buttonX = tweakRotate.buttonX;
//                int buttonY = tweakRotate.buttonY;
//                if (alignToGrid != null) {
//                    buttonX = getButtonX(screen, index);
//                    buttonY = getButtonY(screen, index);
//                }
//                addWidgetFunc.accept(CraftingTweaksAPI.createRotateButtonRelative(0, screen, buttonX, buttonY));
//                index++;
//            }
//
//            if (tweakBalance.enabled && tweakBalance.showButton) {
//                int buttonX = tweakBalance.buttonX;
//                int buttonY = tweakBalance.buttonY;
//                if (alignToGrid != null) {
//                    buttonX = getButtonX(screen, index);
//                    buttonY = getButtonY(screen, index);
//                }
//                addWidgetFunc.accept(CraftingTweaksAPI.createBalanceButtonRelative(0, screen, buttonX, buttonY));
//                index++;
//            }
//
//            if (tweakClear.enabled && tweakClear.showButton) {
//                int buttonX = tweakClear.buttonX;
//                int buttonY = tweakClear.buttonY;
//                if (alignToGrid != null) {
//                    buttonX = getButtonX(screen, index);
//                    buttonY = getButtonY(screen, index);
//                }
//                addWidgetFunc.accept(CraftingTweaksAPI.createClearButtonRelative(0, screen, buttonX, buttonY));
//            }
//        }
//    }
//
//    private int getButtonX(AbstractContainerScreen<T> guiContainer, int index) {
//        Slot firstSlot = guiContainer.getMenu().slots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.getMenu(), 0));
//        switch (alignToGrid) {
//            case NORTH:
//            case UP:
//            case SOUTH:
//            case DOWN:
//                return firstSlot.x + 18 * index;
//            case EAST:
//                return firstSlot.x + 18 * 3 + 1;
//            case WEST:
//                return firstSlot.x - 19;
//        }
//        return 0;
//    }
//
//    private int getButtonY(AbstractContainerScreen<T> guiContainer, int index) {
//        Slot firstSlot = guiContainer.getMenu().slots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.getMenu(), 0));
//        switch (alignToGrid) {
//            case NORTH:
//            case UP:
//                return firstSlot.y - 18 - 1;
//            case SOUTH:
//            case DOWN:
//                return firstSlot.y + 18 * 3 + 1;
//            case EAST:
//            case WEST:
//                return firstSlot.y + 18 * index;
//        }
//        return 0;
//    }
//
//    @Override
//    public void setContainerValidPredicate(Predicate<AbstractContainerMenu> predicate) {
//        this.isContainerValidPredicate = predicate;
//    }
//
//    @Override
//    public void setGetGridStartFunction(Function<AbstractContainerMenu, Integer> function) {
//        this.getGridStartFunction = function;
//    }
//
//    @Override
//    public boolean isValidContainer(AbstractContainerMenu menu) {
//        return isContainerValidPredicate == null || isContainerValidPredicate.test(menu);
//    }
//}
