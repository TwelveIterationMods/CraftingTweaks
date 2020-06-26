package net.blay09.mods.craftingtweaks;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.List;

public class CraftingGuideButtonFixer {

    public static void fixMistakes(ContainerScreen<?> guiContainer, List<? extends IGuiEventListener> widgets) {
        Button button = findCraftButton(widgets);
        if (button != null) {
            if (CraftingTweaksConfig.CLIENT.hideVanillaCraftingGuide.get()) {
                button.field_230694_p_ = false; // visible
            } else if (!CraftingTweaksConfig.CLIENT.hideButtons.get() && !(guiContainer instanceof InventoryScreen)) {
                button.field_230690_l_ = guiContainer.getGuiLeft() + guiContainer.getXSize() - 25; // x

                // Let's be hacky because fuck this button. Hopefully no one else adds it to their GUIs.
                if (guiContainer.getClass().getSimpleName().equals("GuiCraftingStation")) {
                    button.field_230691_m_ = guiContainer.getGuiTop() + 37; // y
                } else {
                    button.field_230691_m_ = guiContainer.getGuiTop() + 5; // y
                }

                // Let's be hacky again!
                if (ModList.get().isLoaded("inventorytweaks")) {
                    button.field_230690_l_ -= 15; // x
                }
            }
        }
    }

    @Nullable
    private static Button findCraftButton(List<? extends IGuiEventListener> buttonList) {
        return (Button) buttonList
                .stream()
                .filter(p -> p instanceof ImageButton && ((ImageButton) p).resourceLocation.getPath().equals("textures/gui/recipe_button.png"))
                .findFirst().orElse(null);
    }

}
