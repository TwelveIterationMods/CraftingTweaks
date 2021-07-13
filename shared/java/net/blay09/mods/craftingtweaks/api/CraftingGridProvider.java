package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface CraftingGridProvider {
    String getModId();

    void onInitialize();

    default boolean requiresServerSide() {
        return true;
    } // TODO

    void buildCraftingGrids(CraftingGridBuilder builder, AbstractContainerMenu menu);
}
