package net.blay09.mods.craftingtweaks.api;

import net.minecraft.util.Mth;

public interface CraftingGridBuilder {
    /**
     * @deprecated Use {@link #addGrid(String, int, int, int)} instead.
     */
    @Deprecated
    default CraftingGridDecorator addGrid(int start, int size) {
        return addGrid("default", start, size);
    }

    /**
     * @deprecated Use {@link #addGrid(String, int, int, int)} instead.
     */
    @Deprecated
    default CraftingGridDecorator addGrid(int start, int size, int width, int height) {
        return addGrid("default", start, size, width, height);
    }

    default CraftingGridDecorator addGrid(int start, int width, int height) {
        return addGrid("default", start, width, height);
    }

    /**
     * @deprecated Use {@link #addGrid(String, int, int, int)} instead.
     */
    @Deprecated
    default CraftingGridDecorator addGrid(String name, int start, int size) {
        return addGrid(name, start, size, (int) Mth.sqrt(size), (int) Mth.sqrt(size));
    }

    /**
     * @deprecated Use {@link #addGrid(String, int, int, int)} instead.
     */
    @Deprecated
    CraftingGridDecorator addGrid(String name, int start, int size, int width, int height);

    default CraftingGridDecorator addGrid(String name, int start, int width, int height) {
        return addGrid(name, start, width * height, width, height);
    }

    void addCustomGrid(CraftingGrid grid);
}
