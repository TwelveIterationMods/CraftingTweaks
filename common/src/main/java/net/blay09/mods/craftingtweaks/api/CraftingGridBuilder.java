package net.blay09.mods.craftingtweaks.api;

public interface CraftingGridBuilder {
    default CraftingGridDecorator addGrid(int start, int width, int height) {
        return addGrid("default", start, width, height);
    }

    CraftingGridDecorator addGrid(String name, int start, int width, int height);

    void addCustomGrid(CraftingGrid grid);
}
