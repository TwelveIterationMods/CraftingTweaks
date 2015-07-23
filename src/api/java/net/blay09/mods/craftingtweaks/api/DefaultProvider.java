package net.blay09.mods.craftingtweaks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public interface DefaultProvider {

    void rotateGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);
    void clearGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);
    void balanceGrid(EntityPlayer entityPlayer, Container container, IInventory craftMatrix);

}
