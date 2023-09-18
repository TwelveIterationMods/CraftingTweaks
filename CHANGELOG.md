- **Hotfix:** Fixed key modifiers missing translations
- Added support for Tom's Simple Storage
- Added support for Extended Crafting
- Added support for Simple Storage Network
- Added buttonStyle property to API (Java, IMC, Data Pack), allowing mod compat to use smaller or narrower buttons
- Changed buttonOffsetX and buttonOffsetY to now apply to alignToGrid also instead of being ignored when no button positions are set
- Fixed alignToGrid not working correctly with bigger grids

---

- **Hotfix:** Fixed crash on dedicated server
- Added support for data packs specifying crafting grids for compatibility https://github.com/TwelveIterationMods/CraftingTweaks#data-packs
- Added /craftingtweaks debug command which allows for click-dragging over a crafting grid to print the appropriate data pack JSON
- Fixed recipe book button positioning when used with EMI