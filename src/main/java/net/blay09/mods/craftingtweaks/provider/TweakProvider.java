package net.blay09.mods.craftingtweaks.provider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public interface TweakProvider {

    void clearGrid(EntityPlayer entityPlayer, Container container);
    void rotateGrid(EntityPlayer entityPlayer, Container container);
    void balanceGrid(EntityPlayer entityPlayer, Container container);

}
