package net.blay09.mods.craftingtweaks;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.List;

public class CraftingGuideButtonFixer {

	public static void fixMistakes(GuiContainer guiContainer, List<GuiButton> buttonList) {
		GuiButton button = buttonList.stream().filter(p -> p instanceof GuiButtonImage && ((GuiButtonImage) p).resourceLocation.getResourcePath().equals("textures/gui/container/crafting_table.png")).findFirst().orElse(null);
		if(button != null) {
			button.x = guiContainer.getGuiLeft() + guiContainer.getXSize() - 25;

			// Let's be hacky because fuck this button. Hopefully no one else adds it to their GUIs.
			if(guiContainer.getClass().getSimpleName().equals("GuiCraftingStation")) {
				button.y = guiContainer.getGuiTop() + 37;
			} else {
				button.y = guiContainer.getGuiTop() + 5;
			}
		}
	}

}
