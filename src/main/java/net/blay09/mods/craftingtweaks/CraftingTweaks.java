package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.blay09.mods.craftingtweaks.provider.TweakProvider;
import net.blay09.mods.craftingtweaks.provider.VanillaTweakProvider;
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

    private Map<Class, TweakProvider> providerMap = Maps.newHashMap();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        NetworkHandler.init();

        providerMap.put(ContainerWorkbench.class, new VanillaTweakProvider());
    }

    public TweakProvider getProvider(Container container) {
        for(Class clazz : providerMap.keySet()) {
            if(container.getClass() == clazz) {
                return providerMap.get(clazz);
            }
        }
        for(Class clazz : providerMap.keySet()) {
            if(clazz.isAssignableFrom(container.getClass())) {
                return providerMap.get(clazz);
            }
        }
        return null;
    }
}
