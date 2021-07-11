package net.blay09.mods.craftingtweaks.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.balm.config.Comment;
import net.blay09.mods.balm.config.BalmConfig;
import net.blay09.mods.balm.config.BalmConfigHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(name = CraftingTweaks.MOD_ID)
public class CraftingTweaksConfigData extends BalmConfig {

    @ConfigEntry.Gui.CollapsibleObject
    public Common common = new Common();

    @ConfigEntry.Gui.CollapsibleObject
    public Client client = new Client();

    public static class Common {
        @Comment("Set this to true if you want the (de)compress feature to work outside of crafting GUIs (only works if installed on server)")
        public boolean compressAnywhere = false;

        @Comment("A list of modid:name entries that will not be crafted by the compress key.")
        public List<String> compressBlacklist = Arrays.asList("minecraft:sandstone", "minecraft:iron_trapdoor");
    }

    public static class Client {
        @Comment("This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.")
        public boolean hideButtons = false;

        @Comment("If set to true, right-clicking the result slot in a crafting table will craft a full stack.")
        public boolean rightClickCraftsStack = true;

        @Comment("Set this to true if you don't want the tweak buttons' tooltips to show.")
        public boolean hideButtonTooltips = false;

        @Comment("We both know JEI is much better. This option hides Vanilla's crafting book button instead of moving it.")
        public boolean hideVanillaCraftingGuide = false;

        @Comment("Set to 'DEFAULT' to enable both buttons and hotkeys. Set to 'BUTTONS' to enable buttons only. Set to 'HOTKEYS' to enable hotkeys only.")
        public CraftingTweaksMode craftingTweaksMode = CraftingTweaksMode.DEFAULT;

        @Comment("Add mod ids here of mods that you wish to disable Crafting Tweaks support for.")
        public List<String> disabledAddons = new ArrayList<>();
    }

    public CraftingTweaksMode getCraftingTweaksMode(String modId) {
        if (client.disabledAddons.contains(modId)) {
            return CraftingTweaksMode.DISABLED;
        }

        return client.craftingTweaksMode;
    }
}
