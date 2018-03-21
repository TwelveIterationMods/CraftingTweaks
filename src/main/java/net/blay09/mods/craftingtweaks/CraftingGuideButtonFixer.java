package net.blay09.mods.craftingtweaks;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.List;

public class CraftingGuideButtonFixer {

    public static void fixMistakes(GuiContainer guiContainer, List<GuiButton> buttonList) {
        GuiButton button = findCraftButton(buttonList);
        if (button != null) {
            if (CraftingTweaks.hideVanillaCraftingGuide) {
                button.visible = false;
            } else if (!CraftingTweaks.hideButtons && !(guiContainer instanceof GuiInventory)) {
                button.x = guiContainer.getGuiLeft() + guiContainer.getXSize() - 25;

                // Let's be hacky because fuck this button. Hopefully no one else adds it to their GUIs.
                if (guiContainer.getClass().getSimpleName().equals("GuiCraftingStation")) {
                    button.y = guiContainer.getGuiTop() + 37;
                } else {
                    button.y = guiContainer.getGuiTop() + 5;
                }

                // Let's be hacky again!
                if (Loader.isModLoaded("inventorytweaks")) {
                    button.x -= 15;
                }
            }
        }
    }

    @Nullable
    private static GuiButton findCraftButton(List<GuiButton> buttonList) {
        return buttonList.stream().filter(p -> p instanceof GuiButtonImage &&
                (((GuiButtonImage) p).resourceLocation.getResourcePath().equals("textures/gui/container/crafting_table.png")
                        || ((GuiButtonImage) p).resourceLocation.getResourcePath().equals("textures/gui/container/inventory.png"))
        ).findFirst().orElse(null);
    }

}
