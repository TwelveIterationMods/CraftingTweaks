package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.balm.mixin.ImageButtonAccessor;
import net.blay09.mods.balm.mixin.ScreenAccessor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CraftingGuideButtonFixer {

    public static Button fixMistakes(AbstractContainerScreen<?> screen) {
        Button button = findCraftButton(((ScreenAccessor) screen).balm_getChildren());
        if (button != null) {
            CraftingTweaksConfigData config = CraftingTweaksConfig.getActive();
            if (config.client.hideVanillaCraftingGuide) {
                button.visible = false;
            } else if (!(screen instanceof InventoryScreen)) {
                AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
                button.setX(accessor.getLeftPos() + accessor.getImageWidth() - 25);

                // Let's be hacky because fuck this button. Hopefully no one else adds it to their GUIs.
                if (screen.getClass().getSimpleName().equals("GuiCraftingStation")) {
                    button.setY(accessor.getTopPos() + 37);
                } else {
                    button.setY(accessor.getTopPos() + 5);
                }

                // Let's be hacky again!
                if (Balm.isModLoaded("inventorytweaks")) {
                    button.setX(button.getX() - 15);
                }
            }
        }
        return button;
    }

    @Nullable
    private static Button findCraftButton(List<? extends GuiEventListener> buttonList) {
        return (Button) buttonList
                .stream()
                .filter(p -> p instanceof ImageButtonAccessor imageButton && imageButton.getSprites().get(false, false).getPath().equals("recipe_book/button"))
                .findFirst().orElse(null);
    }

}
