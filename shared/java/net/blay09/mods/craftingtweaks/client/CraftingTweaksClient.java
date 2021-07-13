package net.blay09.mods.craftingtweaks.client;


import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.client.keybinds.BalmKeyMappings;
import net.blay09.mods.balm.client.screen.BalmScreens;
import net.blay09.mods.balm.event.client.BalmClientEvents;
import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksClientAPI;
import net.blay09.mods.craftingtweaks.api.GridGuiHandler;
import net.blay09.mods.craftingtweaks.api.InternalClientMethodsImpl;
import net.blay09.mods.craftingtweaks.api.impl.DefaultGridGuiHandler;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksMode;
import net.blay09.mods.craftingtweaks.network.*;
import net.blay09.mods.balm.event.BalmEvents;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.balm.mixin.ScreenAccessor;
import net.blay09.mods.balm.network.BalmNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class CraftingTweaksClient {

    private static final ClientProvider clientProvider = new ClientProvider();

    private static boolean ignoreMouseUp;
    private static int rightClickCraftingSlot = -1;
    private static int guiLeftOnMistakeFix;

    public static void initialize() {
        CraftingTweaksClientAPI.setupAPI(new InternalClientMethodsImpl());

        //noinspection unchecked
        CraftingTweaksClientAPI.registerCraftingGridGuiHandler(AbstractContainerScreen.class, new DefaultGridGuiHandler());

        ModKeyMappings.initialize();

        BalmEvents.onItemCrafted(CraftingTweaksClient::onItemCrafted);

        BalmClientEvents.onConnectedToServer(it -> CraftingTweaks.isServerSideInstalled = false);

        BalmClientEvents.onScreenInitialized(CraftingTweaksClient::screenInitialized);
        BalmClientEvents.onScreenInitialized(CraftingTweaksClient::screenInitialized);
        BalmClientEvents.onScreenKeyPressed(CraftingTweaksClient::screenKeyPressed);
        BalmClientEvents.onScreenMouseClick(CraftingTweaksClient::screenMouseClick);
        BalmClientEvents.onScreenMouseRelease(CraftingTweaksClient::screenMouseRelease);
        BalmClientEvents.onScreenDrawn(CraftingTweaksClient::screenDrawn);
    }

    public static boolean screenKeyPressed(Screen screen, int key, int scanCode, int modifiers) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) {
            return false;
        }

        if (!(screen instanceof AbstractContainerScreen<?>)) {
            return false;
        }

        // Toggle client-only mode for testing if BLAY is held
        Window window = Minecraft.getInstance().getWindow();
        if (CraftingTweaks.isServerSideInstalled
                && GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_B) == 1
                && GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_L) == 1
                && GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_A) == 1
                && (GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_Y) == 1 || GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_Z) == 1)) {
            CraftingTweaks.isServerSideInstalled = false;
            player.displayClientMessage(new TextComponent("[CraftingTweaks] Enabled client-side testing mode"), false);
        }

        CraftingGrid grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
        CompressType compressType = ModKeyMappings.getCompressTypeForKey(key, scanCode);
        if (grid != null) {
            String modId = grid.getId().getNamespace();
            CraftingTweaksMode config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(modId);
            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                boolean isRotate = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyRotate, key, scanCode);
                boolean isRotateCCW = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyRotateCounterClockwise, key, scanCode);
                boolean isBalance = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyBalance, key, scanCode);
                boolean isSpread = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keySpread, key, scanCode);
                boolean isClear = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyClear, key, scanCode);
                boolean isForceClear = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyForceClear, key, scanCode);
                boolean isRefill = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyRefillLast, key, scanCode);
                boolean isRefillStack = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyRefillLastStack, key, scanCode);
                if (isRotate || isRotateCCW) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        BalmNetworking.sendToServer(new RotateMessage(grid.getId(), isRotateCCW));
                    } else {
                        clientProvider.rotateGrid(player, menu, grid, isRotateCCW);
                    }
                    return true;
                } else if (isClear || isForceClear) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        BalmNetworking.sendToServer(new ClearMessage(grid.getId(), isForceClear));
                    } else {
                        clientProvider.clearGrid(player, menu, grid, isForceClear);
                    }
                    return true;
                } else if (isBalance || isSpread) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        BalmNetworking.sendToServer(new BalanceMessage(grid.getId(), isSpread));
                    } else {
                        if (isSpread) {
                            clientProvider.spreadGrid(player, menu, grid);
                        } else {
                            clientProvider.balanceGrid(player, menu, grid);
                        }
                    }
                    return true;
                } else if (isRefill || isRefillStack) {
                    clientProvider.refillLastCrafted(player, menu, grid, isRefillStack);
                    return true;
                }
            }

            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) screen;
            if (compressType != null) {
                Slot mouseSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
                if (mouseSlot != null) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        BalmNetworking.sendToServer(new CompressMessage(mouseSlot.index, compressType));
                    } else {
                        clientProvider.compress(player, menu, grid, mouseSlot, compressType);
                    }
                    return true;
                }
            } else if (BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyToggleButtons, key, scanCode)) {
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
                    BalmNetworking.sendToServer(new CompressMessage(mouseSlot.index, compressType));
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

        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) {
            return false;
        }

        Slot mouseSlot = screen instanceof AbstractContainerScreen<?> ? ((AbstractContainerScreenAccessor) screen).getHoveredSlot() : null;
        CraftingGrid grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
        if (grid != null) {
            if (BalmKeyMappings.isKeyDownIgnoreContext(ModKeyMappings.keyTransferStack)) {
                if (mouseSlot != null && mouseSlot.hasItem()) {
                    List<Slot> transferSlots = Lists.newArrayList();
                    transferSlots.add(mouseSlot);
                    if (Screen.hasShiftDown()) {
                        ItemStack mouseSlotStack = mouseSlot.getItem();
                        for (Slot slot : menu.slots) {
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
                            BalmNetworking.sendToServer(new TransferStackMessage(grid.getId(), slot.index));
                        }
                    } else {
                        for (Slot slot : transferSlots) {
                            clientProvider.transferIntoGrid(player, menu, grid, slot);
                        }
                        ignoreMouseUp = true;
                    }

                    return true;
                }
            } else if (CraftingTweaksConfig.getActive().client.rightClickCraftsStack && button == 1 && mouseSlot instanceof ResultSlot) {
                if (CraftingTweaks.isServerSideInstalled) {
                    BalmNetworking.sendToServer(new CraftStackMessage(mouseSlot.index));
                } else {
                    rightClickCraftingSlot = mouseSlot.index;
                }
                ignoreMouseUp = true;
                return true;
            }
        }

        return false;
    }

    public static void screenInitialized(Screen screen) {
        // We need to do this as soon as possible because EnderIO wraps the button and gives it a new id, completely hiding it from other mods...
        if (screen instanceof AbstractContainerScreen<?>) {
            CraftingGuideButtonFixer.fixMistakes((AbstractContainerScreen<?>) screen);
            guiLeftOnMistakeFix = ((AbstractContainerScreenAccessor) screen).getLeftPos();
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            GridGuiHandler guiHandler = CraftingTweaksClientProviderManager.getGridGuiHandler(containerScreen);
            List<CraftingGrid> grids = CraftingTweaksProviderManager.getCraftingGrids(((AbstractContainerScreen<?>) screen).getMenu());
            for (CraftingGrid grid : grids) {
                String modId = grid.getId().getNamespace();
                CraftingTweaksMode config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(modId);
                if ((config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.BUTTONS) && !CraftingTweaksConfig.getActive().client.hideButtons) {
                    guiHandler.createButtons(containerScreen, grid, widget -> BalmScreens.addRenderableWidget(screen, widget));
                }
            }
        }
    }

    private static void handleRightClickCrafting() {
        if (rightClickCraftingSlot == -1) {
            return;
        }

        int craftingSlot = rightClickCraftingSlot;
        rightClickCraftingSlot = -1;

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        MultiPlayerGameMode playerController = Minecraft.getInstance().gameMode;
        if (playerController == null) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) {
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
            playerController.handleInventoryMouseClick(menu.containerId, mouseSlot.index, 0, ClickType.PICKUP, player);
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
