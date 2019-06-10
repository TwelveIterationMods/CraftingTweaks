package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.CompressType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class KeyBindings {

    public static final KeyBinding keyRotate = new KeyBinding("key.craftingtweaks.rotate", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyRotateCounterClockwise = new KeyBinding("key.craftingtweaks.rotate_counter_clockwise", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyBalance = new KeyBinding("key.craftingtweaks.balance", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keySpread = new KeyBinding("key.craftingtweaks.spread", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyClear = new KeyBinding("key.craftingtweaks.clear", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyForceClear = new KeyBinding("key.craftingtweaks.force_clear", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyToggleButtons = new KeyBinding("key.craftingtweaks.toggleButtons", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyCompressOne = new KeyBinding("key.craftingtweaks.compressOne", KeyConflictContext.GUI, KeyModifier.CONTROL, InputMappings.getInputByCode(GLFW.GLFW_KEY_K, 0), "key.categories.craftingtweaks");
    public static final KeyBinding keyCompressStack = new KeyBinding("key.craftingtweaks.compressStack", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.getInputByCode(GLFW.GLFW_KEY_K, 0), "key.categories.craftingtweaks");
    public static final KeyBinding keyCompressAll = new KeyBinding("key.craftingtweaks.compressAll", KeyConflictContext.GUI, KeyModifier.SHIFT, InputMappings.getInputByCode(GLFW.GLFW_KEY_K, 0), "key.categories.craftingtweaks");
    public static final KeyBinding keyDecompressOne = new KeyBinding("key.craftingtweaks.decompressOne", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyDecompressStack = new KeyBinding("key.craftingtweaks.decompressStack", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyDecompressAll = new KeyBinding("key.craftingtweaks.decompressAll", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.craftingtweaks");
    public static final KeyBinding keyRefillLast = new KeyBinding("key.craftingtweaks.refill_last", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.getInputByCode(GLFW.GLFW_KEY_TAB, 0), "key.categories.craftingtweaks");
    public static final KeyBinding keyRefillLastStack = new KeyBinding("key.craftingtweaks.refill_last_stack", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.getInputByCode(GLFW.GLFW_KEY_TAB, 0), "key.categories.craftingtweaks");
    public static KeyBinding keyTransferStack;

    public static void init() {
        ClientRegistry.registerKeyBinding(keyRotate);
        ClientRegistry.registerKeyBinding(keyRotateCounterClockwise);
        ClientRegistry.registerKeyBinding(keyBalance);
        ClientRegistry.registerKeyBinding(keySpread);
        ClientRegistry.registerKeyBinding(keyClear);
        ClientRegistry.registerKeyBinding(keyForceClear);
        ClientRegistry.registerKeyBinding(keyToggleButtons);
        ClientRegistry.registerKeyBinding(keyCompressOne);
        ClientRegistry.registerKeyBinding(keyCompressStack);
        ClientRegistry.registerKeyBinding(keyCompressAll);
        ClientRegistry.registerKeyBinding(keyDecompressOne);
        ClientRegistry.registerKeyBinding(keyDecompressStack);
        ClientRegistry.registerKeyBinding(keyDecompressAll);
        ClientRegistry.registerKeyBinding(keyRefillLast);
        ClientRegistry.registerKeyBinding(keyRefillLastStack);
        keyTransferStack = Minecraft.getInstance().gameSettings.keyBindForward;
    }

    private static boolean isActiveAndMatches(KeyBinding keyBinding, InputMappings.Input input) {
        if (keyBinding.getKeyModifier() == KeyModifier.NONE) {
            if (KeyModifier.SHIFT.isActive(keyBinding.getKeyConflictContext()) || KeyModifier.CONTROL.isActive(keyBinding.getKeyConflictContext()) || KeyModifier.ALT.isActive(keyBinding.getKeyConflictContext())) {
                return false;
            }
        }

        return keyBinding.isActiveAndMatches(input);
    }

    public static boolean isActiveIgnoreContext(KeyBinding keyBinding) {
        return keyBinding.getKey().getType() == InputMappings.Type.KEYSYM && InputMappings.func_216506_a(Minecraft.getInstance().mainWindow.getHandle(), keyBinding.getKey().getKeyCode());
    }

    @Nullable
    public static CompressType getCompressTypeForKey(InputMappings.Input input) {
        if (isActiveAndMatches(keyCompressOne, input)) {
            return CompressType.COMPRESS_ONE;
        } else if (isActiveAndMatches(keyCompressStack, input)) {
            return CompressType.COMPRESS_STACK;
        } else if (isActiveAndMatches(keyCompressAll, input)) {
            return CompressType.COMPRESS_ALL;
        } else if (isActiveAndMatches(keyDecompressOne, input)) {
            return CompressType.DECOMPRESS_ONE;
        } else if (isActiveAndMatches(keyDecompressStack, input)) {
            return CompressType.DECOMPRESS_STACK;
        } else if (isActiveAndMatches(keyDecompressAll, input)) {
            return CompressType.DECOMPRESS_ALL;
        }

        return null;
    }

}
