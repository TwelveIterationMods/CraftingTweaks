- Revamped loading of CraftingTweaks compat files
  - Added support for loading CraftingTweaks compat files from `config/craftingtweaks/grids`
  - Added support for loading CraftingTweaks compat files from within mod jars under `craftingtweaks/grids` folder
  - Deprecated support for loading from data packs, as data packs do not work well in scenarios where the mod is present on the client only

Data packs containing CraftingTweaks compat files will continue to work. However, to ensure they are loaded when 
playing in multiplayer as well, the following migrations are recommended:

- Modpack makers using data packs for CraftingTweaks should move their CraftingTweaks data pack files into the `config/craftingtweaks/grids` folder instead
- Mod makers shipping compat files for CraftingTweaks should move their compat files out of the data pack and into a `craftingtweaks/grids` folder at the root of the resources instead
- Users using data packs for CraftingTweaks should move their CraftingTweaks data pack files into the `config/craftingtweaks/grids` folder