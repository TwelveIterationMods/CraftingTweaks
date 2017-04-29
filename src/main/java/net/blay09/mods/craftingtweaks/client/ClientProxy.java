package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.Lists;
import net.blay09.mods.craftingtweaks.CommonProxy;
import net.blay09.mods.craftingtweaks.CompressType;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.net.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    private final ClientProvider clientProvider = new ClientProvider();
    private final KeyBinding keyRotate = new KeyBinding("key.craftingtweaks.rotate", KeyConflictContext.GUI, KeyModifier.NONE, Keyboard.KEY_R, "key.categories.craftingtweaks");
    private final KeyBinding keyBalance = new KeyBinding("key.craftingtweaks.balance", KeyConflictContext.GUI, KeyModifier.NONE, Keyboard.KEY_B, "key.categories.craftingtweaks");
    private final KeyBinding keyClear = new KeyBinding("key.craftingtweaks.clear", KeyConflictContext.GUI, KeyModifier.NONE, Keyboard.KEY_C, "key.categories.craftingtweaks");
    private final KeyBinding keyToggleButtons = new KeyBinding("key.craftingtweaks.toggleButtons", KeyConflictContext.GUI, KeyModifier.NONE, 0, "key.categories.craftingtweaks");
    private final KeyBinding keyCompressOne = new KeyBinding("key.craftingtweaks.compressOne", KeyConflictContext.GUI, KeyModifier.CONTROL, Keyboard.KEY_K, "key.categories.craftingtweaks");
    private final KeyBinding keyCompressStack = new KeyBinding("key.craftingtweaks.compressStack", KeyConflictContext.GUI, KeyModifier.NONE, Keyboard.KEY_K, "key.categories.craftingtweaks");
    private final KeyBinding keyCompressAll = new KeyBinding("key.craftingtweaks.compressAll", KeyConflictContext.GUI, KeyModifier.SHIFT, Keyboard.KEY_K, "key.categories.craftingtweaks");
    private final KeyBinding keyDecompressOne = new KeyBinding("key.craftingtweaks.decompressOne", KeyConflictContext.GUI, KeyModifier.NONE, 0, "key.categories.craftingtweaks");
    private final KeyBinding keyDecompressStack = new KeyBinding("key.craftingtweaks.decompressStack", KeyConflictContext.GUI, KeyModifier.NONE, 0, "key.categories.craftingtweaks");
    private final KeyBinding keyDecompressAll = new KeyBinding("key.craftingtweaks.decompressAll", KeyConflictContext.GUI, KeyModifier.NONE, 0, "key.categories.craftingtweaks");
    private final KeyBinding keyRefillLast = new KeyBinding("key.craftingtweaks.refill_last", KeyConflictContext.GUI, KeyModifier.NONE, Keyboard.KEY_TAB, "key.categories.craftingtweaks");
    private KeyBinding keyTransferStack;

    private boolean ignoreMouseUp;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(keyRotate);
        ClientRegistry.registerKeyBinding(keyBalance);
        ClientRegistry.registerKeyBinding(keyClear);
        ClientRegistry.registerKeyBinding(keyToggleButtons);
        ClientRegistry.registerKeyBinding(keyCompressOne);
        ClientRegistry.registerKeyBinding(keyCompressStack);
        ClientRegistry.registerKeyBinding(keyCompressAll);
        ClientRegistry.registerKeyBinding(keyDecompressOne);
        ClientRegistry.registerKeyBinding(keyDecompressStack);
        ClientRegistry.registerKeyBinding(keyDecompressAll);
        ClientRegistry.registerKeyBinding(keyRefillLast);
        keyTransferStack = Minecraft.getMinecraft().gameSettings.keyBindForward;
    }

    @Override
    public void addScheduledTask(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @SubscribeEvent
    public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardInputEvent.Post event) {
        if (Keyboard.getEventKeyState()) {
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            if (entityPlayer != null) {
                Container container = entityPlayer.openContainer;
                if (container != null) {
                    GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
                    TweakProvider<Container> provider = CraftingTweaks.instance.getProvider(container);
                    int keyCode = Keyboard.getEventKey();
                    CompressType compressType = getCompressType(keyCode);
                    if (provider != null && provider.isValidContainer(container)) {
                        boolean isShiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
                        CraftingTweaks.ModSupportState config = CraftingTweaks.instance.getModSupportState(provider.getModId());
                        if (config == CraftingTweaks.ModSupportState.ENABLED || config == CraftingTweaks.ModSupportState.HOTKEYS_ONLY) {
                            if (keyRotate.getKeyCode() > 0 && keyRotate.getKeyCode() == keyCode) { // Note: these can't be isActiveAndMatches because they behave differently on shift
                                if (CraftingTweaks.isServerSideInstalled) {
                                    NetworkHandler.instance.sendToServer(new MessageRotate(0, isShiftDown));
                                } else {
                                    clientProvider.rotateGrid(provider, entityPlayer, container, 0, isShiftDown);
                                }
                                event.setCanceled(true);
                            } else if (keyClear.getKeyCode() > 0 && keyClear.getKeyCode() ==keyCode) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    NetworkHandler.instance.sendToServer(new MessageClear(0, isShiftDown));
                                } else {
                                    clientProvider.clearGrid(provider, entityPlayer, container, 0, isShiftDown);
                                }
                                event.setCanceled(true);
                            } else if (keyBalance.getKeyCode() > 0 && keyBalance.getKeyCode() == keyCode) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    NetworkHandler.instance.sendToServer(new MessageBalance(0, isShiftDown));
                                } else {
                                    if(isShiftDown) {
                                        clientProvider.spreadGrid(provider, entityPlayer, container, 0);
                                    } else {
                                        clientProvider.balanceGrid(provider, entityPlayer, container, 0);
                                    }
                                }
                                event.setCanceled(true);
                            } else if(keyRefillLast.getKeyCode() > 0 && keyRefillLast.getKeyCode() == keyCode) {
                                if (CraftingTweaks.isServerSideInstalled) {
                                    // TODO send it
                                    clientProvider.refillLastCrafted(provider, entityPlayer, container, 0, isShiftDown);
                                } else {
                                    clientProvider.refillLastCrafted(provider, entityPlayer, container, 0, isShiftDown);
                                }
                                event.setCanceled(true);
                            }
                        }
                        if (guiScreen instanceof GuiContainer) {
                            GuiContainer guiContainer = (GuiContainer) guiScreen;
                            if (compressType != null) {
                                Slot mouseSlot = guiContainer.getSlotUnderMouse();
                                if (mouseSlot != null) { // Forge needs @Nullable
                                    if (CraftingTweaks.isServerSideInstalled) {
                                        NetworkHandler.instance.sendToServer(new MessageCompress(mouseSlot.slotNumber, compressType));
                                    } else {
                                        clientProvider.compress(provider, entityPlayer, container, mouseSlot, compressType);
                                    }
                                    event.setCanceled(true);
                                }
                            } else if (keyToggleButtons.getKeyCode() > 0 && keyCode == keyToggleButtons.getKeyCode()) {
                                CraftingTweaks.hideButtons = !CraftingTweaks.hideButtons;
                                if (CraftingTweaks.hideButtons) {
                                    guiScreen.buttonList.removeIf(o -> o instanceof GuiTweakButton);
                                } else {
                                    initGui(guiContainer);
                                }
                                CraftingTweaks.saveConfig();
                                event.setCanceled(true);
                            }
                        }
                    } else if (CraftingTweaks.isServerSideInstalled && guiScreen instanceof GuiContainer) {
                        GuiContainer guiContainer = (GuiContainer) guiScreen;
                        if (compressType != null) {
                            Slot mouseSlot = guiContainer.getSlotUnderMouse();
                            if (mouseSlot != null) {
                                NetworkHandler.instance.sendToServer(new MessageCompress(mouseSlot.slotNumber, compressType));
                            }
                        }
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @Nullable
    private CompressType getCompressType(int eventKey) {
        if(isActiveAndMatches(keyCompressOne, eventKey)) {
            return CompressType.COMPRESS_ONE;
        } else if(isActiveAndMatches(keyCompressStack, eventKey)) {
            return CompressType.COMPRESS_STACK;
        } else if(isActiveAndMatches(keyCompressAll, eventKey)) {
            return CompressType.COMPRESS_ALL;
        } else if(isActiveAndMatches(keyDecompressOne, eventKey)) {
            return CompressType.DECOMPRESS_ONE;
        } else if(isActiveAndMatches(keyDecompressStack, eventKey)) {
            return CompressType.DECOMPRESS_STACK;
        } else if(isActiveAndMatches(keyDecompressAll, eventKey)) {
            return CompressType.DECOMPRESS_ALL;
        }
        return null;
    }

    private boolean isActiveAndMatches(KeyBinding keyBinding, int eventKey) {
        if(keyBinding.getKeyModifier() == KeyModifier.NONE) {
            if(KeyModifier.SHIFT.isActive(keyBinding.getKeyConflictContext()) || KeyModifier.CONTROL.isActive(keyBinding.getKeyConflictContext()) || KeyModifier.ALT.isActive(keyBinding.getKeyConflictContext())) {
                return false;
            }
        }
        return keyBinding.isActiveAndMatches(eventKey);
    }

    @SubscribeEvent
    public void onGuiMouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Mouse.getEventButtonState()) {
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            if (entityPlayer != null) {
                Container container = entityPlayer.openContainer;
                if (container != null) {
                    Slot mouseSlot = event.getGui() instanceof GuiContainer ? ((GuiContainer) event.getGui()).getSlotUnderMouse() : null;
                    TweakProvider<Container> provider = CraftingTweaks.instance.getProvider(container);
                    if (provider != null && provider.isValidContainer(container)) {
                        if (keyTransferStack.getKeyCode() > 0 && Keyboard.isKeyDown(keyTransferStack.getKeyCode())) {
                            if (mouseSlot != null && mouseSlot.getHasStack()) {
                                List<Slot> transferSlots = Lists.newArrayList();
                                transferSlots.add(mouseSlot);
                                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
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
                                        NetworkHandler.instance.sendToServer(new MessageTransferStack(0, slot.slotNumber));
                                    }
                                } else {
                                    for (Slot slot : transferSlots) {
                                        clientProvider.transferIntoGrid(provider, entityPlayer, container, 0, slot);
                                    }
                                    ignoreMouseUp = true;
                                }
                                event.setCanceled(true);
                            }
                        } else if(CraftingTweaks.rightClickCraftsStack && Mouse.getEventButton() == 1 && mouseSlot instanceof SlotCrafting) {
                            PlayerControllerMP playerController = Minecraft.getMinecraft().playerController;
                            if(playerController == null) {
                                return;
                            }
                            ItemStack mouseStack = entityPlayer.inventory.getItemStack();
                            int maxTries = 64;
                            while(maxTries > 0 && mouseSlot.getHasStack() && (mouseStack.isEmpty() || mouseStack.getCount() < mouseStack.getMaxStackSize())) {
                                playerController.windowClick(container.windowId, mouseSlot.slotNumber, 0, ClickType.PICKUP, entityPlayer);
                                mouseStack = entityPlayer.inventory.getItemStack();
                                maxTries--;
                            }
                            event.setCanceled(true);
                            ignoreMouseUp = true;
                        }
                    }
                }
            }
        } else if (ignoreMouseUp) {
            event.setCanceled(true);
            ignoreMouseUp = false;
        }
    }

    private void initGui(GuiContainer guiContainer) {
        TweakProvider provider = CraftingTweaks.instance.getProvider(guiContainer.inventorySlots);
        if (provider != null) {
            CraftingTweaks.ModSupportState config = CraftingTweaks.instance.getModSupportState(provider.getModId());
            if (config == CraftingTweaks.ModSupportState.ENABLED || config == CraftingTweaks.ModSupportState.BUTTONS_ONLY) {
                if (provider.isValidContainer(guiContainer.inventorySlots)) {
                    provider.initGui(guiContainer, guiContainer.buttonList);
                }
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!CraftingTweaks.hideButtons) {
            if (event.getGui() instanceof GuiContainer) {
                initGui((GuiContainer) event.getGui());
            }
        }
    }

    private static final List<String> tooltipList = Lists.newArrayList();

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if(event.getGui() == null) { // WAILA somehow breaks the DrawScreenEvent, so we have to null-check here. o_o
            return;
        }
        if(!CraftingTweaks.hideButtonTooltips) {
            tooltipList.clear();
            for (GuiButton button : event.getGui().buttonList) {
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
    @SuppressWarnings("unused")
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.getButton() instanceof GuiTweakButton) {
            event.getButton().playPressSound(Minecraft.getMinecraft().getSoundHandler());
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            Container container = entityPlayer.openContainer;
            TweakProvider<Container> provider = CraftingTweaks.instance.getProvider(container);
            boolean isShiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
            switch (((GuiTweakButton) event.getButton()).getTweakOption()) {
                case Rotate:
                    if (CraftingTweaks.isServerSideInstalled) {
                        NetworkHandler.instance.sendToServer(new MessageRotate(((GuiTweakButton) event.getButton()).getTweakId(), isShiftDown));
                    } else {
                        clientProvider.rotateGrid(provider, entityPlayer, container, ((GuiTweakButton) event.getButton()).getTweakId(), isShiftDown);
                    }
                    event.setCanceled(true);
                    break;
                case Balance:
                    if (CraftingTweaks.isServerSideInstalled) {
                        NetworkHandler.instance.sendToServer(new MessageBalance(((GuiTweakButton) event.getButton()).getTweakId(), isShiftDown));
                    } else {
                        if(isShiftDown) {
                            clientProvider.spreadGrid(provider, entityPlayer, container, ((GuiTweakButton) event.getButton()).getTweakId());
                        } else {
                            clientProvider.balanceGrid(provider, entityPlayer, container, ((GuiTweakButton) event.getButton()).getTweakId());
                        }
                    }
                    event.setCanceled(true);
                    break;
                case Clear:
                    if (CraftingTweaks.isServerSideInstalled) {
                        NetworkHandler.instance.sendToServer(new MessageClear(((GuiTweakButton) event.getButton()).getTweakId(), isShiftDown));
                    } else {
                        clientProvider.clearGrid(provider, entityPlayer, container, ((GuiTweakButton) event.getButton()).getTweakId(), isShiftDown);
                    }
                    event.setCanceled(true);
                    break;
            }
        }
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        clientProvider.onItemCrafted(event.craftMatrix);
    }
}
