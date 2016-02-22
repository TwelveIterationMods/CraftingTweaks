package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.Lists;
import net.blay09.mods.craftingtweaks.CommonProxy;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.net.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Iterator;
import java.util.List;

public class ClientProxy extends CommonProxy {

    private static final int HELLO_TIMEOUT = 20 * 10;
    private int helloTimeout;
    private boolean isServerSide;

    private final ClientProvider clientProvider = new ClientProvider();
    private final KeyBinding keyRotate = new KeyBinding("key.craftingtweaks.rotate", Keyboard.KEY_R, "key.categories.craftingtweaks");
    private final KeyBinding keyBalance = new KeyBinding("key.craftingtweaks.balance", Keyboard.KEY_B, "key.categories.craftingtweaks");
    private final KeyBinding keyClear = new KeyBinding("key.craftingtweaks.clear", Keyboard.KEY_C, "key.categories.craftingtweaks");
    private final KeyBinding keyToggleButtons = new KeyBinding("key.craftingtweaks.toggleButtons", 0, "key.categories.craftingtweaks");
    private final KeyBinding keyCompress = new KeyBinding("key.craftingtweaks.compress", Keyboard.KEY_K, "key.categories.craftingtweaks");
    private final KeyBinding keyDecompress = new KeyBinding("key.craftingtweaks.decompress", 0, "key.categories.craftingtweaks");
    private KeyBinding keyTransferStack;

    private boolean ignoreMouseUp;

