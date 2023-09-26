package net.blay09.mods.craftingtweaks;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class InventoryCraftingCompress extends TransientCraftingContainer implements RecipeCraftingHolder {

    private RecipeHolder<?> recipeUsed;

    public InventoryCraftingCompress(AbstractContainerMenu menu, int size, ItemStack itemStack) {
        super(menu, size, size);
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, itemStack.copy());
        }
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
