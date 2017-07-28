package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.Lists;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GuiFactory implements IModGuiFactory {
	@Override
	public void initialize(Minecraft minecraft) {

	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new ConfigGUI(parentScreen);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.emptySet();
	}

	public static class ConfigGUI extends GuiConfig {
		public ConfigGUI(GuiScreen parentScreen) {
			super(parentScreen, getElements(), CraftingTweaks.MOD_ID, "", false, false, "Crafting Tweaks");
		}

		private static List<IConfigElement> getElements() {
			List<IConfigElement> list = Lists.newArrayList();
			list.addAll(new ConfigElement(CraftingTweaks.config.getCategory("general")).getChildElements());
			list.addAll(new ConfigElement(CraftingTweaks.config.getCategory("addons")).getChildElements());
			return list;
		}
	}
}
