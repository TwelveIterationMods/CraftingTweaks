package net.blay09.mods.craftingtweaks.client;


import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.ItemCraftedEvent;
import net.blay09.mods.balm.api.event.client.ConnectedToServerEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenInitEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenKeyEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenMouseEvent;
import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksClientAPI;
import net.blay09.mods.craftingtweaks.api.GridGuiHandler;
import net.blay09.mods.craftingtweaks.api.impl.InternalClientMethodsImpl;
import net.blay09.mods.craftingtweaks.api.impl.DefaultGridGuiHandler;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksMode;
import net.blay09.mods.craftingtweaks.network.*;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;


public class CraftingTweaksClient {

    private static final ClientProvider clientProvider = new ClientProvider();

    private static boolean ignoreMouseUp;
    private static int rightClickCraftingSlot = -1;
    private static Button unpleasantButton;
    private static int fixedUnpleasantButtonX;

    public static void initialize() {
        CraftingTweaksClientAPI.setupAPI(new InternalClientMethodsImpl());

        //noinspection unchecked
        CraftingTweaksClientAPI.registerCraftingGridGuiHandler(AbstractContainerScreen.class, new DefaultGridGuiHandler());

        ModKeyMappings.initialize();

        Balm.getEvents().onEvent(ItemCraftedEvent.class, CraftingTweaksClient::onItemCrafted);

        Balm.getEvents().onEvent(ConnectedToServerEvent.class, it -> CraftingTweaks.isServerSideInstalled = false);

        Balm.getEvents().onEvent(ScreenInitEvent.Post.class, CraftingTweaksClient::screenInitialized);
        Balm.getEvents().onEvent(ScreenKeyEvent.Press.Post.class, CraftingTweaksClient::screenKeyPressed);
        Balm.getEvents().onEvent(ScreenMouseEvent.Click.Pre.class, CraftingTweaksClient::screenMouseClick);
        Balm.getEvents().onEvent(ScreenMouseEvent.Release.Pre.class, CraftingTweaksClient::screenMouseRelease);
        Balm.getEvents().onEvent(ScreenDrawEvent.Pre.class, CraftingTweaksClient::screenAboutToDraw);
        Balm.getEvents().onEvent(ScreenDrawEvent.Post.class, CraftingTweaksClient::screenDrawn);

        CraftingTweaksDebugger.initialize();
    }

    public static void screenKeyPressed(ScreenKeyEvent event) {
        final var player = Minecraft.getInstance().player;
        if (player != null) {
            // Toggle client-only mode for testing if BLAY is held
            Window window = Minecraft.getInstance().getWindow();
            if (CraftingTweaks.isServerSideInstalled
                    && GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_B) == 1
                    && GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_L) == 1
                    && GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_A) == 1
                    && (GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_Y) == 1 || GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_Z) == 1)) {
                CraftingTweaks.isServerSideInstalled = false;
                player.displayClientMessage(Component.literal("[CraftingTweaks] Enabled client-side testing mode"), false);
            }
        }
    }

    public static boolean screenMouseRelease(ScreenMouseEvent event) {
        if (ignoreMouseUp) {
            ignoreMouseUp = false;
            return true;
        }

        return false;
    }

    public static boolean screenMouseClick(ScreenMouseEvent event) {
        Screen screen = event.getScreen();
        int button = event.getButton();

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
            if (ModKeyMappings.keyTransferStack.isActiveAndDown()) {
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
                            if (ItemStack.isSameItemSameComponents(slotStack, mouseSlotStack)) {
                                transferSlots.add(slot);
                            }
                        }
                    }

                    if (CraftingTweaks.isServerSideInstalled) {
                        for (Slot slot : transferSlots) {
                            Balm.getNetworking().sendToServer(new TransferStackMessage(grid.getId(), slot.index));
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
                    Balm.getNetworking().sendToServer(new CraftStackMessage(mouseSlot.index));
                } else {
                    rightClickCraftingSlot = mouseSlot.index;
                }
                ignoreMouseUp = true;
                return true;
            }
        }

        return false;
    }

    public static void screenInitialized(ScreenInitEvent event) {
        Screen screen = event.getScreen();
        // We need to do this as soon as possible because EnderIO wraps the button and gives it a new id, completely hiding it from other mods...
        if (screen instanceof AbstractContainerScreen<?>) {
            unpleasantButton = CraftingGuideButtonFixer.fixMistakes((AbstractContainerScreen<?>) screen);
            if (unpleasantButton != null) {
                fixedUnpleasantButtonX = unpleasantButton.getX();
            }
        } else {
            unpleasantButton = null;
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            GridGuiHandler guiHandler = CraftingTweaksClientProviderManager.getGridGuiHandler(containerScreen);
            List<CraftingGrid> grids = CraftingTweaksProviderManager.getCraftingGrids(((AbstractContainerScreen<?>) screen).getMenu());
            for (CraftingGrid grid : grids) {
                String modId = grid.getId().getNamespace();
                CraftingTweaksMode config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(modId);
                if ((config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.BUTTONS)) {
                    guiHandler.createButtons(containerScreen, grid, widget -> BalmClient.getScreens().addRenderableWidget(screen, widget));
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

    public static void screenAboutToDraw(ScreenDrawEvent event) {
        Screen screen = event.getScreen();
        if (screen == null) {
            // WAILA somehow breaks the DrawScreenEvent, so we have to null-check here. o_o
            return;
        }

        // Detect changes on the button that shall not be named to fix its positioning
        if (screen instanceof AbstractContainerScreen<?> containerScreen && unpleasantButton != null) {
            int unpleasantX = unpleasantButton.getX();
            if (unpleasantX != fixedUnpleasantButtonX) {
                unpleasantButton = CraftingGuideButtonFixer.fixMistakes(containerScreen);
                if (unpleasantButton != null) {
                    fixedUnpleasantButtonX = unpleasantButton.getX();
                }
            }
        }
    }

    public static void screenDrawn(ScreenDrawEvent event) {
        Screen screen = event.getScreen();
        if (screen == null) {
            // WAILA somehow breaks the DrawScreenEvent, so we have to null-check here. o_o
            return;
        }

        handleRightClickCrafting();
    }

    private static void onItemCrafted(ItemCraftedEvent event) {
        clientProvider.onItemCrafted(event.getCraftMatrix());
    }

    public static ClientProvider getClientProvider() {
        return clientProvider;
    }
}
