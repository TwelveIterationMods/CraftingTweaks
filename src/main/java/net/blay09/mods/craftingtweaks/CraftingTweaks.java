package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.blay09.mods.craftingtweaks.addon.*;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.Map;

@Mod(modid = CraftingTweaks.MOD_ID, name = "Crafting Tweaks")
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

    public static final String MOD_ID = "craftingtweaks";

    @Mod.Instance
    public static CraftingTweaks instance;

    @SidedProxy(clientSide = "net.blay09.mods.craftingtweaks.client.ClientProxy", serverSide = "net.blay09.mods.craftingtweaks.CommonProxy")
    public static CommonProxy proxy;

    private static Configuration config;

    private final Map<String, ModSupportState> configMap = Maps.newHashMap();
    private final Map<Class<? extends Container>, TweakProvider> providerMap = Maps.newHashMap();

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
        config.setCategoryComment("addons", "Here you can control whether support for a mod should be enabled, buttons_only, hotkeys_only or disabled. For Vanilla Minecraft, see the option 'minecraft'. Mods are identified by their mod ids.");
        config.getString("minecraft", "addons", ModSupportState.ENABLED.name().toLowerCase(), "", ModSupportState.getValidValues());
        // Load all options (including those from non-included addons)
        for(Property property : config.getCategory("addons").values()) {
            configMap.put(property.getName(), ModSupportState.fromName(config.getString(property.getName(), "addons", ModSupportState.ENABLED.name().toLowerCase(), "", ModSupportState.getValidValues())));
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
        registerProvider("forestry.factory.gui.ContainerWorktable", new ForestryWorktableTweakProvider());
        registerProvider("forestry.factory.gui.ContainerCarpenter", new ForestryCarpenterTweakProvider());
        registerProvider("forestry.factory.gui.ContainerFabricator", new ForestryFabricatorTweakProvider());
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

        config.save();
    }

    public void registerProvider(Class<? extends Container> clazz, TweakProvider provider) {
        if(!provider.getModId().equals("minecraft") && !Loader.isModLoaded(provider.getModId())) {
            return;
        }
        if(provider.isLoaded()) {
            providerMap.put(clazz, provider);
        }
    }

    @SuppressWarnings("unchecked")
    public void registerProvider(String className, TweakProvider provider) {
        config.getString(provider.getModId(), "addons", ModSupportState.ENABLED.name().toLowerCase(), "", ModSupportState.getValidValues());
        if(Loader.isModLoaded(provider.getModId()) && provider.isLoaded()) {
            try {
                Class clazz = Class.forName(className);
                if (Container.class.isAssignableFrom(clazz)) {
                    providerMap.put(clazz, provider);
                }
            } catch (ClassNotFoundException ignored) {}
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
        return configMap.get(modId);
    }

}
