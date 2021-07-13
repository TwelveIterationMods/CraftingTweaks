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
            ButtonPosition buttonPos = guiSettings.getButtonPosition(tweak).orElseGet(() -> getAlignedPosition(screen.getMenu(), grid, guiSettings.getButtonAlignment(), index));
            addWidgetFunc.accept(CraftingTweaksClientAPI.createTweakButtonRelative(grid, screen, buttonPos.getX(), buttonPos.getY(), tweak));
            return true;
        }

        return false;
    }

    private ButtonPosition getAlignedPosition(AbstractContainerMenu menu, CraftingGrid grid, ButtonAlignment alignment, int index) {
        Player player = Minecraft.getInstance().player;
        Slot firstSlot = menu.slots.get(grid.getGridStartSlot(player, menu));
        return switch (alignment) {
            case TOP -> new ButtonPosition(firstSlot.x + 18 * index, firstSlot.y - 18 - 1);
            case BOTTOM -> new ButtonPosition(firstSlot.x + 18 * index, firstSlot.y + 18 * 3 + 1);
            case RIGHT -> new ButtonPosition(firstSlot.x + 18 * 3 + 1, firstSlot.y + 18 * index);
            case LEFT -> new ButtonPosition(firstSlot.x - 19, firstSlot.y + 18 * index);
        };
    }

}