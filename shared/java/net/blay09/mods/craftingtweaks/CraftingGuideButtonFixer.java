package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;
import net.blay09.mods.forbic.ForbicModList;
import net.blay09.mods.forbic.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.forbic.mixin.ImageButtonAccessor;
import net.blay09.mods.forbic.mixin.ScreenAccessor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CraftingGuideButtonFixer {

    public static void fixMistakes(AbstractContainerScreen<?> screen) {
        Button button = findCraftButton(((ScreenAccessor) screen).getChildren());
        if (button != null) {
            CraftingTweaksConfigData config = CraftingTweaksConfig.getActive();
            if (config.client.hideVanillaCraftingGuide) {
                button.visible = false;
            } else if (!config.client.hideButtons && !(screen instanceof InventoryScreen)) {
                AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
                button.x = accessor.getLeftPos() + accessor.getImageWidth() - 25;

                // Let's be hacky because fuck this button. Hopefully no one else adds it to their GUIs.
                if (screen.getClass().getSimpleName().equals("GuiCraftingStation")) {
                    button.y = accessor.getTopPos() + 37;
                } else {
                    button.y = accessor.getTopPos() + 5;
                }

                // Let's be hacky again!
                if (ForbicModList.isLoaded("inventorytweaks")) {
                    button.x -= 15;
                }
            }
        }
    }

    @Nullable
    private static Button findCraftButton(List<? extends GuiEventListener> buttonList) {
        return (Button) buttonList
                .stream()
                .filter(p -> p instanceof ImageButton && ((ImageButtonAccessor) p).getResourceLocation() != null && ((ImageButtonAccessor) p).getResourceLocation().getPath().equals("textures/gui/recipe_button.png"))
                .findFirst().orElse(null);
    }

}
