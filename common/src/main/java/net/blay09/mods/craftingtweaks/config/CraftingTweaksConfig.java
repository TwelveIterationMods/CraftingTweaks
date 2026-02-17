package net.blay09.mods.craftingtweaks.config;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;
import net.blay09.mods.balm.platform.config.reflection.NestedType;
import net.blay09.mods.craftingtweaks.CraftingTweaks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(CraftingTweaks.MOD_ID)
public class CraftingTweaksConfig {

    public Common common = new Common();
    public Client client = new Client();

    public static class Common {
        @Comment("Set this to false if you want the (de)compress feature to work outside of crafting GUIs (only works if installed on server while in the player inventory)")
        public boolean compressRequiresCraftingGrid = true;

        @NestedType(String.class)
        @Comment("A list of modid:name entries that will not be crafted by the compress key.")
        public List<String> compressDenylist = Arrays.asList("minecraft:sandstone", "minecraft:iron_trapdoor");
    }

    public static class Client {
        @Comment("If set to true, right-clicking the result slot in a crafting table will craft a full stack.")
        public boolean rightClickCraftsStack = true;

        @Comment("We both know JEI is much better. This option hides Vanilla's crafting book button instead of moving it.")
        public boolean hideVanillaCraftingGuide = false;

        @Comment("Offset from the right when repositioning the vanilla recipe book button.")
        public int vanillaCraftingGuideOffsetX = -25;

        @Comment("Offset from the top when repositioning the vanilla recipe book button.")
        public int vanillaCraftingGuideOffsetY = 5;

        @Comment("Set to 'DEFAULT' to enable both buttons and hotkeys. Set to 'BUTTONS' to enable buttons only. Set to 'HOTKEYS' to enable hotkeys only. Set to 'DISABLED' to disable completely.")
        public CraftingTweaksMode mode = CraftingTweaksMode.DEFAULT;

        @NestedType(String.class)
        @Comment("Add mod ids here of mods that you wish to disable Crafting Tweaks support for.")
        public List<String> disabledAddons = new ArrayList<>();
    }

    public CraftingTweaksMode getCraftingTweaksMode(String modId) {
        if (client.disabledAddons.contains(modId)) {
            return CraftingTweaksMode.DISABLED;
        }

        return client.mode;
    }

    public static CraftingTweaksConfig getActive() {
        return Balm.config().getActiveConfig(CraftingTweaksConfig.class);
    }

    public static void initialize() {
        Balm.config().registerConfig(CraftingTweaksConfig.class);
    }
}
