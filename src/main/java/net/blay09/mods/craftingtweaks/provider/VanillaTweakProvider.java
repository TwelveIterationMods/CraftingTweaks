package net.blay09.mods.craftingtweaks.provider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;

public class VanillaTweakProvider extends DefaultProvider {

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container) {
        ContainerWorkbench workbench = (ContainerWorkbench) container;
        clearGridDefault(entityPlayer, container, workbench.craftMatrix);
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container) {
        ContainerWorkbench workbench = (ContainerWorkbench) container;
        rotateGridDefault(entityPlayer, container, workbench.craftMatrix);
    }

}
