package net.blay09.mods.craftingtweaks.api;

public interface CraftingGridBuilder {
    default CraftingGridDecorator addGrid(int start, int size) {
        return addGrid("default", start, size);
    }

    CraftingGridDecorator addGrid(String name, int start, int size);
    void addCustomGrid(CraftingGrid grid);
}
