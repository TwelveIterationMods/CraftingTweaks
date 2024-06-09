package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class CompressMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<CompressMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("craftingtweaks", "compress"));

    private final int slotNumber;
    private final CompressType type;

    public CompressMessage(int slotNumber, CompressType type) {
        this.slotNumber = slotNumber;
        this.type = type;
    }

    public static CompressMessage decode(FriendlyByteBuf buf) {
        int slotNumber = buf.readInt();
        CompressType type = CompressType.values()[buf.readByte()];
        return new CompressMessage(slotNumber, type);
    }

    public static void encode(FriendlyByteBuf buf, CompressMessage message) {
        buf.writeInt(message.slotNumber);
        buf.writeByte(message.type.ordinal());
    }

    public static void handle(ServerPlayer player, CompressMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) {
            return;
        }

        CompressType compressType = message.type;
        Slot mouseSlot = menu.slots.get(message.slotNumber);
        if (!(mouseSlot.container instanceof Inventory)) {
            return;
        }

        ItemStack mouseStack = mouseSlot.getItem();
        if (mouseStack.isEmpty()) {
            return;
        }

        CraftingGrid grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
        boolean compressRequiresCraftingGrid = CraftingTweaksConfig.getActive().common.compressRequiresCraftingGrid;
        if (compressRequiresCraftingGrid && grid == null) {
            return;
        }

        if (compressType == CompressType.DECOMPRESS_ALL || compressType == CompressType.DECOMPRESS_STACK || compressType == CompressType.DECOMPRESS_ONE) {
            boolean decompressAll = compressType != CompressType.DECOMPRESS_ONE;
            // Perform decompression on all valid slots
            for (Slot slot : menu.slots) {
                if (compressType != CompressType.DECOMPRESS_ALL && slot != mouseSlot) {
                    continue;
                }

                ItemStack slotStack = slot.getItem();
                if (slot.container instanceof Inventory && ItemStack.isSameItemSameComponents(slot.getItem(), mouseSlot.getItem())) {
                    final var craftingContainer = new InventoryCraftingDecompress(menu, slotStack);
                    ItemStack result = findMatchingResult(craftingContainer.asCraftInput(), craftingContainer, player);
                    if (!result.isEmpty() && !isBlacklisted(result) && !slotStack.isEmpty() && slotStack.getCount() >= 1) {
                        do {
                            if (player.getInventory().add(result.copy())) {
                                giveLeftoverItems(player, slotStack, 1);
                                slot.remove(1);
                            } else {
                                break;
                            }
                        } while (decompressAll && slot.hasItem() && slotStack.getCount() >= 1 && slotStack.getItem() != result.getItem());
                    }
                }
            }
        } else {
            switch (compressType) {
                case COMPRESS_ONE -> compressMouseSlot(player, menu, mouseSlot, grid, compressRequiresCraftingGrid, false);
                case COMPRESS_STACK -> compressMouseSlot(player, menu, mouseSlot, grid, compressRequiresCraftingGrid, true);
                case COMPRESS_ALL -> compressAll(player, menu, mouseSlot, grid, compressRequiresCraftingGrid);
            }
        }
        menu.broadcastChanges();
    }

    private static void compressMouseSlot(ServerPlayer player, AbstractContainerMenu menu, Slot mouseSlot, CraftingGrid grid, boolean compressRequiresCraftingGrid, boolean wholeStack) {
        int maxGridSize = grid != null && compressRequiresCraftingGrid ? grid.getGridSize(player, menu) : 9;
        ItemStack mouseStack = mouseSlot.getItem();
        CompressionRecipe recipe = findRecipe(menu, player, mouseStack, maxGridSize);
        int recipeSize = recipe.size();
        if (recipeSize > 0) {
            int maxStackSize = recipe.result().getMaxStackSize();
            // Calculate the number of crafts possible
            int craftsPossible = Math.min(mouseStack.getCount() / recipeSize, wholeStack ? maxStackSize : 1);
            if (craftsPossible == 0) {
                return;
            }

            // Delete the source items from the inventory
            int itemsToRemove = craftsPossible * recipeSize;
            giveLeftoverItems(player, mouseStack, itemsToRemove);
            mouseStack.shrink(itemsToRemove);

            // Add crafted items to the inventory
            addCraftedItemsToInventory(player, recipe.result(), craftsPossible);
        }
    }

    private static void compressAll(ServerPlayer player, AbstractContainerMenu menu, Slot mouseSlot, CraftingGrid grid, boolean compressRequiresCraftingGrid) {
        int maxGridSize = grid != null && compressRequiresCraftingGrid ? grid.getGridSize(player, menu) : 9;

        // Count the total number of source items
        ItemStack mouseStack = mouseSlot.getItem().copy();
        int totalItemCount = countTotalItems(menu, mouseStack);

        // Find the recipe and see if it's 2x2 or 3x3
        CompressionRecipe recipe = findRecipe(menu, player, mouseStack, maxGridSize);
        int recipeSize = recipe.size();

        if (recipeSize > 0) {
            // Calculate the number of crafts possible
            int craftsPossible = totalItemCount / recipeSize;

            // Delete the source items from the inventory
            int itemsToRemove = craftsPossible * recipeSize;
            removeSourceItems(player, menu, mouseStack, itemsToRemove);

            // Add crafted items to the inventory
            addCraftedItemsToInventory(player, recipe.result(), craftsPossible);
        }
    }

    private static void giveLeftoverItems(ServerPlayer player, ItemStack slotStack, int count) {
        for (int i = 0; i < count; i++) {
            // Must be inside loop as it's being shrunk in addItemStackToInventory
            final ItemStack containerItem = Balm.getHooks().getCraftingRemainingItem(slotStack);
            if (!player.addItem(containerItem)) {
                ItemEntity itemEntity = player.drop(containerItem, false);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                    itemEntity.setTarget(player.getUUID());
                }
            }
        }
    }

    private static <T extends CraftingInput> ItemStack findMatchingResult(T recipeInput, RecipeCraftingHolder recipeCraftingHolder, ServerPlayer player) {
        RecipeManager recipeManager = Objects.requireNonNull(player.getServer()).getRecipeManager();
        Level level = player.level();
        RecipeHolder<CraftingRecipe> recipe = recipeManager.getRecipeFor(RecipeType.CRAFTING, recipeInput, level).orElse(null);
        if (recipe != null && recipeCraftingHolder.setRecipeUsed(level, player, recipe)) {
            return recipe.value().assemble(recipeInput, level.registryAccess());
        }

        return ItemStack.EMPTY;
    }

    private static boolean isBlacklisted(ItemStack result) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(result.getItem());
        return CraftingTweaksConfig.getActive().common.compressDenylist.contains(registryName.toString());
    }

    private static int countTotalItems(AbstractContainerMenu menu, ItemStack sourceItem) {
        int totalItemCount = 0;
        for (Slot slot : menu.slots) {
            final ItemStack slotStack = slot.getItem();
            if (slot.container instanceof Inventory && ItemStack.isSameItemSameComponents(slot.getItem(), sourceItem)) {
                totalItemCount += slotStack.getCount();
            }
        }
        return totalItemCount;
    }

    record CompressionRecipe(int size, ItemStack result) {
    }

    private static CompressionRecipe findRecipe(AbstractContainerMenu menu, ServerPlayer player, ItemStack exampleStack, int maxGridSize) {
        int recipeSize = 0;
        ItemStack result = ItemStack.EMPTY;

        if (maxGridSize >= 9) {
            InventoryCraftingCompress exampleInventory = new InventoryCraftingCompress(menu, 3, exampleStack);
            result = findMatchingResult(exampleInventory.asCraftInput(), exampleInventory, player);
            if (!result.isEmpty() && !isBlacklisted(result)) {
                recipeSize = 9;
            }
        }

        if (recipeSize == 0 && maxGridSize >= 4) {
            InventoryCraftingCompress exampleInventory = new InventoryCraftingCompress(menu, 2, exampleStack);
            result = findMatchingResult(exampleInventory.asCraftInput(), exampleInventory, player);
            if (!result.isEmpty() && !isBlacklisted(result)) {
                recipeSize = 4;
            }
        }

        return new CompressionRecipe(recipeSize, result);
    }

    private static void removeSourceItems(ServerPlayer player, AbstractContainerMenu menu, ItemStack sourceItem, int itemsToRemove) {
        for (Slot slot : menu.slots) {
            final ItemStack slotStack = slot.getItem();
            if (slot.container instanceof Inventory && ItemStack.isSameItemSameComponents(slot.getItem(), sourceItem)) {
                int removedFromSlot = Math.min(slotStack.getCount(), itemsToRemove);
                giveLeftoverItems(player, slotStack, removedFromSlot);
                slot.remove(removedFromSlot);
                itemsToRemove -= removedFromSlot;

                if (itemsToRemove == 0) {
                    break;
                }
            }
        }
    }

    private static void addCraftedItemsToInventory(ServerPlayer player, ItemStack result, int timesCrafted) {
        int itemsCrafted = timesCrafted * result.getCount();
        while (itemsCrafted > 0) {
            ItemStack craftedStack = result.copy();
            craftedStack.setCount(Math.min(itemsCrafted, result.getMaxStackSize()));
            itemsCrafted -= craftedStack.getCount();
            if (!player.getInventory().add(craftedStack)) {
                // Drop the item on the ground if the inventory is full
                player.drop(craftedStack, true);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
