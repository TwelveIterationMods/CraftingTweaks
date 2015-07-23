package net.blay09.mods.craftingtweaks.provider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public abstract class TweakProvider {

    public abstract void clearGrid(EntityPlayer entityPlayer, Container container);
    public abstract void rotateGrid(EntityPlayer entityPlayer, Container container);

}
