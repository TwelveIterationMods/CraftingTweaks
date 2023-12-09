# Crafting Tweaks

Minecraft Mod. Allows you to rotate, balance or clear the crafting matrix by the press of a button, in any (supported)
crafting window.

- [Modpack Permissions](https://mods.twelveiterations.com/permissions)

#### Forge

[![Versions](http://cf.way2muchnoise.eu/versions/233071_latest.svg)](https://www.curseforge.com/minecraft/mc-mods/crafting-tweaks)
[![Downloads](http://cf.way2muchnoise.eu/full_233071_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/crafting-tweaks)

#### Fabric

[![Versions](http://cf.way2muchnoise.eu/versions/502516_latest.svg)](https://www.curseforge.com/minecraft/mc-mods/crafting-tweaks-fabric)
[![Downloads](http://cf.way2muchnoise.eu/full_502516_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/crafting-tweaks-fabric)

## Contributing

If you're interested in contributing to the mod, you can check
out [issues labelled as "help wanted"](https://github.com/TwelveIterationMods/CraftingTweaks/issues?q=is%3Aopen+is%3Aissue+label%3A%22help+wanted%22).

When it comes to new features, it's best to confer with me first to ensure we share the same vision. You can join us
on [Discord](https://discord.gg/VAfZ2Nau6j) if you'd like to talk.

Contributions must be done through pull requests. I will not be able to accept translations, code or other assets
through any other channels.

## Data Packs

Most crafting grids can be registered using data packs. This works on both Fabric and Forge.

Crafting Tweaks scans for JSON files inside `craftingtweaks_compat` within a data pack.
This can be either an isolated datapack in the datapacks folder, or an embedded datapack in a mod jar (i.e. your `data`
folder).

A simple example (in this case, for the Vanilla crafting table) would be:

```json
{
  "modid": "minecraft",
  "containerClass": "net.minecraft.world.inventory.CraftingMenu",
  "gridSlotNumber": 1,
  "gridSize": 9
}
```

```json5
{
  "modid": "yourmodid",
  "silent": false, // currently not used, but it's mostly intended for CraftingTweaks' own embedded datapack
  "containerClass": "net.minecraft.world.inventory.CraftingMenu", // the full name incl. package of your menu class
  "containerCallbackClass": "", // legacy field for backwards compatibility, ignore me
  "validContainerPredicateClass": "", // full name of a predicate class, see IMC API below for a description
  "getGridStartFunctionClass": "", //  full name of an int function, see IMC API below for a description
  "gridSlotNumber": 1, // the first slot in your crafting grid
  "gridSize": 9, // the total size of your crafting grid
  "alignToGrid": "left", // can be "up", "down", "left" or "right". Will automatically position buttons next to the grid.
  "hideButtons": false, // allows you to hide all buttons and make only the keybinds work for your grid 
  "phantomItems": false, // set to true if your grid has fake items to prevent item dupes
  // "buttonOffsetX": 0, // allows specifying a manual offset for the tweak buttons, but I recommend alignToGrid instead
  // "buttonOffsetY": 0, // allows specifying a manual offset for the tweak buttons, but I recommend alignToGrid instead
  "tweakRotate": { // you can also manage some options in each individual tweak
    "enabled": true,
    "showButton": true
    // "buttonX": 0, // allows specifying a manual offset for the tweak buttons, but I recommend alignToGrid instead
    // "buttonY": 0, // allows specifying a manual offset for the tweak buttons, but I recommend alignToGrid instead
  },
  "tweakBalance": {
    "enabled": true,
    "showButton": true
    // "buttonX": 0, // allows specifying a manual offset for the tweak buttons, but I recommend alignToGrid instead
    // "buttonY": 0, // allows specifying a manual offset for the tweak buttons, but I recommend alignToGrid instead
  },
  "tweakClear": {
    "enabled": true,
    "showButton": true
    // "buttonX": 0, // allows specifying a manual offset for the tweak buttons, but I recommend alignToGrid instead
    // "buttonY": 0, // allows specifying a manual offset for the tweak buttons, but I recommend alignToGrid instead
  }
}
```

## IMC API (Forge)

Most crafting grids can be registered using the IMC API.

In order to register your container for Crafting Tweaks, send an IMC message as follows:

```java
InterModComms.sendTo("craftingtweaks","RegisterProvider",()->{
        CompoundNBT tagCompound=new CompoundNBT();
        tagCompound.putString("ContainerClass",YourCraftingContainer.class.getName());

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
* **ContainerValidPredicate**: (V3 or higher) The full class name (including package name) of an optional container
  callback; a class that implements Function<Container, Boolean> to determine whether a container is a valid crafting
  container
* **GetGridStartFunction**: (V3 or higher) The full class name (including package name) of an optional slot callback; a
  class that implements Function<Container, Integer> to determine the first slot of a crafting grid (overrides
  GridSlotNumber)
* **GridSlotNumber**: The slotNumber of the first slot in the crafting matrix (this is the index within
  Container.inventorySlots, NOT the index within the IInventory)
* **GridSize**: The size of the crafting grid (probably 9)
* **HideButtons**: If you don't want Crafting Tweak's buttons to show up (but you want the hotkeys to work), set this to
  true
* **PhantomItems**: If your crafting grid contains phantom items, set this to true. This will make the clear operation
  delete the grid instead of putting the items into the player inventory.
* **ButtonOffsetX**: X-Offset to apply to all tweak buttons, relative to the upper left corner of the GuiContainer.
  AlignToGrid will override this option.
* **ButtonOffsetY**: Y-Offset to apply to all tweak buttons, relative to the upper left corner of the GuiContainer.
  AlignToGrid will override this option.
* **AlignToGrid**: Can be "up", "down", "left" or "right". Will automatically position buttons next to the grid.
* **TweakRotate**: A tag compound containing settings for the rotate tweak (see below)
* **TweakBalance**: A tag compound containing settings for the balance tweak (see below)
* **TweakClear**: A tag compound containing settings for the clear tweak (see below)
* **Tweak...**: Contains the following settings for tweaks:
    * **Enabled**: Set this to false if this tweak should be disabled for this container
    * **ShowButton**: Set this to false if the button for this tweak should not be added
    * **ButtonX**: X-Position of the tweak button relative to ButtonOffsetX. AlignToGrid will override this option.
    * **ButtonY**: Y-Position of the tweak button relative to ButtonOffsetY. AlignToGrid will override this option.

*Note*: If you're specifying custom button positions, they should be 18 pixels apart from each other. If you simply want
the buttons next to the crafting grid, use AlignToGrid. If you just want to move all buttons at once, use
ButtonOffsetX/Y. The buttons are set out vertically by default.

## Java API (Forge & Fabric)

If your crafting grid is more complex or doesn't follow Vanilla standards, you may need to supply a custom grid provider
using the Java API.

## Adding the mod to a development environment

Note that you will also need to add Balm if you want to test your integration in your environment.

### Using CurseMaven

Add the following to your `build.gradle`:

```groovy
repositories {
    maven { url "https://www.cursemaven.com" }
}

dependencies {
    // Replace ${craftingtweaks_file_id} and ${balm_file_id} with the id of the file you want to depend on.
    // You can find it in the URL of the file on CurseForge (e.g. 3914527).
    // Forge: implementation fg.deobf("curse.maven:balm-531761:${balm_file_id}")
    // Fabric: modImplementation "curse.maven:balm-fabric-500525:${balm_file_id}"

    // Forge: implementation fg.deobf("curse.maven:crafting-tweaks-233071:${craftingtweaks_file_id}")
    // Fabric: modImplementation "curse.maven:crafting-tweaks-fabric-502516:${craftingtweaks_file_id}"
}
```

### Using Twelve Iterations Maven (includes snapshot and mojmap versions)

Add the following to your `build.gradle`:

```groovy
repositories {
    maven {
        url "https://maven.twelveiterations.com/repository/maven-public/"

        content {
            includeGroup "net.blay09.mods"
        }
    }
}

dependencies {
    // Replace ${craftingtweaks_version} and ${balm_version} with the version you want to depend on. 
    // You can find the latest version for a given Minecraft version at https://maven.twelveiterations.com/service/rest/repository/browse/maven-public/net/blay09/mods/balm-common/ and https://maven.twelveiterations.com/service/rest/repository/browse/maven-public/net/blay09/mods/craftingtweaks-common/
    // Common (mojmap): implementation "net.blay09.mods:balm-common:${balm_version}"
    // Forge: implementation fg.deobf("net.blay09.mods:balm-forge:${balm_version}")
    // Fabric: modImplementation "net.blay09.mods:balm-fabric:${balm_version}"

    // Common (mojmap): implementation "net.blay09.mods:craftingtweaks-common:${craftingtweaks_version}"
    // Forge: implementation fg.deobf("net.blay09.mods:craftingtweaks-forge:${craftingtweaks_version}")
    // Fabric: modImplementation "net.blay09.mods:craftingtweaks-fabric:${craftingtweaks_version}"
}
```

### Registering a Crafting Grid using the Java API

The entry point for your plugin is going to be a class implementing `CraftingGridProvider`.

```java
import net.blay09.mods.craftingtweaks.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class YourCustomCraftingGridProvider implements CraftingGridProvider {

    public YourCustomCraftingGridProvider() {
        CraftingTweaksAPI.registerCraftingGridProvider(this);
    }

    @Override
    public String getModId() {
        return "yourModId";
    }

    @Override
    public boolean requiresServerSide() {
        return false; // should be true if you use things such as phantom items, where simulating clicks would now result in desired behaviour
    }

    @Override
    public boolean handles(AbstractContainerMenu menu) {
        return menu instanceof YourCustomCraftingMenu; // buildCraftingGrids will only be called if this returns true for the given menu
    }

    @Override
    public void buildCraftingGrids(CraftingGridBuilder builder, AbstractContainerMenu menu) {
        // this is where you define crafting grids
        if (menu instanceof YourCustomCraftingMenu) {
            builder.addGrid(1, 9); // you can register a simple default grid like this

            builder.addGrid("secondary_grid",
                    1,
                    9); // you can also specify names (default: "default") if you have multiple grids in one screen

            builder.addGrid(1, 9) // you can also configure the default grid further
                    .disableTweak(TweakType.Rotate) // e.g. by disabling certain tweaks
                    .disableAllTweaks() // or by disabling all tweaks, if you hate Crafting Tweaks
                    .hideTweakButton(TweakType.Clear) // or by hiding a certain tweak button 
                    .hideAllTweakButtons() // or by hiding all tweak buttons
                    .usePhantomItems() // or by marking this grid to contain phantom items, which ensures that Crafting Tweaks doesn't perceive these items as real items
                    .setButtonAlignment(ButtonAlignment.TOP) // or by changing the alignment of the buttons, e.g. having them be at the top of the grid instead of at the left
                    .setButtonPosition(TweakType.Balance,
                            10,
                            10) // or by overriding the position of the buttons directly, ignoring alignment
                    .rotateHandler(/*...*/) // or by overriding the rotation handling
                    .balanceHandler(/*...*/) // or by overriding the balance handling
                    .clearHandler(/*...*/) // or by overriding the clear handling
                    .transferHandler(/*...*/); // or by overriding the transfer handling

            builder.addCustomGrid(new CraftingGrid() { // you can also add completely custom crafting grids with your own implementations
                // ...
            });
        }
    }
}
```

Then instantiate that provider if Crafting Tweaks is loaded:

```java
public class YourMod {
    private void setup() {
        // ...
        if (isModLoaded("craftingtweaks")) { // different calls depending on whether you use Forge or Fabric
            try {
                Class.forName("your.mod.compat.craftingtweaks.YourCustomCraftingGridProvider")
                        .getConstructor()
                        .newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        // ...
    }
}
```

### Registering a GUI handler for custom tweak button placement

You shouldn't really need to do this, but in case you want to handle the button placement in your screen yourself, you
can define a GUI handler for it:

```java
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksClientAPI;
import net.blay09.mods.craftingtweaks.api.GridGuiHandler;
import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.function.Consumer;

public class YourCustomGridGuiHandler implements GridGuiHandler {
    public YourCustomGridGuiHandler() {
        CraftingTweaksClientAPI.registerCraftingGridGuiHandler(this);
    }

    @Override
    public void createButtons(AbstractContainerScreen<?> screen, CraftingGrid grid, Consumer<AbstractWidget> addWidgetFunc) {
        // CraftingTweaksClientAPI.createTweakButton(/* ... */); ...
    }
}
```

Remember to instantiate the handler (so that it gets registered) as done in the previous section.
