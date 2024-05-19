package net.blay09.mods.craftingtweaks.api;

import java.lang.reflect.InvocationTargetException;

public class CraftingTweaksAPI {

    private static final InternalMethods internalMethods = loadInternalMethods();

    private static InternalMethods loadInternalMethods() {
        try {
            return (InternalMethods) Class.forName("net.blay09.mods.craftingtweaks.api.impl.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException("Failed to load Crafting Tweaks API", e);
        }
    }

    public static void registerCraftingGridProvider(CraftingGridProvider provider) {
        internalMethods.registerCraftingGridProvider(provider);
    }

    public static void unregisterCraftingGridProvider(CraftingGridProvider provider) {
        internalMethods.unregisterCraftingGridProvider(provider);
    }

}
