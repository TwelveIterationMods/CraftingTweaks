package net.blay09.mods.craftingtweaks.client;


import com.google.common.collect.Lists;
import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfigData;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksMode;
import net.blay09.mods.craftingtweaks.network.*;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
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

import java.util.Collections;
import java.util.List;


public class CraftingTweaksClient {

    private final ClientProvider clientProvider = new ClientProvider();

    private boolean ignoreMouseUp;
    private int rightClickCraftingSlot = -1;
    private int guiLeftOnMistakeFix;

    public static void initialize() {

    }

    @SubscribeEvent
    public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardKeyPressedEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        AbstractContainerMenu container = player.containerMenu;
        if (container == null) {
            return;
        }

        Screen guiScreen = Minecraft.getInstance().screen;
        if (!(guiScreen instanceof AbstractContainerScreen<?>)) {
            return;
        }

        TweakProvider<AbstractContainerMenu> provider = CraftingTweaksProviderManager.getProvider(container);
        CompressType compressType = KeyBindings.getCompressTypeForKey(event.getKeyCode(), event.getScanCode());
        if (provider != null && provider.isValidContainer(container)) {
            CraftingTweaksMode config = CraftingTweaksConfig.getActive().getCraftingTweaksMode(provider.getModId());
            if (config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.HOTKEYS) {
                boolean isRotate = KeyBindings.keyRotate.isActiveAndMatches(input);
                boolean isRotateCCW = KeyBindings.keyRotateCounterClockwise.isActiveAndMatches(input);
                boolean isBalance = KeyBindings.keyBalance.isActiveAndMatches(input);
                boolean isSpread = KeyBindings.keySpread.isActiveAndMatches(input);
                boolean isClear = KeyBindings.keyClear.isActiveAndMatches(input);
                boolean isForceClear = KeyBindings.keyForceClear.isActiveAndMatches(input);
                boolean isRefill = KeyBindings.keyRefillLast.isActiveAndMatches(input);
                boolean isRefillStack = KeyBindings.keyRefillLastStack.isActiveAndMatches(input);
                if (isRotate || isRotateCCW) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        ForbicNetworking.sendToServer(new RotateMessage(0, isRotateCCW));
                    } else {
                        clientProvider.rotateGrid(provider, player, container, 0, isRotateCCW);
                    }
                    event.setCanceled(true);
                } else if (isClear || isForceClear) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        ForbicNetworking.sendToServer(new ClearMessage(0, isForceClear));
                    } else {
                        clientProvider.clearGrid(provider, player, container, 0, isForceClear);
                    }
                    event.setCanceled(true);
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
                    event.setCanceled(true);
                } else if (isRefill || isRefillStack) {
                    clientProvider.refillLastCrafted(provider, player, container, 0, isRefillStack);
                    event.setCanceled(true);
                }
            }

            AbstractContainerScreen<?> guiContainer = (AbstractContainerScreen<?>) guiScreen;
            if (compressType != null) {
                Slot mouseSlot = guiContainer.getSlotUnderMouse();
                if (mouseSlot != null) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        ForbicNetworking.sendToServer(new CompressMessage(mouseSlot.index, compressType));
                    } else {
                        clientProvider.compress(provider, player, container, mouseSlot, compressType);
                    }
                    event.setCanceled(true);
                }
            } else if (KeyBindings.keyToggleButtons.isActiveAndMatches(input)) {
                CraftingTweaksConfigData.setHideButtons(!CraftingTweaksConfig.getActive().client.hideButtons);
                Minecraft mc = Minecraft.getInstance();
                guiScreen.init(mc, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
                event.setCanceled(true);
            }
        } else if (CraftingTweaks.isServerSideInstalled) {
            AbstractContainerScreen<?> guiContainer = (AbstractContainerScreen<?>) guiScreen;
            if (compressType != null) {
                Slot mouseSlot = guiContainer.getSlotUnderMouse();
                if (mouseSlot != null) {
                    ForbicNetworking.sendToServer(new CompressMessage(mouseSlot.index, compressType));
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onGuiMouseEvent(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        if (ignoreMouseUp) {
            event.setCanceled(true);
            ignoreMouseUp = false;
        }
    }

    @SubscribeEvent
    public void onGuiMouseEvent(GuiScreenEvent.MouseClickedEvent.Pre event) {
        /// Reset right-click crafting if any click happens
        rightClickCraftingSlot = -1;

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        AbstractContainerMenu container = player.containerMenu;
        if (container == null) {
            return;
        }

        Slot mouseSlot = event.getGui() instanceof AbstractContainerScreen<?> ? ((AbstractContainerScreen<?>) event.getGui()).getSlotUnderMouse() : null;
        TweakProvider<AbstractContainerMenu> provider = CraftingTweaksProviderManager.getProvider(container);
        if (provider != null && provider.isValidContainer(container)) {
            if (KeyBindings.isActiveIgnoreContext(KeyBindings.keyTransferStack)) {
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

                    event.setCanceled(true);
                }
            } else if (CraftingTweaksConfig.getActive().client.rightClickCraftsStack && event.getButton() == 1 && mouseSlot instanceof ResultSlot) {
                if (CraftingTweaks.isServerSideInstalled) {
                    ForbicNetworking.sendToServer(new CraftStackMessage(mouseSlot.index));
                } else {
                    rightClickCraftingSlot = mouseSlot.index;
                }
                event.setCanceled(true);
                ignoreMouseUp = true;
            }
        }
    }

    private <T extends AbstractContainerMenu> void initGui(AbstractContainerScreen<T> guiContainer, GuiScreenEvent.InitGuiEvent event) {
        TweakProvider<T> provider = CraftingTweaksProviderManager.getProvider(guiContainer.getContainer());
        if (provider != null) {
            CraftingTweaksMode config = CraftingTweaksConfigData.getCraftingTweaksMode(provider.getModId());
            if ((config == CraftingTweaksMode.DEFAULT || config == CraftingTweaksMode.BUTTONS) && !CraftingTweaksConfig.getActive().client.hideButtons.get()) {
                if (provider.isValidContainer(guiContainer.getContainer())) {
                    provider.initGui(guiContainer, event);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInitGuiFirst(GuiScreenEvent.InitGuiEvent.Post event) {
        // We need to do this as soon as possible because EnderIO wraps the button and gives it a new id, completely hiding it from other mods...
        if (event.getGui() instanceof AbstractContainerScreen<?>) {
            CraftingGuideButtonFixer.fixMistakes((AbstractContainerScreen<?>) event.getGui(), event.getWidgetList());
            guiLeftOnMistakeFix = ((AbstractContainerScreen<?>) event.getGui()).getGuiLeft();
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof AbstractContainerScreen<?>) {
            initGui((AbstractContainerScreen<?>) event.getGui(), event);
        }
    }

    private void handleRightClickCrafting() {
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

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() == null) {
            // WAILA somehow breaks the DrawScreenEvent, so we have to null-check here. o_o
            return;
        }

        // Detect changes on guiLeft to fix the recipe book button positioning (guiLeft changes on recipe book toggle)
        if (event.getGui() instanceof AbstractContainerScreen<?>) {
            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) event.getGui();
            int guiLeft = containerScreen.getGuiLeft();
            if (guiLeft != guiLeftOnMistakeFix) {
                CraftingGuideButtonFixer.fixMistakes(containerScreen, containerScreen.getEventListeners());
                guiLeftOnMistakeFix = guiLeft;
            }
        }

        handleRightClickCrafting();

        if (!CraftingTweaksConfigData.CLIENT.hideButtonTooltips.get()) {
            List<Component> tooltipList = Collections.emptyList();
            for (GuiEventListener button : event.getGui().getEventListeners()) {
                if (button instanceof ITooltipProvider && button.isMouseOver(event.getMouseX(), event.getMouseY())) {
                    tooltipList = ((ITooltipProvider) button).getTooltip();
                    break;
                }
            }
            if (!tooltipList.isEmpty()) {
                event.getGui().func_243308_b(event.getMatrixStack(), tooltipList, event.getMouseX(), event.getMouseY());
            }
        }
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        clientProvider.onItemCrafted(event.getInventory());
    }

    public ClientProvider getClientProvider() {
        return clientProvider;
    }
}
