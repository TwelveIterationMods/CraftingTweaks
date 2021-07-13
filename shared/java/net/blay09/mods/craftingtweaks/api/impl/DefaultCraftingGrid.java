package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.craftingtweaks.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.*;

public class DefaultCraftingGrid implements CraftingGrid, CraftingGridDecorator, GridGuiSettings {

    private final ResourceLocation id;
    private final int start;
    private final int size;

    private final Set<TweakType> hiddenButtons = new HashSet<>();
    private final Map<TweakType, ButtonPosition> buttonPositions = new HashMap<>();

    private GridClearHandler<AbstractContainerMenu> clearHandler = CraftingGrid.super.clearHandler();
    private GridBalanceHandler<AbstractContainerMenu> balanceHandler = CraftingGrid.super.balanceHandler();
    private GridRotateHandler<AbstractContainerMenu> rotateHandler = CraftingGrid.super.rotateHandler();
    private final GridTransferHandler<AbstractContainerMenu> transferHandler = CraftingGrid.super.transferHandler();

    private ButtonAlignment buttonAlignment = ButtonAlignment.LEFT;

    public DefaultCraftingGrid(ResourceLocation id, int start, int size) {
        this.id = id;
        this.start = start;
        this.size = size;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public int getGridStartSlot(Player player, AbstractContainerMenu menu) {
        return start;
    }

    @Override
    public int getGridSize(Player player, AbstractContainerMenu menu) {
        return size;
    }

    @Override
    public void disableTweak(TweakType tweak) {
        switch (tweak) {
            case Clear -> clearHandler = new NoopHandler();
            case Balance -> balanceHandler = new NoopHandler();
            case Rotate -> rotateHandler = new NoopHandler();
        }
    }

    @Override
    public void disableAllTweaks() {
        disableTweak(TweakType.Balance);
        disableTweak(TweakType.Rotate);
        disableTweak(TweakType.Clear);
    }

    @Override
    public void usePhantomItems() {
        if (clearHandler instanceof DefaultGridClearHandler defaultClearHandler) {
            defaultClearHandler.setPhantomItems(true);
        }
    }

    @Override
    public GridClearHandler<AbstractContainerMenu> clearHandler() {
        return clearHandler;
    }

    @Override
    public GridBalanceHandler<AbstractContainerMenu> balanceHandler() {
        return balanceHandler;
    }

    @Override
    public GridTransferHandler<AbstractContainerMenu> transferHandler() {
        return transferHandler;
    }

    @Override
    public GridRotateHandler<AbstractContainerMenu> rotateHandler() {
        return rotateHandler;
    }

    @Override
    public void hideTweakButton(TweakType tweak) {
        hiddenButtons.add(tweak);
    }

    @Override
    public void hideAllTweakButtons() {
        hideTweakButton(TweakType.Clear);
        hideTweakButton(TweakType.Balance);
        hideTweakButton(TweakType.Rotate);
    }

    @Override
    public void setButtonAlignment(ButtonAlignment alignment) {
        buttonAlignment = alignment;
    }

    @Override
    public void setButtonPosition(TweakType tweak, int x, int y) {
        buttonPositions.put(tweak, new ButtonPosition(x, y));
    }

    @Override
    public boolean isButtonVisible(TweakType tweak) {
        return !hiddenButtons.contains(tweak);
    }

    @Override
    public ButtonAlignment getButtonAlignment() {
        return buttonAlignment;
    }

    @Override
    public Optional<ButtonPosition> getButtonPosition(TweakType tweak) {
        return Optional.ofNullable(buttonPositions.get(tweak));
    }
}
