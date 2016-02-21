package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.addon.GanysDualWorktableTweakProvider;
import net.blay09.mods.craftingtweaks.addon.TerraFirmaCraftTweakProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.EnumFacing;

public class Compatiblity {

    public static void vanilla() {
        SimpleTweakProvider provider = CraftingTweaksAPI.registerSimpleProvider("minecraft", ContainerWorkbench.class);
        provider.setTweakRotate(true, true, 0, 0);
        provider.setTweakBalance(true, true, 0, 0);
        provider.setTweakClear(true, true, 0, 0);
        provider.setAlignToGrid(EnumFacing.WEST);
    }

    public static void twilightforest() {
        SimpleTweakProvider provider = registerSimpleProvider("TwilightForest", "twilightforest.uncrafting.ContainerTFUncrafting");
        if (provider != null) {
            provider.setGrid(2, 9);
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void tinkersconstruct() {
        SimpleTweakProvider provider = registerSimpleProvider("TConstruct", "tconstruct.tools.inventory.CraftingStationContainer");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void thermalexpansion() {
        SimpleTweakProvider provider = registerSimpleProvider("ThermalExpansion", "cofh.thermalexpansion.gui.container.device.ContainerWorkbench");
        if (provider != null) {
            provider.setGrid(57, 9);
            provider.setTweakRotate(true, false, 0, 0);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, false, 0, 0);
            provider.setPhantomItems(true);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void thaumcraft4() {
        SimpleTweakProvider provider = registerSimpleProvider("Thaumcraft", "thaumcraft.common.container.ContainerArcaneWorkbench");
        if (provider != null) {
            provider.setGrid(2, 9);
            provider.setTweakRotate(true, true, -16, 16);
            provider.setTweakBalance(true, true, -16, 34);
            provider.setTweakClear(true, true, -16, 52);
        }
    }

    public static void rotarycraft() {
        SimpleTweakProvider provider = registerSimpleProvider("RotaryCraft", "Reika.RotaryCraft.Containers.ContainerHandCraft");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
        provider = registerSimpleProvider("RotaryCraft", "Reika.RotaryCraft.Containers.ContainerCraftingPattern");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void natura() {
        SimpleTweakProvider provider = registerSimpleProvider("Natura", "mods.natura.gui.WorkbenchContainer");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void minefactoryreloaded() {
        SimpleTweakProvider provider = registerSimpleProvider("MineFactoryReloaded", "powercrystals.minefactoryreloaded.gui.container.ContainerLiquiCrafter");
        if (provider != null) {
            provider.setGrid(0, 9);
            provider.setTweakRotate(true, false, 0, 0);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, false, 0, 0);
            provider.setPhantomItems(true);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void jacb() {
        SimpleTweakProvider provider = registerSimpleProvider("jacb", "tv.vanhal.jacb.gui.BenchContainer");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void draconicevolution() {
        SimpleTweakProvider provider = registerSimpleProvider("DraconicEvolution", "com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest");
        if (provider != null) {
            provider.setGrid(234, 9);
            provider.setTweakRotate(true, false, 0, 0);
            provider.setTweakBalance(true, false, 0, 0);
            provider.setTweakClear(true, false, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void buildcraft() {
        SimpleTweakProvider provider = registerSimpleProvider("BuildCraft|Factory", "buildcraft.factory.gui.ContainerAutoWorkbench");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void bluepower() {
        SimpleTweakProvider provider = registerSimpleProvider("bluepower", "com.bluepowermod.container.ContainerProjectTable");
        if (provider != null) {
            provider.setGrid(0, 9);
            provider.setTweakRotate(true, true, 16, 34);
            provider.setTweakBalance(true, true, 16, 52);
            provider.setTweakClear(true, false, 0, 0);
        }
    }

    public static void bibliocraft() {
        SimpleTweakProvider provider = registerSimpleProvider("BiblioCraft", "jds.bibliocraft.blocks.ContainerFancyWorkbench");
        if (provider != null) {
            provider.setGrid(10, 9);
            provider.setTweakRotate(true, true, -16, 16);
            provider.setTweakBalance(true, true, -16, 34);
            provider.setTweakClear(true, true, -16, 52);
        }
    }

    public static void backpacks() {
        SimpleTweakProvider provider = registerSimpleProvider("BiblioCraft", "de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, false, 0, 0);
            provider.setPhantomItems(true);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void railcraft() {
        SimpleTweakProvider provider = registerSimpleProvider("Railcraft", "mods.railcraft.common.gui.containers.ContainerCartWork");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
        provider = registerSimpleProvider("Railcraft", "mods.railcraft.common.gui.containers.ContainerRollingMachine");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void ganyssurface() {
        SimpleTweakProvider provider = registerSimpleProvider("ganyssurface", "ganymedes01.ganyssurface.inventory.ContainerWorkTable");
        if (provider != null) {
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
        registerProvider("ganymedes01.ganyssurface.inventory.ContainerDualWorkTable", new GanysDualWorktableTweakProvider());
    }

    public static void appliedenergistics2() {
        SimpleTweakProvider provider = registerSimpleProvider("appliedenergistics2", "appeng.container.implementations.ContainerCraftingTerm");
        if (provider != null) {
            provider.setGrid(41, 9);
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
        provider = registerSimpleProvider("appliedenergistics2", "appeng.container.implementations.ContainerPatternTerm");
        if (provider != null) {
            provider.setGrid(41, 9);
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setPhantomItems(true);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void avaritia() {
        SimpleTweakProvider provider = registerSimpleProvider("Avaritia", "fox.spiteful.avaritia.gui.ContainerExtremeCrafting");
        if (provider != null) {
            provider.setGrid(1, 81);
            provider.setTweakRotate(false, false, 0, 0);
            provider.setTweakBalance(true, true, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void forestry() {
        SimpleTweakProvider provider = registerSimpleProvider("Forestry", "forestry.core.gui.ContainerTile");
        if (provider != null) {
            provider.setGrid(0, 9);
            provider.setTweakRotate(true, true, 0, 0);
            provider.setTweakBalance(false, false, 0, 0);
            provider.setTweakClear(true, true, 0, 0);
            provider.setPhantomItems(true);
            provider.setAlignToGrid(EnumFacing.WEST);
        }
    }

    public static void terrafirmacraft() {
        registerProvider("com.bioxx.tfc.Containers.ContainerPlayerTFC", new TerraFirmaCraftTweakProvider());
    }

    @SuppressWarnings("unchecked")
    private static SimpleTweakProvider registerSimpleProvider(String modid, String className) {
        try {
            return CraftingTweaksAPI.registerSimpleProvider(modid, (Class<? extends Container>) Class.forName(className));
        } catch (ClassNotFoundException e) {
            System.err.println("Could not register Crafting Tweaks addon for " + modid + " - internal names have changed.");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void registerProvider(String className, TweakProvider provider) {
        try {
            CraftingTweaksAPI.registerProvider((Class<? extends Container>) Class.forName(className), provider);
        } catch (ClassNotFoundException e) {
            System.err.println("Could not register Crafting Tweaks addon for " + provider.getModId() + " - internal names have changed.");
        }
    }
}
