package net.blay09.mods.craftingtweaks.compat;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksClientAPI;
import net.blay09.mods.craftingtweaks.api.impl.DefaultGridGuiHandler;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class TomsStorageGuiHandlers extends DefaultGridGuiHandler {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TomsStorageGuiHandlers() {
        try {
            final var craftingTerminalScreen = Class.forName("com.tom.storagemod.screen.CraftingTerminalScreen");
            CraftingTweaksClientAPI.registerCraftingGridGuiHandler((Class) craftingTerminalScreen, this);
        } catch (ClassNotFoundException e) {
            CraftingTweaks.logger.error("Failed to load Tom's Simple Storage compatibility", e);
        }
    }

    @Override
    public void repositionRecipeBookButton(AbstractContainerScreen<?> screen, AbstractWidget button) {
        final var accessor = (AbstractContainerScreenAccessor) screen;
        button.setX(accessor.getLeftPos() + accessor.getImageWidth() - 47);
    }
}
