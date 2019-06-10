package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.Lists;
import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.net.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.List;

public class CraftingTweaksClient {

    private final ClientProvider clientProvider = new ClientProvider();

    private boolean ignoreMouseUp;
    private int rightClickCraftingSlot = -1;
    private int guiLeftOnMistakeFix;

    @SubscribeEvent
    public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardKeyPressedEvent.Post event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Container container = player.openContainer;
        if (container == null) {
            return;
        }

        Screen guiScreen = Minecraft.getInstance().field_71462_r;
        TweakProvider<Container> provider = CraftingTweaksProviderManager.getProvider(container);
        InputMappings.Input input = InputMappings.getInputByCode(event.getKeyCode(), event.getScanCode());
        CompressType compressType = KeyBindings.getCompressTypeForKey(input);
        if (provider != null && provider.isValidContainer(container)) {
            ModSupportState config = CraftingTweaksConfig.getModSupportState(provider.getModId());
            if (config == ModSupportState.ENABLED || config == ModSupportState.HOTKEYS_ONLY) {
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
                        NetworkHandler.channel.sendToServer(new MessageRotate(0, isRotateCCW));
                    } else {
                        clientProvider.rotateGrid(provider, player, container, 0, isRotateCCW);
                    }
                    event.setCanceled(true);
                } else if (isClear || isForceClear) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        NetworkHandler.channel.sendToServer(new MessageClear(0, isForceClear));
                    } else {
                        clientProvider.clearGrid(provider, player, container, 0, isForceClear);
                    }
                    event.setCanceled(true);
                } else if (isBalance || isSpread) {
                    if (CraftingTweaks.isServerSideInstalled) {
                        NetworkHandler.channel.sendToServer(new MessageBalance(0, isSpread));
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
            if (guiScreen instanceof ContainerScreen<?>) {
                ContainerScreen<?> guiContainer = (ContainerScreen<?>) guiScreen;
                if (compressType != null) {
                    Slot mouseSlot = guiContainer.getSlotUnderMouse();
                    if (mouseSlot != null) {
                        if (CraftingTweaks.isServerSideInstalled) {
                            NetworkHandler.channel.sendToServer(new MessageCompress(mouseSlot.slotNumber, compressType));
                        } else {
                            clientProvider.compress(provider, player, container, mouseSlot, compressType);
                        }
                        event.setCanceled(true);
                    }
                } else if (KeyBindings.keyToggleButtons.isActiveAndMatches(input)) {
                    CraftingTweaksConfig.setHideButtons(!CraftingTweaksConfig.CLIENT.hideButtons.get());
                    Minecraft mc = Minecraft.getInstance();
                    guiScreen.init(mc, mc.mainWindow.getScaledWidth(), mc.mainWindow.getScaledHeight());
                    event.setCanceled(true);
                }
            }
        } else if (CraftingTweaks.isServerSideInstalled && guiScreen instanceof ContainerScreen<?>) {
            ContainerScreen<?> guiContainer = (ContainerScreen<?>) guiScreen;
            if (compressType != null) {
                Slot mouseSlot = guiContainer.getSlotUnderMouse();
                if (mouseSlot != null) {
                    NetworkHandler.channel.sendToServer(new MessageCompress(mouseSlot.slotNumber, compressType));
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

        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Container container = player.openContainer;
        if (container == null) {
            return;
        }

        Slot mouseSlot = event.getGui() instanceof ContainerScreen<?> ? ((ContainerScreen<?>) event.getGui()).getSlotUnderMouse() : null;
        TweakProvider<Container> provider = CraftingTweaksProviderManager.getProvider(container);
        if (provider != null && provider.isValidContainer(container)) {
            if (KeyBindings.isActiveIgnoreContext(KeyBindings.keyTransferStack)) {
                if (mouseSlot != null && mouseSlot.getHasStack()) {
                    List<Slot> transferSlots = Lists.newArrayList();
                    transferSlots.add(mouseSlot);
                    if (Screen.hasShiftDown()) {
                        ItemStack mouseSlotStack = mouseSlot.getStack();
                        for (Slot slot : container.inventorySlots) {
                            if (!slot.getHasStack() || mouseSlot == slot) {
                                continue;
                            }
                            ItemStack slotStack = slot.getStack();
                            if (slotStack.isItemEqual(mouseSlotStack) && ItemStack.areItemStackTagsEqual(slotStack, mouseSlotStack)) {
                                transferSlots.add(slot);
                            }
                        }
                    }

                    if (CraftingTweaks.isServerSideInstalled) {
                        for (Slot slot : transferSlots) {
                            NetworkHandler.channel.sendToServer(new MessageTransferStack(0, slot.slotNumber));
                        }
                    } else {
                        for (Slot slot : transferSlots) {
                            clientProvider.transferIntoGrid(provider, player, container, 0, slot);
                        }
                        ignoreMouseUp = true;
                    }

                    event.setCanceled(true);
                }
            } else if (CraftingTweaksConfig.CLIENT.rightClickCraftsStack.get() && event.getButton() == 1 && mouseSlot instanceof CraftingResultSlot) {
                if (CraftingTweaks.isServerSideInstalled) {
                    NetworkHandler.channel.sendToServer(new MessageCraftStack(mouseSlot.slotNumber));
                } else {
                    rightClickCraftingSlot = mouseSlot.slotNumber;
                }
                event.setCanceled(true);
                ignoreMouseUp = true;
            }
        }
    }

    private <T extends Container> void initGui(ContainerScreen<T> guiContainer, GuiScreenEvent.InitGuiEvent event) {
        TweakProvider<T> provider = CraftingTweaksProviderManager.getProvider(guiContainer.getContainer());
        if (provider != null) {
            ModSupportState config = CraftingTweaksConfig.getModSupportState(provider.getModId());
            if (config == ModSupportState.ENABLED || config == ModSupportState.BUTTONS_ONLY) {
                if (provider.isValidContainer(guiContainer.getContainer())) {
                    provider.initGui(guiContainer, event);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInitGuiFirst(GuiScreenEvent.InitGuiEvent.Post event) {
        // We need to do this as soon as possible because EnderIO wraps the button and gives it a new id, completely hiding it from other mods...
        if (event.getGui() instanceof ContainerScreen<?>) {
            CraftingGuideButtonFixer.fixMistakes((ContainerScreen<?>) event.getGui(), event.getWidgetList());
            guiLeftOnMistakeFix = ((ContainerScreen<?>) event.getGui()).getGuiLeft();
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof ContainerScreen<?>) {
            initGui((ContainerScreen<?>) event.getGui(), event);
        }
    }

    private static final List<String> tooltipList = Lists.newArrayList();

    private void handleRightClickCrafting() {
        if (rightClickCraftingSlot == -1) {
            return;
        }

        int craftingSlot = rightClickCraftingSlot;
        rightClickCraftingSlot = -1;

        PlayerEntity entityPlayer = Minecraft.getInstance().player;
        if (entityPlayer == null) {
            return;
        }

        PlayerController playerController = Minecraft.getInstance().field_71442_b;
        if (playerController == null) {
            return;
        }

        Container container = entityPlayer.openContainer;
        if (container == null) {
            return;
        }

        TweakProvider<Container> provider = CraftingTweaksProviderManager.getProvider(container);
        if (provider == null || !provider.isValidContainer(container)) {
            return;
        }

        if (craftingSlot >= container.inventorySlots.size()) {
            return;
        }

        Slot mouseSlot = container.inventorySlots.get(craftingSlot);
        if (!mouseSlot.getHasStack()) {
            rightClickCraftingSlot = mouseSlot.slotNumber;
            return;
        }

        ItemStack mouseStack = entityPlayer.inventory.getItemStack();
        if (mouseStack.isEmpty() || mouseStack.getCount() + mouseSlot.getStack().getCount() <= mouseStack.getMaxStackSize()) {
            playerController.windowClick(container.windowId, mouseSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
            rightClickCraftingSlot = mouseSlot.slotNumber;
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() == null) {
            // WAILA somehow breaks the DrawScreenEvent, so we have to null-check here. o_o
            return;
        }

        // Detect changes on guiLeft to fix the recipe book button positioning (guiLeft changes on recipe book toggle)
        if (event.getGui() instanceof ContainerScreen<?>) {
            ContainerScreen containerScreen = (ContainerScreen) event.getGui();
            int guiLeft = containerScreen.getGuiLeft();
            if (guiLeft != guiLeftOnMistakeFix) {
                CraftingGuideButtonFixer.fixMistakes(containerScreen, containerScreen.children());
                guiLeftOnMistakeFix = guiLeft;
            }
        }

        handleRightClickCrafting();

        if (!CraftingTweaksConfig.CLIENT.hideButtonTooltips.get()) {
            tooltipList.clear();
            for (IGuiEventListener button : event.getGui().children()) {
                if (button instanceof ITooltipProvider && button.isMouseOver(event.getMouseX(), event.getMouseY())) {
                    ((ITooltipProvider) button).addInformation(tooltipList);
                    break;
                }
            }
            if (!tooltipList.isEmpty()) {
                event.getGui().renderTooltip(tooltipList, event.getMouseX(), event.getMouseY());
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
