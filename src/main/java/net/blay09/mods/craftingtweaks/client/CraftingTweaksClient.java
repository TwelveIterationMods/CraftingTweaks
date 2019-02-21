package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.Lists;
import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.net.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
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

    @SubscribeEvent
    public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardKeyPressedEvent.Post event) {
        EntityPlayerSP player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Container container = player.openContainer;
        if (container == null) {
            return;
        }

        GuiScreen guiScreen = Minecraft.getInstance().currentScreen;
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
            if (guiScreen instanceof GuiContainer) {
                GuiContainer guiContainer = (GuiContainer) guiScreen;
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
                    guiScreen.setWorldAndResolution(mc, mc.mainWindow.getScaledWidth(), mc.mainWindow.getScaledHeight());
                    event.setCanceled(true);
                }
            }
        } else if (CraftingTweaks.isServerSideInstalled && guiScreen instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) guiScreen;
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

        EntityPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Container container = player.openContainer;
        if (container == null) {
            return;
        }

        Slot mouseSlot = event.getGui() instanceof GuiContainer ? ((GuiContainer) event.getGui()).getSlotUnderMouse() : null;
        TweakProvider<Container> provider = CraftingTweaksProviderManager.getProvider(container);
        if (provider != null && provider.isValidContainer(container)) {
            if (KeyBindings.isActiveIgnoreContext(KeyBindings.keyTransferStack)) {
                if (mouseSlot != null && mouseSlot.getHasStack()) {
                    List<Slot> transferSlots = Lists.newArrayList();
                    transferSlots.add(mouseSlot);
                    if (GuiScreen.isShiftKeyDown()) {
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
            } else if (CraftingTweaksConfig.CLIENT.rightClickCraftsStack.get() && event.getButton() == 1 && mouseSlot instanceof SlotCrafting) {
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

    private void initGui(GuiContainer guiContainer, GuiScreenEvent.InitGuiEvent event) {
        TweakProvider provider = CraftingTweaksProviderManager.getProvider(guiContainer.inventorySlots);
        if (provider != null) {
            ModSupportState config = CraftingTweaksConfig.getModSupportState(provider.getModId());
            if (config == ModSupportState.ENABLED || config == ModSupportState.BUTTONS_ONLY) {
                if (provider.isValidContainer(guiContainer.inventorySlots)) {
                    provider.initGui(guiContainer, event);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInitGuiFirst(GuiScreenEvent.InitGuiEvent.Post event) {
        // We need to do this as soon as possible because EnderIO wraps the button and gives it a new id, completely hiding it from other mods...
        if (event.getGui() instanceof GuiContainer) {
            CraftingGuideButtonFixer.fixMistakes((GuiContainer) event.getGui(), event.getButtonList());
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiContainer) {
            initGui((GuiContainer) event.getGui(), event);
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        // TODO ActionPerformed is no longer fired. Would have to currently check for mouse click on the recipe book button...
        if (event.getGui() instanceof GuiContainer) {
            CraftingGuideButtonFixer.fixMistakes((GuiContainer) event.getGui(), event.getButtonList());
        }
    }

    private static final List<String> tooltipList = Lists.newArrayList();

    private void handleRightClickCrafting() {
        if (rightClickCraftingSlot == -1) {
            return;
        }

        int craftingSlot = rightClickCraftingSlot;
        rightClickCraftingSlot = -1;

        EntityPlayer entityPlayer = Minecraft.getInstance().player;
        if (entityPlayer == null) {
            return;
        }

        PlayerControllerMP playerController = Minecraft.getInstance().playerController;
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

        handleRightClickCrafting();

        if (!CraftingTweaksConfig.CLIENT.hideButtonTooltips.get()) {
            tooltipList.clear();
            for (GuiButton button : event.getGui().buttons) {
                if (button instanceof ITooltipProvider && button.isMouseOver()) {
                    ((ITooltipProvider) button).addInformation(tooltipList);
                    break;
                }
            }
            if (!tooltipList.isEmpty()) {
                event.getGui().drawHoveringText(tooltipList, event.getMouseX(), event.getMouseY());
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
