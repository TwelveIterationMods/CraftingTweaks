package net.blay09.mods.craftingtweaks.provider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public abstract class TweakProvider<T extends Container> {

    public abstract void clearGrid(EntityPlayer entityPlayer, T container);
    public abstract void rotateGrid(EntityPlayer entityPlayer, T container);

}
