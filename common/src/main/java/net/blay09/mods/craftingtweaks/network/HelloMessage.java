package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class HelloMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<HelloMessage> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(CraftingTweaks.MOD_ID, "hello"));

    public static HelloMessage decode(FriendlyByteBuf buf) {
        return new HelloMessage();
    }

    public static void encode(FriendlyByteBuf buf, HelloMessage message) {
    }

    public static void handle(Player player, HelloMessage message) {
        CraftingTweaks.isServerSideInstalled = true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
