package net.blay09.mods.craftingtweaks;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.Maps;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = CraftingTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CraftingTweaksConfig {

    public static class Common {
        public final ForgeConfigSpec.BooleanValue compressAnywhere;
        public final ForgeConfigSpec.ConfigValue<List<String>> compressBlacklist;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Crafting Tweaks Configuration").push("common");

            compressAnywhere = builder
                    .comment("Set this to true if you want the (de)compress feature to work outside of crafting GUIs (only works if installed on server)")
                    .translation("craftingtweaks.config.compressAnywhere")
                    .define("compressAnywhere", false);

            compressBlacklist = builder
                    .comment("A list of modid:name entries that will not be crafted by the compress key.")
                    .translation("craftingtweaks.config.compressBlacklist")
                    .define("compressBlacklist", Arrays.asList(
                            "minecraft:sandstone",
                            "minecraft:iron_trapdoor"
                    ));
        }
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue hideButtons;
        public final ForgeConfigSpec.BooleanValue rightClickCraftsStack;
        public final ForgeConfigSpec.BooleanValue hideButtonTooltips;
        public final ForgeConfigSpec.BooleanValue hideVanillaCraftingGuide;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Crafting Tweaks Client Configuration").push("client");

            hideButtons = builder
                    .comment("This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.")
                    .translation("craftingtweaks.config.hideButtons")
                    .define("hideButtons", false);

            hideVanillaCraftingGuide = builder
                    .comment("We both know JEI is much better. This option hides Vanilla's crafting book button instead of moving it.")
                    .translation("craftingtweaks.config.hideVanillaCraftingGuide")
                    .define("hideVanillaCraftingGuide", false);

            rightClickCraftsStack = builder
                    .comment("If set to true, right-clicking the result slot in a crafting table will craft a full stack.")
                    .translation("craftingtweaks.config.rightClickCraftsStack")
                    .define("rightClickCraftsStack", true);

            hideButtonTooltips = builder
                    .comment("Set this to true if you don't want the tweak buttons' tooltips to show.")
                    .translation("craftingtweaks.config.hideButtonTooltips")
                    .define("hideButtonTooltips", false);

            builder.comment("Here you can control whether support for a mod should be enabled, buttons_only, hotkeys_only or disabled. For Vanilla Minecraft, see the option 'minecraft'. Mods are identified by their mod ids.")
                    .push("addons").pop();
        }
    }

    static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    private static final Map<String, ModSupportState> configMap = Maps.newHashMap();
    private static ModConfig clientConfig;

    public static void setHideButtons(boolean hideButtons) {
        clientConfig.getConfigData().set(CLIENT.hideButtons.getPath(), hideButtons);
        clientConfig.save();
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            clientConfig = event.getConfig();
            addModSupportOption("minecraft");

            // Load all options (including those from non-included addons)
            CommentedConfig subConfig = clientConfig.getConfigData().get("client.addons");
            for (Map.Entry<String, Object> entry : subConfig.valueMap().entrySet()) {
                String modId = entry.getKey();
                ModSupportState state = ModSupportState.fromName(entry.getValue().toString());
                configMap.put(modId, state);
            }
        }
    }

    public static void addModSupportOption(String modId) {
        if (clientConfig != null) {
            String path = "client.addons." + modId;
            CommentedConfig configData = clientConfig.getConfigData();
            if (!configData.contains(path)) {
                configData.set(path, ModSupportState.ENABLED.name());
                configData.setComment(path, "State of this addon. Can be ENABLED, BUTTONS_ONLY, HOTKEYS_ONLY or DISABLED.");
                clientConfig.save();
            }
        }
    }

    public static ModSupportState getModSupportState(String modId) {
        return configMap.computeIfAbsent(modId, k -> ModSupportState.ENABLED);
    }
}
