package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.inventory.AbstractContainerMenu;

import java.lang.reflect.InvocationTargetException;

public class CraftingTweaksDefaultHandlers {

    private static final InternalMethods internalMethods = loadInternalMethods();

    private static InternalMethods loadInternalMethods() {
        try {
            return (InternalMethods) Class.forName("net.blay09.mods.craftingtweaks.api.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            return null;
        }
    }

    public static GridTransferHandler<AbstractContainerMenu> defaultTransferHandler() {
        return internalMethods.defaultTransferHandler();
    }

    public static GridBalanceHandler<AbstractContainerMenu> defaultBalanceHandler() {
        return internalMethods.defaultBalanceHandler();
    }

    public static GridClearHandler<AbstractContainerMenu> defaultClearHandler() {
        return internalMethods.defaultClearHandler();
    }

    public static GridRotateHandler<AbstractContainerMenu> defaultRotateHandler() {
        return internalMethods.defaultRotateHandler();
    }

    public static GridRotateHandler<AbstractContainerMenu> defaultFourByFourRotateHandler() {
        return internalMethods.defaultFourByFourRotateHandler();
    }
}
