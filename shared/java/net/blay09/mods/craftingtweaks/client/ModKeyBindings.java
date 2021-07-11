package net.blay09.mods.craftingtweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.craftingtweaks.CompressType;
import net.blay09.mods.forbic.client.ForbicKeyBindings;
import net.blay09.mods.forbic.client.KeyConflictContext;
import net.blay09.mods.forbic.client.KeyModifier;
import net.blay09.mods.forbic.event.ForbicEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings extends ForbicKeyBindings {

    public static KeyMapping keyRotate;
    public static KeyMapping keyRotateCounterClockwise;
    public static KeyMapping keyBalance;
    public static KeyMapping keySpread;
    public static KeyMapping keyClear;
    public static KeyMapping keyForceClear;
    public static KeyMapping keyToggleButtons;
    public static KeyMapping keyCompressOne;
    public static KeyMapping keyCompressStack;
    public static KeyMapping keyCompressAll;
    public static KeyMapping keyDecompressOne;
    public static KeyMapping keyDecompressStack;
    public static KeyMapping keyDecompressAll;
    public static KeyMapping keyRefillLast;
    public static KeyMapping keyRefillLastStack;
    public static KeyMapping keyTransferStack;

    public static void initialize() {
        keyRotate = registerKeyBinding("key.craftingtweaks.rotate", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyRotateCounterClockwise = registerKeyBinding("key.craftingtweaks.rotate_counter_clockwise", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyBalance = registerKeyBinding("key.craftingtweaks.balance", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keySpread = registerKeyBinding("key.craftingtweaks.spread", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyClear = registerKeyBinding("key.craftingtweaks.clear", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyForceClear = registerKeyBinding("key.craftingtweaks.force_clear", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyToggleButtons = registerKeyBinding("key.craftingtweaks.toggleButtons", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyCompressOne = registerKeyBinding("key.craftingtweaks.compressOne", KeyConflictContext.GUI, KeyModifier.CONTROL, GLFW.GLFW_KEY_K, "key.categories.craftingtweaks");
        keyCompressStack = registerKeyBinding("key.craftingtweaks.compressStack", KeyConflictContext.GUI, KeyModifier.NONE, GLFW.GLFW_KEY_K, "key.categories.craftingtweaks");
        keyCompressAll = registerKeyBinding("key.craftingtweaks.compressAll", KeyConflictContext.GUI, KeyModifier.SHIFT, GLFW.GLFW_KEY_K, "key.categories.craftingtweaks");
        keyDecompressOne = registerKeyBinding("key.craftingtweaks.decompressOne", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyDecompressStack = registerKeyBinding("key.craftingtweaks.decompressStack", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyDecompressAll = registerKeyBinding("key.craftingtweaks.decompressAll", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.craftingtweaks");
        keyRefillLast = registerKeyBinding("key.craftingtweaks.refill_last", KeyConflictContext.GUI, KeyModifier.CONTROL, GLFW.GLFW_KEY_TAB, "key.categories.craftingtweaks");
        keyRefillLastStack = registerKeyBinding("key.craftingtweaks.refill_last_stack", KeyConflictContext.GUI, KeyModifier.NONE, GLFW.GLFW_KEY_TAB, "key.categories.craftingtweaks");

        ForbicEvents.onClientStarted(client -> {
            keyTransferStack = Minecraft.getInstance().options.keyUp;
        });
    }

    @Nullable
    public static CompressType getCompressTypeForKey(int keyCode, int scanCode) {
        if (isActiveAndMatches(keyCompressOne, keyCode, scanCode)) {
            return CompressType.COMPRESS_ONE;
        } else if (isActiveAndMatches(keyCompressStack, keyCode, scanCode)) {
            return CompressType.COMPRESS_STACK;
        } else if (isActiveAndMatches(keyCompressAll, keyCode, scanCode)) {
            return CompressType.COMPRESS_ALL;
        } else if (isActiveAndMatches(keyDecompressOne, keyCode, scanCode)) {
            return CompressType.DECOMPRESS_ONE;
        } else if (isActiveAndMatches(keyDecompressStack, keyCode, scanCode)) {
            return CompressType.DECOMPRESS_STACK;
        } else if (isActiveAndMatches(keyDecompressAll, keyCode, scanCode)) {
            return CompressType.DECOMPRESS_ALL;
        }

        return null;
    }

}
