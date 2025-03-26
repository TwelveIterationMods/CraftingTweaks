package net.blay09.mods.craftingtweaks.api;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.balm.mixin.ImageButtonAccessor;
import net.blay09.mods.balm.mixin.ScreenAccessor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.Optional;
import java.util.function.Consumer;

public interface GridGuiHandler {
    /**
     * Called to add buttons to the GUI. May not be called if buttons are disabled in the configuration.
     * Use CraftingTweaksAPI.create***Button() to create tweak buttons, then add them to the buttonList.
     *
     * @param screen        the gui container the buttons are being added to
     * @param addWidgetFunc function to call for adding widgets to the screen
     */
    void createButtons(AbstractContainerScreen<?> screen, CraftingGrid grid, Consumer<AbstractWidget> addWidgetFunc);

    default void hideRecipeBookButton(AbstractContainerScreen<?> screen, AbstractWidget button) {
        button.visible = false;
    }

    default void repositionRecipeBookButton(AbstractContainerScreen<?> screen, AbstractWidget button) {
        final var accessor = (AbstractContainerScreenAccessor) screen;
        button.setX(accessor.getLeftPos() + accessor.getImageWidth() - 25);
        button.setY(accessor.getTopPos() + 5);
    }

    default Optional<AbstractWidget> findRecipeBookButton(AbstractContainerScreen<?> screen) {
        final var buttons = ((ScreenAccessor) screen).balm_getChildren();
        return buttons
                .stream()
                .filter(p -> p instanceof ImageButtonAccessor imageButton && imageButton.getSprites().get(false, false).getPath().equals("recipe_book/button"))
                .findFirst()
                .map(it -> (AbstractWidget) it);
    }
}
