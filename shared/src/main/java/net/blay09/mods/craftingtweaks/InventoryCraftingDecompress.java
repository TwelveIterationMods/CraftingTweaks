package net.blay09.mods.craftingtweaks;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class InventoryCraftingDecompress extends TransientCraftingContainer implements RecipeCraftingHolder {

    private RecipeHolder<?> recipeUsed;

    public InventoryCraftingDecompress(AbstractContainerMenu menu, ItemStack itemStack) {
        super(menu, 3, 3);
        setItem(0, itemStack.copy());
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        this.recipeUsed = recipe;
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return recipeUsed;
    }
}
