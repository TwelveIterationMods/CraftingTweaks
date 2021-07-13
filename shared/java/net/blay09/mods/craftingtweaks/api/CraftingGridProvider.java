package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface CraftingGridProvider {
    String getModId();

    boolean handles(AbstractContainerMenu menu);

    void buildCraftingGrids(CraftingGridBuilder builder, AbstractContainerMenu menu);

    default void onInitialize() {}

    default boolean requiresServerSide() {
        return false;
    }
}
