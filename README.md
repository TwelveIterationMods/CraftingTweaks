# Crafting Tweaks

Minecraft Mod. Allows you to rotate, balance or clear the crafting matrix by the press of a button, in any (supported) crafting window.

See [the license](LICENSE) for modpack permissions etc.

This mod is available for both Forge and Fabric (starting Minecraft 1.17). This is a trial run to see if supporting both platforms is feasible.

#### Forge

[![Versions](http://cf.way2muchnoise.eu/versions/233071_latest.svg)](https://minecraft.curseforge.com/projects/crafting-tweaks) 
[![Downloads](http://cf.way2muchnoise.eu/full_233071_downloads.svg)](https://minecraft.curseforge.com/projects/crafting-tweaks)

#### Fabric

[![Versions](http://cf.way2muchnoise.eu/versions/502516_latest.svg)](https://minecraft.curseforge.com/projects/crafting-tweaks-fabric) 
[![Downloads](http://cf.way2muchnoise.eu/full_502516_downloads.svg)](https://minecraft.curseforge.com/projects/crafting-tweaks-fabric)

## Contributing

If you're interested in contributing to the mod, you can check out [issues labelled as "help wanted"](https://github.com/ModdingForBlockheads/CraftingTweaks/issues?q=is%3Aopen+is%3Aissue+label%3A%22help+wanted%22). These should be ready to be implemented as they are.

If you need help, feel free to join us on [Discord](https://discord.gg/scGAfXC).

## IMC API (Forge)
Most crafting grids can be registered using the IMC API.

In order to register your container for Crafting Tweaks, send an IMC message as follows:

```java
InterModComms.sendTo("craftingtweaks", "RegisterProvider", () -> {
    CompoundNBT tagCompound = new CompoundNBT();
    tagCompound.putString("ContainerClass", YourCraftingContainer.class.getName());

    // tagCompound.putInteger("ValidContainerPredicate", YourContainerPredicate.class.getName());
    // tagCompound.putInteger("GetGridStartFunction", YourGridStartFunction.class.getName());

    // tagCompound.putInteger("GridSlotNumber", 1);
    // tagCompound.putInteger("GridSize", 9);
    // tagCompound.putBoolean("HideButtons", false);
    // tagCompound.putBoolean("PhantomItems", false);

    // tagCompound.putInteger("ButtonOffsetX", -16); // ignored if AlignToGrid is set
    // tagCompound.putInteger("ButtonOffsetY", 16); // ignored if AlignToGrid is set
    // ***** OR *****
    // tagCompound.putString("AlignToGrid", "left");

    // CompoundNBT tweakRotate = new CompoundNBT();
    // tweakRotate.putBoolean("Enabled", true);
    // tweakRotate.putBoolean("ShowButton", true);
    // tweakRotate.putInteger("ButtonX", 0); // ignored if AlignToGrid is set
    // tweakRotate.putInteger("ButtonY", 18); // ignored if AlignToGrid is set
    // tagCompound.put("TweakRotate", tweakRotate);
    // [...] (same structure for "TweakBalance" and "TweakClear")
    
    return tagCompound;
});
```

The commented out lines are optional (the example above shows the default value).

The fields are described below:
* **ContainerClass**: The full class name (including package name) of your container class with the crafting grid.
* **ContainerValidPredicate**: (V3 or higher) The full class name (including package name) of an optional container callback; a class that implements Function<Container, Boolean> to determine whether a container is a valid crafting container
* **GetGridStartFunction**: (V3 or higher) The full class name (including package name) of an optional slot callback; a class t hat implements Function<Container, Integer> to determine the first slot of a crafting grid (overrides GridSlotNumber)
* **GridSlotNumber**: The slotNumber of the first slot in the crafting matrix (this is the index within Container.inventorySlots, NOT the index within the IInventory)
* **GridSize**: The size of the crafting grid (probably 9)
* **HideButtons**: If you don't want Crafting Tweak's buttons to show up (but you want the hotkeys to work), set this to true
* **PhantomItems**: If your crafting grid contains phantom items, set this to true. This will make the clear operation delete the grid instead of putting the items into the player inventory.
* **ButtonOffsetX**: X-Offset to apply to all tweak buttons, relative to the upper left corner of the GuiContainer. AlignToGrid will override this option.
* **ButtonOffsetY**: Y-Offset to apply to all tweak buttons, relative to the upper left corner of the GuiContainer. AlignToGrid will override this option.
* **AlignToGrid**: Can be "up", "down", "left" or "right". Will automatically position buttons next to the grid.
* **TweakRotate**: A tag compound containing settings for the rotate tweak (see below)
* **TweakBalance**: A tag compound containing settings for the balance tweak (see below)
* **TweakClear**: A tag compound containing settings for the clear tweak (see below)
* **Tweak...**: Contains the following settings for tweaks:
  * **Enabled**: Set this to false if this tweak should be disabled for this container
  * **ShowButton**: Set this to false if the button for this tweak should not be added
  * **ButtonX**: X-Position of the tweak button relative to ButtonOffsetX. AlignToGrid will override this option.
  * **ButtonY**: Y-Position of the tweak button relative to ButtonOffsetY. AlignToGrid will override this option.

*Note*: If you're specifying custom button positions, they should be 18 pixels apart from each other. If you simply want the buttons next to the crafting grid, use AlignToGrid. If you just want to move all buttons at once, use ButtonOffsetX/Y. The buttons are set out vertically by default.

## API

If your crafting grid is more complex or doesn't follow Vanilla standards, you may need to supply a custom tweak provider. In that case, follow these steps.
The easiest way to add Crafting Tweaks to your development environment is to do some additions to your build.gradle file. First, register CurseForge's maven repository by adding the following lines:

```
repositories {
    maven { url "https://www.cursemaven.com" }
}
```

Then, add a dependency to Crafting Tweaks:

```
dependencies {
    compile 'curse.maven:craftingtweaks-233071:3330406'
}
```

Make sure you enter the correct file id as version for the Minecraft version you're developing for. Check the CurseForge files page to see available files.

Done! Run gradle to update your project and you'll be good to go.
