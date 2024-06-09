package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RefillLastCraftedMessage implements CustomPacketPayload {

    public static Type<RefillLastCraftedMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "refill_last_crafted"));

    private final ResourceLocation id;
    private final boolean stack;

    public RefillLastCraftedMessage(ResourceLocation id, boolean stack) {
        this.id = id;
        this.stack = stack;
    }

    public static RefillLastCraftedMessage decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        boolean force = buf.readBoolean();
        return new RefillLastCraftedMessage(id, force);
    }

    public static void encode(FriendlyByteBuf buf, RefillLastCraftedMessage message) {
        buf.writeResourceLocation(message.id);
        buf.writeBoolean(message.stack);
    }

    public static void handle(ServerPlayer player, RefillLastCraftedMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu != null) {
            CraftingTweaksProviderManager.getCraftingGrid(menu, message.id).ifPresent(grid -> {
                grid.refillHandler().refillLastCrafted(grid, player, menu, message.stack);
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
