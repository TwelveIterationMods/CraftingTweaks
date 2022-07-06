package net.blay09.mods.craftingtweaks.api;

import java.lang.reflect.InvocationTargetException;

public class CraftingTweaksAPI {

    private static final InternalMethods internalMethods = loadInternalMethods();

    private static InternalMethods loadInternalMethods() {
        try {
            return (InternalMethods) Class.forName("net.blay09.mods.craftingtweaks.api.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            return null;
        }
    }

    public static void registerCraftingGridProvider(CraftingGridProvider provider) {
        internalMethods.registerCraftingGridProvider(provider);
    }

}
