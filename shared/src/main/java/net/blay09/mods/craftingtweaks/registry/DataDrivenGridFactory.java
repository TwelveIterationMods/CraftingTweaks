package net.blay09.mods.craftingtweaks.registry;

import net.blay09.mods.craftingtweaks.api.*;
import net.blay09.mods.craftingtweaks.api.impl.DefaultCraftingGrid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.function.Predicate;

public class DataDrivenGridFactory {
    private static final Logger logger = LoggerFactory.getLogger(DataDrivenGridFactory.class);

    @SuppressWarnings("unchecked")
    public static CraftingGridProvider createGridProvider(CraftingTweaksRegistrationData data) {
        String senderModId = data.getModId();

        String containerClassName = data.getContainerClass();
        // IMC API should always check the container class first, as it was previously possible to use specific generics in predicates
        Predicate<AbstractContainerMenu> matchesContainerClass = it -> it.getClass().getName().equals(containerClassName);
        Predicate<AbstractContainerMenu> containerPredicate = matchesContainerClass;

        String validContainerPredicateLegacy = data.getContainerCallbackClass();
        if (!validContainerPredicateLegacy.isEmpty()) {
            try {
                Class<?> functionClass = Class.forName(validContainerPredicateLegacy);
                if (!Function.class.isAssignableFrom(functionClass)) {
                    logger.error("{} sent a container callback that's not even a function", senderModId);
                    return null;
                }
                Function<AbstractContainerMenu, Boolean> function = (Function<AbstractContainerMenu, Boolean>) functionClass.getDeclaredConstructor()
                        .newInstance();
                containerPredicate = t -> matchesContainerClass.test(t) && function.apply(t);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                logger.error("{} sent an invalid container callback.", senderModId);
            }
        }

        String validContainerPredicate = data.getValidContainerPredicateClass();
        if (!validContainerPredicate.isEmpty()) {
            try {
                Class<?> predicateClass = Class.forName(validContainerPredicate);
                if (!Predicate.class.isAssignableFrom(predicateClass)) {
                    logger.error("{} sent an invalid ValidContainerPredicate - it must implement Predicate<Container>", senderModId);
                    return null;
                }
                Predicate<AbstractContainerMenu> providedPredicate = (Predicate<AbstractContainerMenu>) predicateClass.getDeclaredConstructor()
                        .newInstance();
                containerPredicate = it -> matchesContainerClass.test(it) && providedPredicate.test(it);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                logger.error("{} sent an invalid ValidContainerPredicate: {}", senderModId, e.getMessage());
            }
        }

        String getGridStartFunction = data.getGetGridStartFunctionClass();
        Function<AbstractContainerMenu, Integer> gridStartFunction = null;
        if (!getGridStartFunction.isEmpty()) {
            try {
                Class<?> functionClass = Class.forName(getGridStartFunction);
                if (!Function.class.isAssignableFrom(functionClass)) {
                    logger.error("{} sent an invalid GetGridStartFunction - it must implement Function<Container, Integer>", senderModId);
                    return null;
                }
                gridStartFunction = (Function<AbstractContainerMenu, Integer>) functionClass.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                logger.error("{} sent an invalid GetGridStartFunction: {}", senderModId, e.getMessage());
            }
        }

        final Predicate<AbstractContainerMenu> effectiveContainerPredicate = containerPredicate;
        final Function<AbstractContainerMenu, Integer> effectiveGridStartFunction = gridStartFunction;
        return new CraftingGridProvider() {
            @Override
            public String getModId() {
                return senderModId;
            }

            @Override
            public boolean handles(AbstractContainerMenu menu) {
                return effectiveContainerPredicate.test(menu);
            }

            @Override
            public void buildCraftingGrids(CraftingGridBuilder builder, AbstractContainerMenu menu) {
                int gridSlotNumber = data.getGridSlotNumber();
                int gridSize = data.getGridSize();

                CraftingGridDecorator grid;
                if (effectiveGridStartFunction != null) {
                    grid = new DefaultCraftingGrid(new ResourceLocation(senderModId, "default"), gridSlotNumber, gridSize) {
                        @Override
                        public int getGridStartSlot(Player player, AbstractContainerMenu menu) {
                            return effectiveGridStartFunction.apply(menu);
                        }
                    };
                    builder.addCustomGrid((CraftingGrid) grid);
                } else {
                    grid = builder.addGrid(gridSlotNumber, gridSize);
                }

                int buttonOffsetX = unwrapOr(data.getButtonOffsetX(), 0);
                int buttonOffsetY = unwrapOr(data.getButtonOffsetY(), 0);
                grid.setButtonAlignmentOffset(buttonOffsetX, buttonOffsetY);

                ButtonAlignment alignToGrid = ButtonAlignment.LEFT;
                String alignToGridName = data.getAlignToGrid();
                switch (alignToGridName.toLowerCase()) {
                    case "north", "up", "top" -> alignToGrid = ButtonAlignment.TOP;
                    case "south", "down", "bottom" -> alignToGrid = ButtonAlignment.BOTTOM;
                    case "east", "right" -> alignToGrid = ButtonAlignment.RIGHT;
                    case "west", "left" -> alignToGrid = ButtonAlignment.LEFT;
                }
                grid.setButtonAlignment(alignToGrid);

                ButtonStyle buttonStyle = CraftingTweaksButtonStyles.DEFAULT;
                String buttonStyleName = data.getButtonStyle();
                switch (buttonStyleName.toLowerCase()) {
                    case "default" -> buttonStyle = CraftingTweaksButtonStyles.DEFAULT;
                    case "small_width" -> buttonStyle = CraftingTweaksButtonStyles.SMALL_WIDTH;
                    case "small_height" -> buttonStyle = CraftingTweaksButtonStyles.SMALL_HEIGHT;
                    case "small" -> buttonStyle = CraftingTweaksButtonStyles.SMALL;
                }
                grid.setButtonStyle(buttonStyle);

                if (data.isHideButtons()) {
                    grid.hideAllTweakButtons();
                }

                if (data.isPhantomItems()) {
                    grid.usePhantomItems();
                }

                var rotateTweak = data.getTweakRotate();
                if (!rotateTweak.isEnabled()) {
                    grid.disableTweak(TweakType.Rotate);
                }
                if (!rotateTweak.isShowButton()) {
                    grid.hideTweakButton(TweakType.Rotate);
                }
                // dunno why I did this but we gotta keep the -16/16 offset default for backwards compat
                if (rotateTweak.getButtonX() != null || rotateTweak.getButtonY() != null) {
                    grid.setButtonPosition(TweakType.Rotate,
                            unwrapOr(data.getButtonOffsetX(), -16) + unwrapOr(rotateTweak.getButtonX(), 0),
                            unwrapOr(data.getButtonOffsetY(), 16) + unwrapOr(rotateTweak.getButtonY(), 0));
                }

                var balanceTweak = data.getTweakBalance();
                if (!balanceTweak.isEnabled()) {
                    grid.disableTweak(TweakType.Balance);
                }
                if (!balanceTweak.isShowButton()) {
                    grid.hideTweakButton(TweakType.Balance);
                }
                if (balanceTweak.getButtonX() != null || balanceTweak.getButtonY() != null) {
                    grid.setButtonPosition(TweakType.Balance,
                            unwrapOr(data.getButtonOffsetX(), -16) + unwrapOr(balanceTweak.getButtonX(), 0),
                            unwrapOr(data.getButtonOffsetY(), 16) + unwrapOr(balanceTweak.getButtonY(), 0));
                }

                var clearTweak = data.getTweakClear();
                if (!clearTweak.isEnabled()) {
                    grid.disableTweak(TweakType.Clear);
                }
                if (!clearTweak.isShowButton()) {
                    grid.hideTweakButton(TweakType.Clear);
                }
                if (clearTweak.getButtonX() != null || clearTweak.getButtonY() != null) {
                    grid.setButtonPosition(TweakType.Clear,
                            unwrapOr(data.getButtonOffsetX(), -16) + unwrapOr(clearTweak.getButtonX(), 0),
                            unwrapOr(data.getButtonOffsetY(), 16) + unwrapOr(clearTweak.getButtonY(), 0));
                }
            }
        };
    }

    private static int unwrapOr(Integer integer, int defaultValue) {
        return integer != null ? integer : defaultValue;
    }
}