    private GuiRoundMenu roundMenu;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(keyRotate);
        ClientRegistry.registerKeyBinding(keyBalance);
        ClientRegistry.registerKeyBinding(keyClear);
        ClientRegistry.registerKeyBinding(keyToggleButtons);
        ClientRegistry.registerKeyBinding(keyCompress);
        ClientRegistry.registerKeyBinding(keyDecompress);
        keyTransferStack = Minecraft.getMinecraft().gameSettings.keyBindForward;
    }

    @Override
    public void addScheduledTask(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @SubscribeEvent
    public void connectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        helloTimeout = HELLO_TIMEOUT;
        isServerSide = false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (Keyboard.getEventKeyState()) {
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            if (entityPlayer != null) {
                Container container = entityPlayer.openContainer;
                if (container != null) {
                    GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
                    TweakProvider provider = CraftingTweaks.instance.getProvider(container);
                    if (provider != null) {
                        boolean isShiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
                        CraftingTweaks.ModSupportState config = CraftingTweaks.instance.getModSupportState(provider.getModId());
                        if (config == CraftingTweaks.ModSupportState.ENABLED || config == CraftingTweaks.ModSupportState.HOTKEYS_ONLY) {
                            if (keyRotate.getKeyCode() > 0 && Keyboard.getEventKey() == keyRotate.getKeyCode()) {
                                if (isServerSide) {
                                    NetworkHandler.instance.sendToServer(new MessageRotate(0, isShiftDown));
                                } else {
                                    clientProvider.rotateGrid(provider, entityPlayer, container, 0, isShiftDown);
                                }
                            } else if (keyClear.getKeyCode() > 0 && Keyboard.getEventKey() == keyClear.getKeyCode()) {
                                if (isServerSide) {
                                    NetworkHandler.instance.sendToServer(new MessageClear(0, isShiftDown));
                                } else {
                                    clientProvider.clearGrid(provider, entityPlayer, container, 0, isShiftDown);
                                }
                            } else if (keyBalance.getKeyCode() > 0 && Keyboard.getEventKey() == keyBalance.getKeyCode()) {
                                if (isServerSide) {
                                    NetworkHandler.instance.sendToServer(new MessageBalance(0, isShiftDown));
                                } else {
                                    if(isShiftDown) {
                                        clientProvider.spreadGrid(provider, entityPlayer, container, 0);
                                    } else {
                                        clientProvider.balanceGrid(provider, entityPlayer, container, 0);
                                    }
                                }
                            }
                        }
                        if (guiScreen instanceof GuiContainer) {
                            GuiContainer guiContainer = (GuiContainer) guiScreen;
                            if (keyCompress.getKeyCode() > 0 && Keyboard.getEventKey() == keyCompress.getKeyCode()) {
                                Slot mouseSlot = guiContainer.getSlotUnderMouse();
                                if (mouseSlot != null) {
                                    boolean isDecompress = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
                                    if (isServerSide) {
                                        NetworkHandler.instance.sendToServer(new MessageCompress(mouseSlot.slotNumber, isDecompress, isShiftDown));
                                    } else if (isDecompress) {
                                        clientProvider.decompress(provider, entityPlayer, container, mouseSlot, isShiftDown);
                                    } else {
                                        clientProvider.compress(provider, entityPlayer, container, mouseSlot, isShiftDown);
                                    }
                                }
                            } else if (keyDecompress.getKeyCode() > 0 && Keyboard.getEventKey() == keyDecompress.getKeyCode()) {
                                Slot mouseSlot = guiContainer.getSlotUnderMouse();
                                if (mouseSlot != null) {
                                    if (isServerSide) {
                                        NetworkHandler.instance.sendToServer(new MessageCompress(mouseSlot.slotNumber, true, isShiftDown));
                                    } else {
                                        clientProvider.decompress(provider, entityPlayer, container, mouseSlot, isShiftDown);
                                    }
                                }
                            } else if (keyToggleButtons.getKeyCode() > 0 && Keyboard.getEventKey() == keyToggleButtons.getKeyCode()) {
                                CraftingTweaks.hideButtons = !CraftingTweaks.hideButtons;
                                if (CraftingTweaks.hideButtons) {
                                    Iterator it = guiScreen.buttonList.iterator();
                                    while (it.hasNext()) {
                                        if (it.next() instanceof GuiTweakButton) {
                                            it.remove();
                                        }
                                    }
                                } else {
                                    initGui(guiContainer);
                                }
                                CraftingTweaks.saveConfig();
                            }
                        }
                    }
                    if (isServerSide && provider == null && guiScreen instanceof GuiContainer) {
                        GuiContainer guiContainer = (GuiContainer) guiScreen;
                        boolean isShiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
                        if (keyCompress.getKeyCode() > 0 && Keyboard.getEventKey() == keyCompress.getKeyCode()) {
                            boolean isDecompress = Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
                            Slot mouseSlot = guiContainer.getSlotUnderMouse();
                            if (mouseSlot != null) {
                                NetworkHandler.instance.sendToServer(new MessageCompress(mouseSlot.slotNumber, isDecompress, isShiftDown));
                            }
                        } else if (keyDecompress.getKeyCode() > 0 && Keyboard.getEventKey() == keyDecompress.getKeyCode()) {
                            Slot mouseSlot = guiContainer.getSlotUnderMouse();
                            if (mouseSlot != null) {
                                NetworkHandler.instance.sendToServer(new MessageCompress(mouseSlot.slotNumber, true, isShiftDown));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiMouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Mouse.getEventButtonState()) {
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            if (entityPlayer != null) {
                Container container = entityPlayer.openContainer;
                if (container != null) {
                    Slot mouseSlot = event.gui instanceof GuiContainer ? ((GuiContainer) event.gui).getSlotUnderMouse() : null;
                    TweakProvider provider = CraftingTweaks.instance.getProvider(container);
                    if (provider != null) {
                        if (keyTransferStack.getKeyCode() > 0 && Keyboard.isKeyDown(keyTransferStack.getKeyCode())) {
                            if (mouseSlot != null) {
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
                                if (isServerSide) {
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
                        }
                    }
                }
            }
        } else if (ignoreMouseUp) {
            event.setCanceled(true);
            ignoreMouseUp = false;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
        if (entityPlayer != null) {
            if (helloTimeout > 0) {
                helloTimeout--;
                if (helloTimeout <= 0) {
                    entityPlayer.addChatMessage(new ChatComponentText("This server does not have Crafting Tweaks installed. Functionality may be limited."));
                    isServerSide = false;
                }
            }
        }
    }

    private void initGui(GuiContainer guiContainer) {
        TweakProvider provider = CraftingTweaks.instance.getProvider(guiContainer.inventorySlots);
        if (provider != null) {
            CraftingTweaks.ModSupportState config = CraftingTweaks.instance.getModSupportState(provider.getModId());
            if (config == CraftingTweaks.ModSupportState.ENABLED || config == CraftingTweaks.ModSupportState.BUTTONS_ONLY) {
                provider.initGui(guiContainer, guiContainer.buttonList);
            }
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!CraftingTweaks.hideButtons) {
            if (event.gui instanceof GuiContainer) {
                initGui((GuiContainer) event.gui);
            }
        }
    }

    private static final List<String> tooltipList = Lists.newArrayList();

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        // Testing Code
//        if(event.gui instanceof GuiContainer && roundMenu == null) {
//            GuiContainer guiContainer =  (GuiContainer) event.gui;
//            if(guiContainer.getSlotUnderMouse() != null) {
//                roundMenu = new GuiRoundMenu(guiContainer.guiLeft + guiContainer.getSlotUnderMouse().xDisplayPosition + 8, guiContainer.guiTop + guiContainer.getSlotUnderMouse().yDisplayPosition + 8);
//            } else {
//                roundMenu = null;
//            }
//        }
//        if (roundMenu != null) {
//            roundMenu.drawMenu(event.gui.mc, event.mouseX, event.mouseY);
//        }
        if(!CraftingTweaks.hideButtonTooltips) {
            tooltipList.clear();
            for (GuiButton button : event.gui.buttonList) {
                if (button instanceof ITooltipProvider && button.isMouseOver()) {
                    ((ITooltipProvider) button).addInformation(tooltipList);
                    break;
                }
            }
            if (!tooltipList.isEmpty()) {
                event.gui.drawHoveringText(tooltipList, event.mouseX, event.mouseY);
            }
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.button instanceof GuiTweakButton) {
            event.button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            Container container = entityPlayer.openContainer;
            TweakProvider provider = CraftingTweaks.instance.getProvider(container);
            boolean isShiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
            switch (((GuiTweakButton) event.button).getTweakOption()) {
                case Rotate:
                    if (isServerSide) {
                        NetworkHandler.instance.sendToServer(new MessageRotate(((GuiTweakButton) event.button).getTweakId(), isShiftDown));
                    } else {
                        clientProvider.rotateGrid(provider, entityPlayer, container, ((GuiTweakButton) event.button).getTweakId(), isShiftDown);
                    }
                    event.setCanceled(true);
                    break;
                case Balance:
                    if (isServerSide) {
                        NetworkHandler.instance.sendToServer(new MessageBalance(((GuiTweakButton) event.button).getTweakId(), isShiftDown));
                    } else {
                        if(isShiftDown) {
                            clientProvider.spreadGrid(provider, entityPlayer, container, ((GuiTweakButton) event.button).getTweakId());
                        } else {
                            clientProvider.balanceGrid(provider, entityPlayer, container, ((GuiTweakButton) event.button).getTweakId());
                        }
                    }
                    event.setCanceled(true);
                    break;
                case Clear:
                    if (isServerSide) {
                        NetworkHandler.instance.sendToServer(new MessageClear(((GuiTweakButton) event.button).getTweakId(), isShiftDown));
                    } else {
                        clientProvider.clearGrid(provider, entityPlayer, container, ((GuiTweakButton) event.button).getTweakId(), isShiftDown);
                    }
                    event.setCanceled(true);
                    break;
            }
        }
    }

    @Override
    public void receivedHello(EntityPlayer entityPlayer) {
        super.receivedHello(entityPlayer);
        helloTimeout = 0;
        isServerSide = true;
    }
}
