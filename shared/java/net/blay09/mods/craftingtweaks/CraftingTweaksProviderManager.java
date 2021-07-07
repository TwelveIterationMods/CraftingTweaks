package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.forbic.ForbicModList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CraftingTweaksProviderManager {

    private static final Map<Class<? extends AbstractContainerMenu>, TweakProvider> providerMap = Maps.newHashMap();

    public static <T extends AbstractContainerMenu> void registerProvider(Class<T> clazz, TweakProvider<T> provider) {
        if (!provider.getModId().equals("minecraft") && !ForbicModList.isLoaded(provider.getModId())) {
            return;
        }
        if (provider.load()) {
            providerMap.put(clazz, provider);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerProvider(String className, TweakProvider<?> provider) {
        if (ForbicModList.isLoaded(provider.getModId())) {
            if (provider.load()) {
                try {
                    Class<? extends AbstractContainerMenu> clazz = (Class<? extends AbstractContainerMenu>) Class.forName(className);
                    if (AbstractContainerMenu.class.isAssignableFrom(clazz)) {
                        providerMap.put(clazz, provider);
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends AbstractContainerMenu> TweakProvider<T> getProvider(@Nullable T container) {
        if (container == null) {
            return null;
        }
        for (Class<? extends AbstractContainerMenu> clazz : providerMap.keySet()) {
            if (container.getClass() == clazz) {
                return (TweakProvider<T>) providerMap.get(clazz);
            }
        }
        for (Class<? extends AbstractContainerMenu> clazz : providerMap.keySet()) {
            if (clazz.isAssignableFrom(container.getClass())) {
                return (TweakProvider<T>) providerMap.get(clazz);
            }
        }
        return null;
    }

}
