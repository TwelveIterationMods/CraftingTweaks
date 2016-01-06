package net.blay09.mods.craftingtweaks.client;

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

public class ClientProxy extends CommonProxy {

    private static final int HELLO_TIMEOUT = 20 * 10;
    private int helloTimeout;
    private boolean isServerSide;

    private final ClientProvider clientProvider = new ClientProvider();
    private final KeyBinding keyRotate = new KeyBinding("key.craftingtweaks.rotate", Keyboard.KEY_R, "key.categories.craftingtweaks");
    private final KeyBinding keyBalance = new KeyBinding("key.craftingtweaks.balance", Keyboard.KEY_B, "key.categories.craftingtweaks");
    private final KeyBinding keyClear = new KeyBinding("key.craftingtweaks.clear", Keyboard.KEY_C, "key.categories.craftingtweaks");
    private final KeyBinding keyToggleButtons = new KeyBinding("key.craftingtweaks.toggleButtons", 0, "key.categories.craftingtweaks");
    private KeyBinding keyTransferStack;

    private Slot mouseSlot;
    private boolean ignoreMouseUp;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(keyRotate);
        ClientRegistry.registerKeyBinding(keyBalance);
        ClientRegistry.registerKeyBinding(keyClear);
        ClientRegistry.registerKeyBinding(keyToggleButtons);
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
        if(Keyboard.getEventKeyState()) {
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            if(entityPlayer != null) {
                Container container = entityPlayer.openContainer;
                if (container != null) {
                    TweakProvider provider = CraftingTweaks.instance.getProvider(container);
                    if (provider != null) {
                        CraftingTweaks.ModSupportState config = CraftingTweaks.instance.getModSupportState(provider.getModId());
                        if (config == CraftingTweaks.ModSupportState.ENABLED || config == CraftingTweaks.ModSupportState.HOTKEYS_ONLY) {
                            if (keyRotate.getKeyCode() > 0 && Keyboard.getEventKey() == keyRotate.getKeyCode()) {
                                if(isServerSide) {
                                    NetworkHandler.instance.sendToServer(new MessageRotate(0));
                                } else {
                                    clientProvider.rotateGrid(provider, entityPlayer, container, 0);
                                }
                            } else if (keyClear.getKeyCode() > 0 && Keyboard.getEventKey() == keyClear.getKeyCode()) {
                                if(isServerSide) {
                                    NetworkHandler.instance.sendToServer(new MessageClear(0));
                                } else {
                                    clientProvider.clearGrid(provider, entityPlayer, container, 0);
                                }
                            } else if (keyBalance.getKeyCode() > 0 && Keyboard.getEventKey() == keyBalance.getKeyCode()) {
                                NetworkHandler.instance.sendToServer(new MessageBalance(0));
                            }
                        }
                        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
                        if (guiScreen instanceof GuiContainer) {
                            if (keyToggleButtons.getKeyCode() > 0 && Keyboard.getEventKey() == keyToggleButtons.getKeyCode()) {
                                CraftingTweaks.hideButtons = !CraftingTweaks.hideButtons;
                                if (CraftingTweaks.hideButtons) {
                                    Iterator it = guiScreen.buttonList.iterator();
                                    while (it.hasNext()) {
                                        if (it.next() instanceof GuiTweakButton) {
                                            it.remove();
                                        }
                                    }
                                } else {
                                    initGui((GuiContainer) guiScreen);
                                }
                                CraftingTweaks.saveConfig();
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiMouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if(Mouse.getEventButtonState()) {
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            if (entityPlayer != null) {
                Container container = entityPlayer.openContainer;
                if (container != null) {
                    TweakProvider provider = CraftingTweaks.instance.getProvider(container);
                    if (provider != null) {
                        if (keyTransferStack.getKeyCode() > 0 && Keyboard.isKeyDown(keyTransferStack.getKeyCode())) {
                            if (mouseSlot != null) {
                                if(isServerSide) {
                                    NetworkHandler.instance.sendToServer(new MessageTransferStack(0, mouseSlot.slotNumber));
                                } else {
                                    clientProvider.transferIntoGrid(provider, entityPlayer, container, 0, mouseSlot);
                                    ignoreMouseUp = true;
                                }
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        } else if(ignoreMouseUp) {
            event.setCanceled(true);
            ignoreMouseUp = false;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
        if(entityPlayer != null) {
            if(helloTimeout > 0) {
                helloTimeout--;
                if (helloTimeout <= 0) {
                    entityPlayer.addChatMessage(new ChatComponentText("This server does not have Crafting Tweaks installed. It will be disabled."));
                    isServerSide = false;
                }
            }
        }
    }

    private void initGui(GuiContainer guiContainer) {
        TweakProvider provider = CraftingTweaks.instance.getProvider(guiContainer.inventorySlots);
        if(provider != null) {
            CraftingTweaks.ModSupportState config = CraftingTweaks.instance.getModSupportState(provider.getModId());
            if(config == CraftingTweaks.ModSupportState.ENABLED || config == CraftingTweaks.ModSupportState.BUTTONS_ONLY) {
                provider.initGui(guiContainer, guiContainer.buttonList);
                if(!isServerSide) {
                    for(GuiButton button : guiContainer.buttonList) {
                        if(button instanceof GuiTweakButton) {
                            if(((GuiTweakButton) button).getTweakOption() == GuiTweakButton.TweakOption.Balance) {
                                button.enabled = false;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if(!CraftingTweaks.hideButtons) {
            if (event.gui instanceof GuiContainer) {
                initGui((GuiContainer) event.gui);
            }
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if(event.gui instanceof GuiContainer) {
            mouseSlot = ((GuiContainer) event.gui).getSlotAtPosition(event.mouseX, event.mouseY);
        } else {
            mouseSlot = null;
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent event) {
        if(event.button instanceof GuiTweakButton) {
            EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            Container container = entityPlayer.openContainer;
            TweakProvider provider = CraftingTweaks.instance.getProvider(container);
            switch(((GuiTweakButton) event.button).getTweakOption()) {
                case Rotate:
                    if(isServerSide) {
                        NetworkHandler.instance.sendToServer(new MessageRotate(((GuiTweakButton) event.button).getTweakId()));
                    } else {
                        clientProvider.rotateGrid(provider, entityPlayer, container, ((GuiTweakButton) event.button).getTweakId());
                    }
                    event.setCanceled(true);
                    break;
                case Balance:
                    if(isServerSide) {
                        NetworkHandler.instance.sendToServer(new MessageBalance(((GuiTweakButton) event.button).getTweakId()));
                    }
                    event.setCanceled(true);
                    break;
                case Clear:
                    if(isServerSide) {
                        NetworkHandler.instance.sendToServer(new MessageClear(((GuiTweakButton) event.button).getTweakId()));
                    } else {
                        clientProvider.clearGrid(provider, entityPlayer, container, ((GuiTweakButton) event.button).getTweakId());
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
