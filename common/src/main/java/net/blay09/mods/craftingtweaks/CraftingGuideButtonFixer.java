package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.GridGuiHandler;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class CraftingGuideButtonFixer {

    public static AbstractWidget fixMistakes(AbstractContainerScreen<?> screen, GridGuiHandler guiHandler) {
        final var opt = guiHandler.findRecipeBookButton(screen);
        opt.ifPresent(button -> {
            if (CraftingTweaksConfig.getActive().client.hideVanillaCraftingGuide) {
                guiHandler.hideRecipeBookButton(screen, button);
            } else {
                guiHandler.repositionRecipeBookButton(screen, button);
            }
        });
        return opt.orElse(null);
    }

}
