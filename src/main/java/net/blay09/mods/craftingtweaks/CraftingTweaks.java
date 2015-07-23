package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.blay09.mods.craftingtweaks.addon.AppliedEnergistics2TweakProvider;
import net.blay09.mods.craftingtweaks.addon.DraconicEvolutionTweakProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.blay09.mods.craftingtweaks.addon.TinkersConstructTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.addon.VanillaTweakProviderImpl;
import net.minecraft.inventory.Container;
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

        providerMap.put(ContainerWorkbench.class, new VanillaTweakProviderImpl());
        registerProvider("tconstruct.tools.inventory.CraftingStationContainer", new TinkersConstructTweakProvider());
        registerProvider("appeng.container.implementations.ContainerCraftingTerm", new AppliedEnergistics2TweakProvider());
        registerProvider("com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest", new DraconicEvolutionTweakProvider());
    }

    public void registerProvider(Class<? extends Container> clazz, TweakProvider provider) {
        providerMap.put(clazz, provider);
    }

    @SuppressWarnings("unchecked")
    public void registerProvider(String className, TweakProvider provider) {
        try {
            Class clazz = Class.forName(className);
            if(Container.class.isAssignableFrom(clazz)) {
                providerMap.put(clazz, provider);
            }
        } catch (ClassNotFoundException ignored) {}
    }

    public TweakProvider getProvider(Container container) {
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
