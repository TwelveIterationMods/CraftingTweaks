package net.blay09.mods.craftingtweaks.addon.forestry;

import net.blay09.mods.craftingtweaks.CraftingTweaks;

public class ForestryAddon {

    public ForestryAddon() {
        try {
            Class.forName("forestry.factory.tiles.TileWorktable");
            CraftingTweaks.instance.registerProvider("forestry.factory.gui.ContainerWorktable", new ForestryWorktableTweakProvider());
            CraftingTweaks.instance.registerProvider("forestry.factory.gui.ContainerCarpenter", new ForestryCarpenterTweakProvider());
            CraftingTweaks.instance.registerProvider("forestry.factory.gui.ContainerFabricator", new ForestryFabricatorTweakProvider());
        } catch (ClassNotFoundException e) {
            CraftingTweaks.logger.info("Forestry is outdated, using legacy Crafting Tweaks support...");
            CraftingTweaks.instance.registerProvider("forestry.factory.gui.ContainerWorktable", new ForestryWorktableOldTweakProvider());
            CraftingTweaks.instance.registerProvider("forestry.factory.gui.ContainerCarpenter", new ForestryCarpenterOldTweakProvider());
            CraftingTweaks.instance.registerProvider("forestry.factory.gui.ContainerFabricator", new ForestryFabricatorOldTweakProvider());
        }
    }

}
