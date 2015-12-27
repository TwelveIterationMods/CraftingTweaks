package net.blay09.mods.craftingtweaks.addon.railcraft;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.addon.forestry.*;

public class RailcraftAddon {

    public RailcraftAddon() {
        try {
            Class.forName("mods.railcraft.common.gui.containers.ContainerCartWork");
            CraftingTweaks.instance.registerProvider("mods.railcraft.common.gui.containers.ContainerRollingMachine", new RailcraftRollingMachineTweakProvider());
            CraftingTweaks.instance.registerProvider("mods.railcraft.common.gui.containers.ContainerCartWork", new RailcraftWorkCartTweakProvider());
        } catch (ClassNotFoundException e) {
            CraftingTweaks.logger.info("Railcraft is outdated, using legacy Crafting Tweaks support...");
            CraftingTweaks.instance.registerProvider("mods.railcraft.common.gui.containers.ContainerRollingMachine", new RailcraftRollingMachineTweakProvider());
            CraftingTweaks.instance.registerProvider("mods.railcraft.common.gui.containers.ContainerWorkCart", new RailcraftWorkCartTweakProviderOld());

        }
    }

}
