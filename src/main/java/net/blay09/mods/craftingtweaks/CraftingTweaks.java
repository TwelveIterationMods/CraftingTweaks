package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.craftingtweaks.addon.*;
import net.blay09.mods.craftingtweaks.addon.appliedenergistics2.AE2CraftingTerminalTweakProvider;
import net.blay09.mods.craftingtweaks.addon.appliedenergistics2.AE2PatternTerminalTweakProvider;
import net.blay09.mods.craftingtweaks.addon.forestry.ForestryAddon;
import net.blay09.mods.craftingtweaks.addon.ganyssurface.GanysDualWorktableTweakProvider;
import net.blay09.mods.craftingtweaks.addon.ganyssurface.GanysWorktableTweakProvider;
import net.blay09.mods.craftingtweaks.addon.railcraft.RailcraftRollingMachineTweakProvider;
import net.blay09.mods.craftingtweaks.addon.railcraft.RailcraftWorkCartTweakProvider;
import net.blay09.mods.craftingtweaks.addon.terrafirmacraft.TerraFirmaCraftOldTweakProvider;
import net.blay09.mods.craftingtweaks.addon.terrafirmacraft.TerraFirmaCraftTweakProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;

import java.util.Map;

@Mod(modid = CraftingTweaks.MOD_ID, name = "Crafting Tweaks", acceptableRemoteVersions = "*")
public class CraftingTweaks {

    public enum ModSupportState {
        ENABLED,
        BUTTONS_ONLY,
        HOTKEYS_ONLY,
        DISABLED;

        public static ModSupportState fromName(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ENABLED;
            }
        }

