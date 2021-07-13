package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.api.GridGuiHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.HashMap;
import java.util.Map;

public class CraftingTweaksClientProviderManager {

    private static final Map<Class<?>, GridGuiHandler> gridGuiHandlers = new HashMap<>();

    public static <TScreen extends AbstractContainerScreen<TMenu>, TMenu extends AbstractContainerMenu> void registerCraftingGridGuiHandler(Class<TScreen> clazz, GridGuiHandler handler) {
        gridGuiHandlers.put(clazz, handler);
    }

    public static GridGuiHandler getGridGuiHandler(AbstractContainerScreen<?> screen) {
        return gridGuiHandlers.get(screen.getClass());
    }
}
