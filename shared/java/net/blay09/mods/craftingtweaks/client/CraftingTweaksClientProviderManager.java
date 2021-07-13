package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.api.GridGuiHandler;
import net.blay09.mods.craftingtweaks.api.impl.DefaultGridGuiHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.HashMap;
import java.util.Map;

public class CraftingTweaksClientProviderManager {

    private static final Map<Class<?>, GridGuiHandler> gridGuiHandlers = new HashMap<>();
    private static final DefaultGridGuiHandler defaultGridGuiHandler = new DefaultGridGuiHandler();

    public static <TScreen extends AbstractContainerScreen<TMenu>, TMenu extends AbstractContainerMenu> void registerCraftingGridGuiHandler(Class<TScreen> clazz, GridGuiHandler handler) {
        gridGuiHandlers.put(clazz, handler);
    }

    public static GridGuiHandler getGridGuiHandler(AbstractContainerScreen<?> screen) {
        GridGuiHandler exactHandler = gridGuiHandlers.get(screen.getClass());
        if (exactHandler != null) {
            return exactHandler;
        }

        for (Map.Entry<Class<?>, GridGuiHandler> entry : gridGuiHandlers.entrySet()) {
            if(entry.getKey().isAssignableFrom(screen.getClass())) {
                return entry.getValue();
            }
        }

        return defaultGridGuiHandler;
    }
}
