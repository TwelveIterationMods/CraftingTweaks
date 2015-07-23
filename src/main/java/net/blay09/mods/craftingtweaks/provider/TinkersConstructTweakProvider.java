package net.blay09.mods.craftingtweaks.provider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

import java.lang.reflect.Field;

public class TinkersConstructTweakProvider extends DefaultProvider {

    private Field craftMatrixField;

    public TinkersConstructTweakProvider() {
        try {
            Class clazz = Class.forName("tconstruct.tools.inventory.CraftingStationContainer");
            craftMatrixField = clazz.getField("craftMatrix");
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchFieldException ignored) {}
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container) {
        try {
            IInventory craftMatrix = (IInventory) craftMatrixField.get(container);
            clearGridDefault(entityPlayer, container, craftMatrix);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container) {
        try {
            IInventory craftMatrix = (IInventory) craftMatrixField.get(container);
            rotateGridDefault(entityPlayer, container, craftMatrix);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