        public static String[] getValidValues() {
            ModSupportState[] values = ModSupportState.values();
            String[] validValues = new String[values.length];
            for(int i = 0; i < values.length; i++) {
                validValues[i] = values[i].name().toLowerCase();
            }
            return validValues;
        }
    }

    public static final Logger logger = LogManager.getLogger();
    public static final String MOD_ID = "craftingtweaks";

    @Mod.Instance
    public static CraftingTweaks instance;

    @SidedProxy(clientSide = "net.blay09.mods.craftingtweaks.client.ClientProxy", serverSide = "net.blay09.mods.craftingtweaks.CommonProxy")
    public static CommonProxy proxy;

    private static Configuration config;

    private final Map<String, ModSupportState> configMap = Maps.newHashMap();
    private final Map<Class<? extends Container>, TweakProvider> providerMap = Maps.newHashMap();

    public static boolean hideButtons;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CraftingTweaksAPI.setupAPI(new InternalMethodsImpl());

        configMap.put("minecraft", ModSupportState.ENABLED);
        configMap.put("TConstruct", ModSupportState.ENABLED);
        configMap.put("appliedenergistics2", ModSupportState.ENABLED);
        configMap.put("DraconicEvolution", ModSupportState.ENABLED);
        configMap.put("StevesWorkshop", ModSupportState.ENABLED);
        configMap.put("Natura", ModSupportState.ENABLED);
        configMap.put("Thaumcraft", ModSupportState.ENABLED);
        configMap.put("MineFactoryReloaded", ModSupportState.ENABLED);
        configMap.put("Forestry", ModSupportState.ENABLED);
        configMap.put("Railcraft", ModSupportState.ENABLED);
        configMap.put("BuildCraft|Factory", ModSupportState.ENABLED);
        configMap.put("RotaryCraft", ModSupportState.ENABLED);
        configMap.put("RotaryCraft", ModSupportState.ENABLED);
        configMap.put("TwilightForest", ModSupportState.ENABLED);
        configMap.put("terrafirmacraft", ModSupportState.ENABLED);
        configMap.put("ganyssurface", ModSupportState.ENABLED);
        configMap.put("jacb", ModSupportState.ENABLED);
        configMap.put("bluepower", ModSupportState.ENABLED);
        configMap.put("BiblioCraft", ModSupportState.ENABLED);
        configMap.put("ThermalExpansion", ModSupportState.ENABLED);
        configMap.put("Backpack", ModSupportState.ENABLED);

        config = new Configuration(event.getSuggestedConfigurationFile());
        hideButtons = config.getBoolean("hideButtons", "general", false, "This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.");
        config.setCategoryComment("addons", "Here you can control whether support for a mod should be enabled, buttons_only, hotkeys_only or disabled. For Vanilla Minecraft, see the option 'minecraft'. Mods are identified by their mod ids.");
        config.getString("minecraft", "addons", ModSupportState.ENABLED.name().toLowerCase(), "", ModSupportState.getValidValues());
        // Load all options (including those from non-included addons)
        for(Property property : config.getCategory("addons").values()) {
            configMap.put(property.getName(), ModSupportState.fromName(config.getString(property.getName(), "addons", ModSupportState.ENABLED.name().toLowerCase(), "enabled, buttons_only, hotkeys_only or disabled", ModSupportState.getValidValues())));
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        NetworkHandler.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        providerMap.put(ContainerWorkbench.class, new VanillaTweakProviderImpl());
        registerProvider("tconstruct.tools.inventory.CraftingStationContainer", new TinkersConstructTweakProvider());
        registerProvider("appeng.container.implementations.ContainerCraftingTerm", new AE2CraftingTerminalTweakProvider());
        registerProvider("appeng.container.implementations.ContainerPatternTerm", new AE2PatternTerminalTweakProvider());
        registerProvider("com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest", new DraconicEvolutionTweakProvider());
        registerProvider("vswe.production.gui.container.ContainerTable", new StevesWorkshopTweakProvider());
        registerProvider("mods.natura.gui.WorkbenchContainer", new NaturaTweakProvider());
        registerProvider("thaumcraft.common.container.ContainerArcaneWorkbench", new ThaumCraft4TweakProvider());
        registerProvider("powercrystals.minefactoryreloaded.gui.container.ContainerLiquiCrafter", new MineFactoryReloadedTweakProvider());

        new ForestryAddon();

        registerProvider("mods.railcraft.common.gui.containers.ContainerRollingMachine", new RailcraftRollingMachineTweakProvider());
        registerProvider("mods.railcraft.common.gui.containers.ContainerWorkCart", new RailcraftWorkCartTweakProvider());
        registerProvider("buildcraft.factory.gui.ContainerAutoWorkbench", new BuildcraftTweakProvider());
        registerProvider("Reika.RotaryCraft.Containers.ContainerHandCraft", new RotaryCraftDefaultTweakProvider("Reika.RotaryCraft.Containers.ContainerHandCraft"));
        registerProvider("Reika.RotaryCraft.Containers.ContainerCraftingPattern", new RotaryCraftDefaultTweakProvider("Reika.RotaryCraft.Containers.ContainerCraftingPattern"));
        registerProvider("twilightforest.uncrafting.ContainerTFUncrafting", new TwilightForestTweakProvider());
        registerProvider("com.bioxx.tfc.Containers.ContainerWorkbench", new TerraFirmaCraftOldTweakProvider());
        registerProvider("com.bioxx.tfc.Containers.ContainerPlayerTFC", new TerraFirmaCraftTweakProvider());
        registerProvider("ganymedes01.ganyssurface.inventory.ContainerWorkTable", new GanysWorktableTweakProvider());
        registerProvider("ganymedes01.ganyssurface.inventory.ContainerDualWorkTable", new GanysDualWorktableTweakProvider());
        registerProvider("tv.vanhal.jacb.gui.BenchContainer", new JACBTweakProvider());
        registerProvider("com.bluepowermod.container.ContainerProjectTable", new BluePowerTweakProvider());
        registerProvider("jds.bibliocraft.blocks.ContainerFancyWorkbench", new BiblioCraftTweakProvider());
        registerProvider("cofh.thermalexpansion.gui.container.device.ContainerWorkbench", new ThermalExpansionTweakProvider());
        registerProvider("de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack", new BackpacksTweakProvider());
        registerProvider("fox.spiteful.avaritia.gui.ContainerExtremeCrafting", new AvaritiaTweakProvider());

        config.save();
    }

    public void registerProvider(Class<? extends Container> clazz, TweakProvider provider) {
        if(!provider.getModId().equals("minecraft") && !Loader.isModLoaded(provider.getModId())) {
            return;
        }
        if(provider.load()) {
            providerMap.put(clazz, provider);
        }
    }

    @SuppressWarnings("unchecked")
    public void registerProvider(String className, TweakProvider provider) {
        config.getString(provider.getModId(), "addons", ModSupportState.ENABLED.name().toLowerCase(), "enabled, buttons_only, hotkeys_only or disabled", ModSupportState.getValidValues());
        if(Loader.isModLoaded(provider.getModId())) {
            if(provider.load()) {
                try {
                    Class clazz = Class.forName(className);
                    if (Container.class.isAssignableFrom(clazz)) {
                        providerMap.put(clazz, provider);
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }

    public TweakProvider getProvider(Container container) {
        if(container == null) {
            return null;
        }
        for(Class clazz : providerMap.keySet()) {
            if(container.getClass() == clazz) {
                return providerMap.get(clazz);
            }
        }
        for(Class<? extends Container> clazz : providerMap.keySet()) {
            if(clazz.isAssignableFrom(container.getClass())) {
                return providerMap.get(clazz);
            }
        }
        return null;
    }

    public ModSupportState getModSupportState(String modId) {
        ModSupportState suportState = configMap.get(modId);
        if(suportState == null) {
            suportState = ModSupportState.ENABLED;
            configMap.put(modId, suportState);
        }
        return suportState;
    }

    public static void saveConfig() {
        config.get("general", "hideButtons", false, "This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.").set(hideButtons);
        config.save();
    }

    @SideOnly(Side.CLIENT)
    public static boolean onGuiClick(int mouseX, int mouseY, int button) {
        if(Mouse.getEventButtonState()) {
            GuiClickEvent event = new GuiClickEvent(FMLClientHandler.instance().getClient().currentScreen, mouseX, mouseY, button);
            MinecraftForge.EVENT_BUS.post(event);
            return event.isCanceled();
        }
        return false;
    }

}
