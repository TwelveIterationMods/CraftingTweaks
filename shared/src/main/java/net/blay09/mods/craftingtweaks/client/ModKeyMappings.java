package net.blay09.mods.craftingtweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.keymappings.BalmKeyMappings;
import net.blay09.mods.balm.api.client.keymappings.KeyConflictContext;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.craftingtweaks.CompressType;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {

    public static KeyMapping keyRotate;
    public static KeyMapping keyRotateCounterClockwise;
    public static KeyMapping keyBalance;
    public static KeyMapping keySpread;
    public static KeyMapping keyClear;
    public static KeyMapping keyForceClear;
    public static KeyMapping keyCompressOne;
    public static KeyMapping keyCompressStack;
    public static KeyMapping keyCompressAll;
    public static KeyMapping keyDecompressOne;
    public static KeyMapping keyDecompressStack;
    public static KeyMapping keyDecompressAll;
    public static KeyMapping keyRefillLast;
    public static KeyMapping keyRefillLastStack;
    public static KeyMapping keyTransferStack;

    public static void initialize(BalmKeyMappings keyMappings) {
        keyRotate = keyMappings.registerKeyMapping("key.craftingtweaks.rotate", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyRotateCounterClockwise = keyMappings.registerKeyMapping("key.craftingtweaks.rotate_counter_clockwise", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyBalance = keyMappings.registerKeyMapping("key.craftingtweaks.balance", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keySpread = keyMappings.registerKeyMapping("key.craftingtweaks.spread", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyClear = keyMappings.registerKeyMapping("key.craftingtweaks.clear", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyForceClear = keyMappings.registerKeyMapping("key.craftingtweaks.force_clear", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyCompressOne = keyMappings.registerKeyMapping("key.craftingtweaks.compressOne", KeyConflictContext.GUI, KeyModifier.CONTROL, GLFW.GLFW_KEY_K, "key.categories.craftingtweaks");
        keyCompressStack = keyMappings.registerKeyMapping("key.craftingtweaks.compressStack", KeyConflictContext.GUI, KeyModifier.NONE, GLFW.GLFW_KEY_K, "key.categories.craftingtweaks");
        keyCompressAll = keyMappings.registerKeyMapping("key.craftingtweaks.compressAll", KeyConflictContext.GUI, KeyModifier.SHIFT, GLFW.GLFW_KEY_K, "key.categories.craftingtweaks");
        keyDecompressOne = keyMappings.registerKeyMapping("key.craftingtweaks.decompressOne", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyDecompressStack = keyMappings.registerKeyMapping("key.craftingtweaks.decompressStack", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyDecompressAll = keyMappings.registerKeyMapping("key.craftingtweaks.decompressAll", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyRefillLast = keyMappings.registerKeyMapping("key.craftingtweaks.refill_last", KeyConflictContext.GUI, KeyModifier.CONTROL, GLFW.GLFW_KEY_TAB, "key.categories.craftingtweaks");
        keyRefillLastStack = keyMappings.registerKeyMapping("key.craftingtweaks.refill_last_stack", KeyConflictContext.GUI, KeyModifier.NONE, GLFW.GLFW_KEY_TAB, "key.categories.craftingtweaks");
        keyTransferStack = keyMappings.registerKeyMapping("key.craftingtweaks.transfer_stack", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
    }

    @Nullable
    public static CompressType getCompressTypeForKey(int keyCode, int scanCode) {
        if (BalmClient.getKeyMappings().isActiveAndMatches(keyCompressOne, keyCode, scanCode)) {
            return CompressType.COMPRESS_ONE;
        } else if (BalmClient.getKeyMappings().isActiveAndMatches(keyCompressStack, keyCode, scanCode)) {
            return CompressType.COMPRESS_STACK;
        } else if (BalmClient.getKeyMappings().isActiveAndMatches(keyCompressAll, keyCode, scanCode)) {
            return CompressType.COMPRESS_ALL;
        } else if (BalmClient.getKeyMappings().isActiveAndMatches(keyDecompressOne, keyCode, scanCode)) {
            return CompressType.DECOMPRESS_ONE;
        } else if (BalmClient.getKeyMappings().isActiveAndMatches(keyDecompressStack, keyCode, scanCode)) {
            return CompressType.DECOMPRESS_STACK;
        } else if (BalmClient.getKeyMappings().isActiveAndMatches(keyDecompressAll, keyCode, scanCode)) {
            return CompressType.DECOMPRESS_ALL;
        }

        return null;
    }

}
