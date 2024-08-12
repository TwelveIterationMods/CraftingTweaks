package net.blay09.mods.craftingtweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.craftingtweaks.CompressType;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksMode;
import net.blay09.mods.craftingtweaks.network.*;
import net.blay09.mods.kuma.api.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

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
        keyRotate = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "rotate"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> {
                    if (event.screen() instanceof AbstractContainerScreen<?> screen) {
                        final var menu = screen.getMenu();
                        final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
                        if (grid != null) {
                            final var config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(grid.getId().getNamespace());
                            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    Balm.getNetworking().sendToServer(new RotateMessage(grid.getId(), false));
                                } else {
                                    CraftingTweaksClient.getClientProvider().rotateGrid(Minecraft.getInstance().player, menu, grid, false);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build();

        keyRotateCounterClockwise = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "rotate_counter_clockwise"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> {
                    if (event.screen() instanceof AbstractContainerScreen<?> screen) {
                        final var menu = screen.getMenu();
                        final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
                        if (grid != null) {
                            final var config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(grid.getId().getNamespace());
                            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    Balm.getNetworking().sendToServer(new RotateMessage(grid.getId(), true));
                                } else {
                                    CraftingTweaksClient.getClientProvider().rotateGrid(Minecraft.getInstance().player, menu, grid, true);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build();

        keyBalance = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "balance"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> {
                    if (event.screen() instanceof AbstractContainerScreen<?> screen) {
                        final var menu = screen.getMenu();
                        final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
                        if (grid != null) {
                            final var config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(grid.getId().getNamespace());
                            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    Balm.getNetworking().sendToServer(new BalanceMessage(grid.getId(), false));
                                } else {
                                    CraftingTweaksClient.getClientProvider().balanceGrid(Minecraft.getInstance().player, menu, grid);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build();

        keySpread = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "spread"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> {
                    if (event.screen() instanceof AbstractContainerScreen<?> screen) {
                        final var menu = screen.getMenu();
                        final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
                        if (grid != null) {
                            final var config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(grid.getId().getNamespace());
                            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    Balm.getNetworking().sendToServer(new BalanceMessage(grid.getId(), true));
                                } else {
                                    CraftingTweaksClient.getClientProvider().spreadGrid(Minecraft.getInstance().player, menu, grid);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build();

        keyClear = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "clear"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> {
                    if (event.screen() instanceof AbstractContainerScreen<?> screen) {
                        final var menu = screen.getMenu();
                        final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
                        if (grid != null) {
                            final var config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(grid.getId().getNamespace());
                            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    Balm.getNetworking().sendToServer(new ClearMessage(grid.getId(), false));
                                } else {
                                    CraftingTweaksClient.getClientProvider().clearGrid(Minecraft.getInstance().player, menu, grid, false);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build();

        keyForceClear = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "force_clear"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> {
                    if (event.screen() instanceof AbstractContainerScreen<?> screen) {
                        final var menu = screen.getMenu();
                        final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
                        if (grid != null) {
                            final var config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(grid.getId().getNamespace());
                            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    Balm.getNetworking().sendToServer(new ClearMessage(grid.getId(), true));
                                } else {
                                    CraftingTweaksClient.getClientProvider().clearGrid(Minecraft.getInstance().player, menu, grid, true);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build();

        keyCompressOne = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "compress_one"))
                .withDefault(InputBinding.key(InputConstants.KEY_K, KeyModifiers.of(KeyModifier.CONTROL)))
                .withFallbackDefault(InputBinding.none()) // TODO we avoid a virtual binding for now until Kuma#1 is fixed
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> handleCompression(event, CompressType.COMPRESS_ONE))
                .build();

        keyCompressStack = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "compress_stack"))
                .withDefault(InputBinding.key(InputConstants.KEY_K))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> handleCompression(event, CompressType.COMPRESS_STACK))
                .build();

        keyCompressAll = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "compress_all"))
                .withDefault(InputBinding.key(InputConstants.KEY_K, KeyModifiers.of(KeyModifier.SHIFT)))
                .withFallbackDefault(InputBinding.none()) // TODO we avoid a virtual binding for now until Kuma#1 is fixed
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> handleCompression(event, CompressType.COMPRESS_ALL))
                .build();

        keyDecompressOne = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "decompress_one"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> handleCompression(event, CompressType.DECOMPRESS_ONE))
                .build();

        keyDecompressStack = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "decompress_stack"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> handleCompression(event, CompressType.DECOMPRESS_STACK))
                .build();

        keyDecompressAll = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "decompress_all"))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> handleCompression(event, CompressType.DECOMPRESS_ALL))
                .build();

        keyRefillLast = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "refill_last"))
                .withDefault(InputBinding.key(InputConstants.KEY_TAB, KeyModifiers.of(KeyModifier.CONTROL)))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> {
                    if (event.screen() instanceof AbstractContainerScreen<?> screen) {
                        final var menu = screen.getMenu();
                        final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
                        if (grid != null) {
                            final var config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(grid.getId().getNamespace());
                            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    Balm.getNetworking().sendToServer(new RefillLastCraftedMessage(grid.getId(), false));
                                } else {
                                    CraftingTweaksClient.getClientProvider().refillLastCrafted(Minecraft.getInstance().player, menu, grid, false);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build();

        keyRefillLastStack = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "refill_last_stack"))
                .withDefault(InputBinding.key(InputConstants.KEY_TAB))
                .withContext(KeyConflictContext.SCREEN)
                .handleScreenInput(event -> {
                    if (event.screen() instanceof AbstractContainerScreen<?> screen) {
                        final var menu = screen.getMenu();
                        final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
                        if (grid != null) {
                            final var config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(grid.getId().getNamespace());
                            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    Balm.getNetworking().sendToServer(new RefillLastCraftedMessage(grid.getId(), true));
                                } else {
                                    CraftingTweaksClient.getClientProvider().refillLastCrafted(Minecraft.getInstance().player, menu, grid, true);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build();

        keyTransferStack = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(CraftingTweaks.MOD_ID, "transfer_stack"))
                .withContext(KeyConflictContext.SCREEN)
                .build();
    }

    private static boolean handleCompression(ScreenInputEvent event, CompressType compressType) {
        if (event.screen() instanceof AbstractContainerScreen<?> screen) {
            final var menu = screen.getMenu();
            final var grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
            final var mouseSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
            if (mouseSlot != null) {
                if (grid != null) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        Balm.getNetworking().sendToServer(new CompressMessage(mouseSlot.index, compressType));
                    } else {
                        CraftingTweaksClient.getClientProvider()
                                .compress(Minecraft.getInstance().player, menu, grid, mouseSlot, compressType);
                    }
                    return true;
                } else if (CraftingTweaks.isServerSideInstalled) {
                    Balm.getNetworking().sendToServer(new CompressMessage(mouseSlot.index, compressType));
                    return true;
                }
            }
        }
        return false;
    }
}
