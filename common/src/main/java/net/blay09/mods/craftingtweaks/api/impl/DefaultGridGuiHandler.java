package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.craftingtweaks.api.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.function.Consumer;

public class DefaultGridGuiHandler implements GridGuiHandler {
    @Override
    public void createButtons(AbstractContainerScreen<?> screen, CraftingGrid grid, Consumer<AbstractWidget> addWidgetFunc) {
        GridGuiSettings guiSettings;
        if (grid instanceof GridGuiSettings) {
            guiSettings = ((GridGuiSettings) grid);
        } else {
            guiSettings = new DefaultGridGuiSettings();
        }

        int index = 0;
        if (createTweakButton(screen, grid, addWidgetFunc, guiSettings, index, TweakType.Rotate)) {
            index++;
        }
        if (createTweakButton(screen, grid, addWidgetFunc, guiSettings, index, TweakType.Balance)) {
            index++;
        }
        createTweakButton(screen, grid, addWidgetFunc, guiSettings, index, TweakType.Clear);
    }

    private boolean createTweakButton(AbstractContainerScreen<?> screen, CraftingGrid grid, Consumer<AbstractWidget> addWidgetFunc, GridGuiSettings guiSettings, int index, TweakType tweak) {
        if (guiSettings.isButtonVisible(tweak)) {
            ButtonPosition buttonPos = guiSettings.getButtonPosition(tweak).orElseGet(() -> getAlignedPosition(screen.getMenu(), grid, guiSettings, index));
            addWidgetFunc.accept(CraftingTweaksClientAPI.createTweakButtonRelative(grid,
                    screen,
                    buttonPos.getX(),
                    buttonPos.getY(),
                    tweak,
                    guiSettings.getButtonStyle()));
            return true;
        }

        return false;
    }

    private ButtonPosition getAlignedPosition(AbstractContainerMenu menu, CraftingGrid grid, GridGuiSettings guiSettings, int index) {
        final var alignment = guiSettings.getButtonAlignment();
        final var style = guiSettings.getButtonStyle();
        final var offsetX = guiSettings.getButtonAlignmentOffsetX();
        final var offsetY = guiSettings.getButtonAlignmentOffsetY();
        Player player = Minecraft.getInstance().player;
        Slot firstSlot = menu.slots.get(grid.getGridStartSlot(player, menu));
        int gridLength = (int) Math.sqrt(grid.getGridSize(player, menu));
        return switch (alignment) {
            case TOP ->
                    new ButtonPosition(offsetX + firstSlot.x + style.getSpacingX() * index, offsetY + firstSlot.y - style.getSpacingY() - style.getMarginY());
            case BOTTOM ->
                    new ButtonPosition(offsetX + firstSlot.x + style.getSpacingX() * index, offsetY + firstSlot.y + 18 * gridLength + style.getMarginY());
            case RIGHT -> new ButtonPosition(offsetX + firstSlot.x + 18 * gridLength + style.getMarginX(), offsetY + firstSlot.y + style.getSpacingY() * index);
            case LEFT ->
                    new ButtonPosition(offsetX + firstSlot.x - style.getSpacingX() - style.getMarginX(), offsetY + firstSlot.y + style.getSpacingY() * index);
        };
    }

}