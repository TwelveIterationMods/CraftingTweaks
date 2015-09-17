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
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;

import java.util.Map;

@Mod(modid = CraftingTweaks.MOD_ID)
public class CraftingTweaks {

    public static final String MOD_ID = "craftingtweaks";

    @Mod.Instance
    public static CraftingTweaks instance;

    @SidedProxy(clientSide = "net.blay09.mods.craftingtweaks.client.ClientProxy", serverSide = "net.blay09.mods.craftingtweaks.CommonProxy")
    public static CommonProxy proxy;

    private final Map<Class<? extends Container>, TweakProvider> providerMap = Maps.newHashMap();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CraftingTweaksAPI.setupAPI(new InternalMethodsImpl());
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
        if(Loader.isModLoaded("TConstruct")) {
            registerProvider("tconstruct.tools.inventory.CraftingStationContainer", new TinkersConstructTweakProvider());
        }
        if(Loader.isModLoaded("appliedenergistics2")) {
            registerProvider("appeng.container.implementations.ContainerCraftingTerm", new AE2CraftingTerminalTweakProvider());
            registerProvider("appeng.container.implementations.ContainerPatternTerm", new AE2PatternTerminalTweakProvider());
        }
        if(Loader.isModLoaded("DraconicEvolution")) {
            registerProvider("com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest", new DraconicEvolutionTweakProvider());
        }
        if(Loader.isModLoaded("StevesWorkshop")) {
            registerProvider("vswe.production.gui.container.ContainerTable", new StevesWorkshopTweakProvider());
        }
        if(Loader.isModLoaded("Natura")) {
            registerProvider("mods.natura.gui.WorkbenchContainer", new NaturaTweakProvider());
        }
        if(Loader.isModLoaded("Thaumcraft")) {
            registerProvider("thaumcraft.common.container.ContainerArcaneWorkbench", new ThaumCraft4TweakProvider());
        }
        if(Loader.isModLoaded("MineFactoryReloaded")) {
            registerProvider("powercrystals.minefactoryreloaded.gui.container.ContainerLiquiCrafter", new MineFactoryReloadedTweakProvider());
        }
        if(Loader.isModLoaded("Forestry")) {
            registerProvider("forestry.factory.gui.ContainerWorktable", new ForestryWorktableTweakProvider());
            registerProvider("forestry.factory.gui.ContainerCarpenter", new ForestryCarpenterTweakProvider());
            registerProvider("forestry.factory.gui.ContainerFabricator", new ForestryFabricatorTweakProvider());
        }
        if(Loader.isModLoaded("Railcraft")) {
            registerProvider("mods.railcraft.common.gui.containers.ContainerRollingMachine", new RailcraftRollingMachineTweakProvider());
            registerProvider("mods.railcraft.common.gui.containers.ContainerWorkCart", new RailcraftWorkCartTweakProvider());
        }
        if(Loader.isModLoaded("BuildCraft|Factory")) {
            registerProvider("buildcraft.factory.gui.ContainerAutoWorkbench", new BuildcraftTweakProvider());
        }
        if(Loader.isModLoaded("RotaryCraft")) {
            registerProvider("Reika.RotaryCraft.Containers.ContainerHandCraft", new RotaryCraftDefaultTweakProvider("Reika.RotaryCraft.Containers.ContainerHandCraft"));
            registerProvider("Reika.RotaryCraft.Containers.ContainerCraftingPattern", new RotaryCraftDefaultTweakProvider("Reika.RotaryCraft.Containers.ContainerCraftingPattern"));
        }
        if(Loader.isModLoaded("TwilightForest")) {
            registerProvider("twilightforest.uncrafting.ContainerTFUncrafting", new TwilightForestTweakProvider());
        }
        if(Loader.isModLoaded("terrafirmacraft")) {
            registerProvider("com.bioxx.tfc.Containers.ContainerWorkbench", new TerraFirmaCraftOldTweakProvider());
            registerProvider("com.bioxx.tfc.Containers.ContainerPlayerTFC", new TerraFirmaCraftTweakProvider());
        }
        if(Loader.isModLoaded("ganyssurface")) {
            registerProvider("ganymedes01.ganyssurface.inventory.ContainerWorkTable", new GanysWorktableTweakProvider());
            registerProvider("ganymedes01.ganyssurface.inventory.ContainerDualWorkTable", new GanysDualWorktableTweakProvider());
        }
        if(Loader.isModLoaded("jacb")) {
            registerProvider("tv.vanhal.jacb.gui.BenchContainer", new JACBTweakProvider());
        }
        if(Loader.isModLoaded("bluepower")) {
            registerProvider("com.bluepowermod.container.ContainerProjectTable", new BluePowerTweakProvider());
        }
    }

    public void registerProvider(Class<? extends Container> clazz, TweakProvider provider) {
        if(provider.isLoaded()) {
            providerMap.put(clazz, provider);
        }
    }

    @SuppressWarnings("unchecked")
    public void registerProvider(String className, TweakProvider provider) {
        if(provider.isLoaded()) {
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
}
