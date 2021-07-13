package net.blay09.mods.craftingtweaks.api;

public class CraftingTweaksAPI {

    private static InternalMethods internalMethods;

    /**
     * Internal Method. Stay away.
     * @param internalMethods I said stay away.
     */
    public static void setupAPI(InternalMethods internalMethods) {
        CraftingTweaksAPI.internalMethods = internalMethods;
        CraftingTweaksDefaultHandlers.setupAPI(internalMethods);
    }

    public static void registerCraftingGridProvider(CraftingGridProvider provider) {
        internalMethods.registerCraftingGridProvider(provider);
    }

}
