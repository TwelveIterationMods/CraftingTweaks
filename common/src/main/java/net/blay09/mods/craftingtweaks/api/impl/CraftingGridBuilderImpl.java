package net.blay09.mods.craftingtweaks.api.impl;

import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.CraftingGridBuilder;
import net.blay09.mods.craftingtweaks.api.CraftingGridDecorator;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CraftingGridBuilderImpl implements CraftingGridBuilder {

    private final List<CraftingGrid> grids = new ArrayList<>();
    private String activeModId;

    @Override
    public CraftingGridDecorator addGrid(String name, int start, int size) {
        DefaultCraftingGrid grid = new DefaultCraftingGrid(ResourceLocation.fromNamespaceAndPath(activeModId, name), start, size);
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
