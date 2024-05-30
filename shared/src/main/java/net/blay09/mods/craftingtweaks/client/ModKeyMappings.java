package net.blay09.mods.craftingtweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.craftingtweaks.CompressType;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.kuma.api.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ModKeyMappings {

    public static ManagedKeyMapping keyRotate;
    public static ManagedKeyMapping keyRotateCounterClockwise;
    public static ManagedKeyMapping keyBalance;
    public static ManagedKeyMapping keySpread;
    public static ManagedKeyMapping keyClear;
    public static ManagedKeyMapping keyForceClear;
    public static ManagedKeyMapping keyCompressOne;
    public static ManagedKeyMapping keyCompressStack;
    public static ManagedKeyMapping keyCompressAll;
    public static ManagedKeyMapping keyDecompressOne;
    public static ManagedKeyMapping keyDecompressStack;
    public static ManagedKeyMapping keyDecompressAll;
    public static ManagedKeyMapping keyRefillLast;
    public static ManagedKeyMapping keyRefillLastStack;
    public static ManagedKeyMapping keyTransferStack;

    public static void initialize() {
        keyRotate = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "rotate"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyRotateCounterClockwise = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "rotate_counter_clockwise"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyBalance = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "balance"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keySpread = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "spread"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyClear = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "clear"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyForceClear = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "force_clear"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyCompressOne = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "compress_one"))
                .withDefault(InputBinding.key(InputConstants.KEY_K, KeyModifiers.of(KeyModifier.CONTROL)))
                .withFallbackDefault(InputBinding.none()) // TODO we avoid a virtual binding for now until Kuma#1 is fixed
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyCompressStack = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "compress_stack"))
                .withDefault(InputBinding.key(InputConstants.KEY_K))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyCompressAll = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "compress_all"))
                .withDefault(InputBinding.key(InputConstants.KEY_K, KeyModifiers.of(KeyModifier.SHIFT)))
                .withFallbackDefault(InputBinding.none()) // TODO we avoid a virtual binding for now until Kuma#1 is fixed
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyDecompressOne = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "decompress_one"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyDecompressStack = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "decompress_stack"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyDecompressAll = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "decompress_all"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyRefillLast = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "refill_last"))
                .withDefault(InputBinding.key(InputConstants.KEY_TAB, KeyModifiers.of(KeyModifier.CONTROL)))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyRefillLastStack = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "refill_last_stack"))
                .withDefault(InputBinding.key(InputConstants.KEY_TAB))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyTransferStack = Kuma.createKeyMapping(new ResourceLocation(CraftingTweaks.MOD_ID, "transfer_stack"))
                .withContext(KeyConflictContext.SCREEN)
                .build();
    }

    @Nullable
    public static CompressType getCompressTypeForKey(int keyCode, int scanCode, int modifiers) {
        if (keyCompressOne.isActiveAndMatchesKey(keyCode, scanCode, modifiers)) {
            return CompressType.COMPRESS_ONE;
        } else if (keyCompressStack.isActiveAndMatchesKey(keyCode, scanCode, modifiers)) {
            return CompressType.COMPRESS_STACK;
        } else if (keyCompressAll.isActiveAndMatchesKey(keyCode, scanCode, modifiers)) {
            return CompressType.COMPRESS_ALL;
        } else if (keyDecompressOne.isActiveAndMatchesKey(keyCode, scanCode, modifiers)) {
            return CompressType.DECOMPRESS_ONE;
        } else if (keyDecompressStack.isActiveAndMatchesKey(keyCode, scanCode, modifiers)) {
            return CompressType.DECOMPRESS_STACK;
        } else if (keyDecompressAll.isActiveAndMatchesKey(keyCode, scanCode, modifiers)) {
            return CompressType.DECOMPRESS_ALL;
        }

        return null;
    }

}
