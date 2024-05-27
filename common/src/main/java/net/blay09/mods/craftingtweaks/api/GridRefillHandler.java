package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Optional;

public interface GridRefillHandler<TMenu extends AbstractContainerMenu> {

    /**
     * Returns the last crafted recipe on this grid.
     *
     * @param player the player this is in regard to
     * @param menu   the menu the grid is part of
     */
    default Optional<RecipeHolder<?>> getLastCrafted(CraftingGrid grid, Player player, TMenu menu) {
        return CraftingTweaksAPI.getLastCraftedRecipe(player);
    }

    /**
     * Refills the grid with the last crafted recipe.
     *
     * @param player the player this is in regard to
     * @param menu   the menu the grid is part of
     * @param stack  if true, the grid will be filled to produce a stack of the last crafted recipe
     */
    default void refillLastCrafted(CraftingGrid grid, Player player, TMenu menu, boolean stack) {
        final var lastCrafted = getLastCrafted(grid, player, menu);
        lastCrafted.ifPresent(recipeHolder -> refillRecipe(grid, player, menu, recipeHolder, stack));
    }

    /**
     * Refills the grid with the given recipe.
     *
     * @param player the player this is in regard to
     * @param menu   the menu the grid is part of
     * @param recipe the recipe to refill the grid with
     * @param stack  if true, the grid will be filled to produce a stack of the given recipe
     */
    void refillRecipe(CraftingGrid grid, Player player, TMenu menu, RecipeHolder<?> recipe, boolean stack);
}
