package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.CraftingGridBuilder;
import net.blay09.mods.craftingtweaks.api.CraftingGridDecorator;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CraftingGridBuilderImpl implements CraftingGridBuilder {

    private final List<CraftingGrid> grids = new ArrayList<>();
    private String activeModId = "minecraft";

    @Override
    public CraftingGridDecorator addGrid(String name, int start, int width, int height) {
        DefaultCraftingGrid grid = new DefaultCraftingGrid(Identifier.fromNamespaceAndPath(activeModId, name), start, width, height);
        grids.add(grid);
        return grid;
    }

    @Override
    public void addCustomGrid(CraftingGrid grid) {
        grids.add(grid);
    }

    public List<CraftingGrid> getGrids() {
        return grids;
    }

    public void setActiveModId(String activeModId) {
        this.activeModId = activeModId;
    }
}
