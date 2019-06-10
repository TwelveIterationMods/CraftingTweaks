package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.Map;

public class CraftingTweaksProviderManager {

    private static final Map<Class<? extends Container>, TweakProvider> providerMap = Maps.newHashMap();

    public static <T extends Container> void registerProvider(Class<T> clazz, TweakProvider<T> provider) {
        if (!provider.getModId().equals("minecraft") && !ModList.get().isLoaded(provider.getModId())) {
            return;
        }
        if (provider.load()) {
            providerMap.put(clazz, provider);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerProvider(String className, TweakProvider provider) {
        CraftingTweaksConfig.addModSupportOption(provider.getModId());

        if (ModList.get().isLoaded(provider.getModId())) {
            if (provider.load()) {
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

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends Container> TweakProvider<T> getProvider(@Nullable T container) {
        if (container == null) {
            return null;
        }
        for (Class<? extends Container> clazz : providerMap.keySet()) {
            if (container.getClass() == clazz) {
                return (TweakProvider<T>) providerMap.get(clazz);
            }
        }
        for (Class<? extends Container> clazz : providerMap.keySet()) {
            if (clazz.isAssignableFrom(container.getClass())) {
                return (TweakProvider<T>) providerMap.get(clazz);
            }
        }
        return null;
    }

}
