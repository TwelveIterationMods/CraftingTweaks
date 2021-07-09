package net.blay09.mods.craftingtweaks.client;


import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksMode;
import net.blay09.mods.craftingtweaks.network.*;
import net.blay09.mods.forbic.client.ForbicKeyBindings;
import net.blay09.mods.forbic.client.ForbicScreens;
import net.blay09.mods.forbic.event.ForbicEvents;
import net.blay09.mods.forbic.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.forbic.mixin.ScreenAccessor;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class CraftingTweaksClient {

    private static final ClientProvider clientProvider = new ClientProvider();

    private static boolean ignoreMouseUp;
    private static int rightClickCraftingSlot = -1;
    private static int guiLeftOnMistakeFix;

    public static void initialize() {
        ModKeyBindings.initialize();

        ForbicEvents.onItemCrafted(CraftingTweaksClient::onItemCrafted);

        ForbicEvents.onScreenInitialized(CraftingTweaksClient::screenInitialized);
        ForbicEvents.onScreenInitialized(CraftingTweaksClient::screenInitialized);
        ForbicEvents.onScreenKeyPressed(CraftingTweaksClient::screenKeyPressed);
        ForbicEvents.onScreenMouseClick(CraftingTweaksClient::screenMouseClick);
        ForbicEvents.onScreenMouseRelease(CraftingTweaksClient::screenMouseRelease);
        ForbicEvents.onScreenDrawn(CraftingTweaksClient::screenDrawn);
    }

    public static boolean screenKeyPressed(Screen screen, int key, int scanCode, int modifiers) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }

        AbstractContainerMenu container = player.containerMenu;
        if (container == null) {
            return false;
        }

        if (!(screen instanceof AbstractContainerScreen<?>)) {
            return false;
        }

        TweakProvider<AbstractContainerMenu> provider = CraftingTweaksProviderManager.getProvider(container);
        CompressType compressType = ModKeyBindings.getCompressTypeForKey(key, scanCode);
        if (provider != null && provider.isValidContainer(container)) {
            CraftingTweaksMode config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(provider.getModId());
            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                boolean isRotate = ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keyRotate, key, scanCode);
                boolean isRotateCCW = ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keyRotateCounterClockwise, key, scanCode);
                boolean isBalance = ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keyBalance, key, scanCode);
                boolean isSpread = ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keySpread, key, scanCode);
                boolean isClear = ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keyClear, key, scanCode);
                boolean isForceClear = ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keyForceClear, key, scanCode);
                boolean isRefill = ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keyRefillLast, key, scanCode);
                boolean isRefillStack = ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keyRefillLastStack, key, scanCode);
                if (isRotate || isRotateCCW) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        ForbicNetworking.sendToServer(new RotateMessage(0, isRotateCCW));
                    } else {
                        clientProvider.rotateGrid(provider, player, container, 0, isRotateCCW);
                    }
                    return true;
                } else if (isClear || isForceClear) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        ForbicNetworking.sendToServer(new ClearMessage(0, isForceClear));
                    } else {
                        clientProvider.clearGrid(provider, player, container, 0, isForceClear);
                    }
                    return true;
                } else if (isBalance || isSpread) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        ForbicNetworking.sendToServer(new BalanceMessage(0, isSpread));
                    } else {
                        if (isSpread) {
                            clientProvider.spreadGrid(provider, player, container, 0);
                        } else {
                            clientProvider.balanceGrid(provider, player, container, 0);
                        }
                    }
                    return true;
                } else if (isRefill || isRefillStack) {
                    clientProvider.refillLastCrafted(provider, player, container, 0, isRefillStack);
                    return true;
                }
            }

            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) screen;
            if (compressType != null) {
                Slot mouseSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
                if (mouseSlot != null) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        ForbicNetworking.sendToServer(new CompressMessage(mouseSlot.index, compressType));
                    } else {
                        clientProvider.compress(provider, player, container, mouseSlot, compressType);
                    }
                    return true;
                }
            } else if (ForbicKeyBindings.isActiveAndMatches(ModKeyBindings.keyToggleButtons, key, scanCode)) {
                CraftingTweaksConfig.setHideButtons(!CraftingTweaksConfig.getActive().client.hideButtons);
                Minecraft mc = Minecraft.getInstance();
                screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                return true;
            }
        } else if (CraftingTweaks.isServerSideInstalled) {
            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) screen;
            if (compressType != null) {
                Slot mouseSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
                if (mouseSlot != null) {
                    ForbicNetworking.sendToServer(new CompressMessage(mouseSlot.index, compressType));
                }
                return true;
            }
        }

        return false;
    }

    public static boolean screenMouseRelease(Screen screen, double mouseX, double mouseY, int button) {
        if (ignoreMouseUp) {
            ignoreMouseUp = false;
            return true;
        }

        return false;
    }

    public static boolean screenMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        /// Reset right-click crafting if any click happens
        rightClickCraftingSlot = -1;

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }

        AbstractContainerMenu container = player.containerMenu;
        if (container == null) {
            return false;
        }

        Slot mouseSlot = screen instanceof AbstractContainerScreen<?> ? ((AbstractContainerScreenAccessor) screen).getHoveredSlot() : null;
        TweakProvider<AbstractContainerMenu> provider = CraftingTweaksProviderManager.getProvider(container);
        if (provider != null && provider.isValidContainer(container)) {
            if (ForbicKeyBindings.isKeyDownIgnoreContext(ModKeyBindings.keyTransferStack)) {
                if (mouseSlot != null && mouseSlot.hasItem()) {
                    List<Slot> transferSlots = Lists.newArrayList();
                    transferSlots.add(mouseSlot);
                    if (Screen.hasShiftDown()) {
                        ItemStack mouseSlotStack = mouseSlot.getItem();
                        for (Slot slot : container.slots) {
                            if (!slot.hasItem() || mouseSlot == slot) {
                                continue;
                            }
                            ItemStack slotStack = slot.getItem();
                            if (slotStack.sameItem(mouseSlotStack) && ItemStack.tagMatches(slotStack, mouseSlotStack)) {
                                transferSlots.add(slot);
                            }
                        }
                    }

                    if (CraftingTweaks.isServerSideInstalled) {
                        for (Slot slot : transferSlots) {
                            ForbicNetworking.sendToServer(new TransferStackMessage(0, slot.index));
                        }
                    } else {
                        for (Slot slot : transferSlots) {
                            clientProvider.transferIntoGrid(provider, player, container, 0, slot);
                        }
                        ignoreMouseUp = true;
                    }

                    return true;
                }
            } else if (CraftingTweaksConfig.getActive().client.rightClickCraftsStack && button == 1 && mouseSlot instanceof ResultSlot) {
                if (CraftingTweaks.isServerSideInstalled) {
                    ForbicNetworking.sendToServer(new CraftStackMessage(mouseSlot.index));
                } else {
                    rightClickCraftingSlot = mouseSlot.index;
                }
                ignoreMouseUp = true;
                return true;
            }
        }

        return false;
    }

    private static <T extends AbstractContainerMenu> void initGui(AbstractContainerScreen<T> screen) {
        TweakProvider<T> provider = CraftingTweaksProviderManager.getProvider(screen.getMenu());
        if (provider != null) {
            CraftingTweaksMode config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(provider.getModId());
            if ((config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.BUTTONS) && !CraftingTweaksConfig.getActive().client.hideButtons) {
                if (provider.isValidContainer(screen.getMenu())) {
                    provider.initGui(screen, widget -> ForbicScreens.addRenderableWidget(screen, widget));
                }
            }
        }
    }

    public static void screenInitialized(Screen screen) {
        // We need to do this as soon as possible because EnderIO wraps the button and gives it a new id, completely hiding it from other mods...
        if (screen instanceof AbstractContainerScreen<?>) {
            CraftingGuideButtonFixer.fixMistakes((AbstractContainerScreen<?>) screen);
            guiLeftOnMistakeFix = ((AbstractContainerScreenAccessor) screen).getLeftPos();
        }

        if (screen instanceof AbstractContainerScreen<?>) {
            initGui((AbstractContainerScreen<?>) screen);
        }
    }

    private static void handleRightClickCrafting() {
        if (rightClickCraftingSlot == -1) {
            return;
        }

        int craftingSlot = rightClickCraftingSlot;
        rightClickCraftingSlot = -1;

        Player entityPlayer = Minecraft.getInstance().player;
        if (entityPlayer == null) {
            return;
        }

        MultiPlayerGameMode playerController = Minecraft.getInstance().gameMode;
        if (playerController == null) {
            return;
        }

        AbstractContainerMenu menu = entityPlayer.containerMenu;
        if (menu == null) {
            return;
        }

        TweakProvider<AbstractContainerMenu> provider = CraftingTweaksProviderManager.getProvider(menu);
        if (provider == null || !provider.isValidContainer(menu)) {
            return;
        }

        if (craftingSlot >= menu.slots.size()) {
            return;
        }

        Slot mouseSlot = menu.slots.get(craftingSlot);
        if (!mouseSlot.hasItem()) {
            rightClickCraftingSlot = mouseSlot.index;
            return;
        }

        ItemStack mouseStack = menu.getCarried();
        if (mouseStack.isEmpty() || mouseStack.getCount() + mouseSlot.getItem().getCount() <= mouseStack.getMaxStackSize()) {
            playerController.handleInventoryMouseClick(menu.containerId, mouseSlot.index, 0, ClickType.PICKUP, entityPlayer);
            rightClickCraftingSlot = mouseSlot.index;
        }
    }

    public static void screenDrawn(Screen screen, PoseStack poseStack, int mouseX, int mouseY) {
        if (screen == null) {
            // WAILA somehow breaks the DrawScreenEvent, so we have to null-check here. o_o
            return;
        }

        // Detect changes on guiLeft to fix the recipe book button positioning (guiLeft changes on recipe book toggle)
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            int guiLeft = ((AbstractContainerScreenAccessor) containerScreen).getLeftPos();
            if (guiLeft != guiLeftOnMistakeFix) {
                CraftingGuideButtonFixer.fixMistakes(containerScreen);
                guiLeftOnMistakeFix = guiLeft;
            }
        }

        handleRightClickCrafting();

        if (!CraftingTweaksConfig.getActive().client.hideButtonTooltips) {
            List<Component> tooltipList = Collections.emptyList();
            for (GuiEventListener button : ((ScreenAccessor) screen).getChildren()) {
                if (button instanceof ITooltipProvider && button.isMouseOver(mouseX, mouseY)) {
                    tooltipList = ((ITooltipProvider) button).getTooltip();
                    break;
                }
            }
            if (!tooltipList.isEmpty()) {
                screen.renderTooltip(poseStack, tooltipList, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    private static void onItemCrafted(Player player, ItemStack itemStack, Container craftMatrix) {
        clientProvider.onItemCrafted(craftMatrix);
    }

    public static ClientProvider getClientProvider() {
        return clientProvider;
    }
}
