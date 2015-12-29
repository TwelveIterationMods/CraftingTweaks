package net.blay09.mods.craftingtweaks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface DefaultProvider {

    RotationHandler getRotationHandler();
    void rotateGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);
    void rotateGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size, RotationHandler rotationHandler);
    void clearGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);
    void clearGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size);
    void balanceGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);
    void balanceGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, int start, int size);
    ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, ItemStack itemStack, int index);
    ItemStack transferIntoGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix, ItemStack itemStack);

}
