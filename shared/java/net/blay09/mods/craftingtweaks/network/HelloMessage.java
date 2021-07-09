package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class HelloMessage {

    private int dummy;

    public static HelloMessage decode(FriendlyByteBuf buf) {
        return new HelloMessage();
    }

    public static void encode(HelloMessage message, FriendlyByteBuf buf) {
    }

    public static void handle(Player player, HelloMessage message) {
        CraftingTweaks.isServerSideInstalled = true;
    }
}
