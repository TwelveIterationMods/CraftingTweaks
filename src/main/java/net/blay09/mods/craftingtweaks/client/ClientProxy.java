package net.blay09.mods.craftingtweaks.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.blay09.mods.craftingtweaks.CommonProxy;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.GuiClickEvent;
import net.blay09.mods.craftingtweaks.net.*;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class ClientProxy extends CommonProxy {

    private static final int HELLO_TIMEOUT = 20 * 10;
    private int helloTimeout;
    private boolean isEnabled;

    private boolean wasRotated;
    private boolean wasCleared;
    private boolean wasBalanced;
    private boolean wasToggleButtons;

    private final KeyBinding keyRotate = new KeyBinding("key.craftingtweaks.rotate", Keyboard.KEY_R, "key.categories.craftingtweaks");
    private final KeyBinding keyBalance = new KeyBinding("key.craftingtweaks.balance", Keyboard.KEY_B, "key.categories.craftingtweaks");
    private final KeyBinding keyClear = new KeyBinding("key.craftingtweaks.clear", Keyboard.KEY_C, "key.categories.craftingtweaks");
    private final KeyBinding keyToggleButtons = new KeyBinding("key.craftingtweaks.toggleButtons", 0, "key.categories.craftingtweaks");
    private KeyBinding keyTransferStack;

    private Field neiSearchField;
    private Method focused;
    private Slot mouseSlot;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(keyRotate);
        ClientRegistry.registerKeyBinding(keyBalance);
        ClientRegistry.registerKeyBinding(keyClear);
        ClientRegistry.registerKeyBinding(keyToggleButtons);
        keyTransferStack = Minecraft.getMinecraft().gameSettings.keyBindForward;
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        if(Loader.isModLoaded("NotEnoughItems")) {
            try {
                Class neiLayoutManagerClass = Class.forName("codechicken.nei.LayoutManager");
                neiSearchField = neiLayoutManagerClass.getField("searchField");
                Class textFieldClass = Class.forName("codechicken.nei.TextField");
                focused = textFieldClass.getMethod("focused");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void connectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        helloTimeout = HELLO_TIMEOUT;
        isEnabled = false;
    }

    @SubscribeEvent
    public void onGuiClick(GuiClickEvent event) {
        EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
        if(entityPlayer != null) {
            Container container = entityPlayer.openContainer;
            if (container != null) {
                if (!areHotkeysEnabled()) {
                    return;
                }
                TweakProvider provider = CraftingTweaks.instance.getProvider(container);
                if (keyTransferStack.getKeyCode() > 0 && Keyboard.isKeyDown(keyTransferStack.getKeyCode())) {
                    if (mouseSlot != null && provider.areHotkeysEnabled(entityPlayer, container)) {
                        NetworkHandler.instance.sendToServer(new MessageTransferStack(0, mouseSlot.slotNumber));
                        event.setCanceled(true);
                    }
                }
            }
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
                    isEnabled = false;
                }
            }
            if(!isEnabled) {
                return;
            }
            Container container = entityPlayer.openContainer;
            if (container != null) {
                if(!areHotkeysEnabled()) {
                    return;
                }
                TweakProvider provider = CraftingTweaks.instance.getProvider(container);
                if(provider != null) {
                    CraftingTweaks.ModSupportState config = CraftingTweaks.instance.getModSupportState(provider.getModId());
                    if(config == CraftingTweaks.ModSupportState.ENABLED || config == CraftingTweaks.ModSupportState.HOTKEYS_ONLY) {
                        if (keyRotate.getKeyCode() > 0 && Keyboard.isKeyDown(keyRotate.getKeyCode())) {
                            if (!wasRotated && provider.areHotkeysEnabled(entityPlayer, container)) {
                                NetworkHandler.instance.sendToServer(new MessageRotate(0));
                                wasRotated = true;
                            }
                        } else {
                            wasRotated = false;
                        }
                        if (keyClear.getKeyCode() > 0 && Keyboard.isKeyDown(keyClear.getKeyCode())) {
                            if (!wasCleared && provider.areHotkeysEnabled(entityPlayer, container)) {
                                NetworkHandler.instance.sendToServer(new MessageClear(0));
                                wasCleared = true;
                            }
                        } else {
                            wasCleared = false;
                        }
                        if (keyBalance.getKeyCode() > 0 && Keyboard.isKeyDown(keyBalance.getKeyCode())) {
                            if (!wasBalanced && provider.areHotkeysEnabled(entityPlayer, container)) {
                                NetworkHandler.instance.sendToServer(new MessageBalance(0));
                                wasBalanced = true;
                            }
                        } else {
                            wasBalanced = false;
                        }
                    }
                    GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
                    if(guiScreen instanceof GuiContainer) {
                        // Toggle Buttons Key should work regardless of hotkey settings
                        if (keyToggleButtons.getKeyCode() > 0 && Keyboard.isKeyDown(keyToggleButtons.getKeyCode())) {
                            if (!wasToggleButtons && provider.areHotkeysEnabled(entityPlayer, container)) {
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
                                wasToggleButtons = true;
                            }
                        } else {
                            wasToggleButtons = false;
                        }
                    }
                }
            }
        }
    }

    private boolean areHotkeysEnabled() {
        if(neiSearchField != null && focused != null) {
            try {
                Object searchField = neiSearchField.get(null);
                if(searchField != null) {
                    return !(Boolean) focused.invoke(searchField);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void initGui(GuiContainer guiContainer) {
        TweakProvider provider = CraftingTweaks.instance.getProvider(guiContainer.inventorySlots);
        if(provider != null) {
            CraftingTweaks.ModSupportState config = CraftingTweaks.instance.getModSupportState(provider.getModId());
            if(config == CraftingTweaks.ModSupportState.ENABLED || config == CraftingTweaks.ModSupportState.BUTTONS_ONLY) {
                provider.initGui(guiContainer, guiContainer.buttonList);
            }
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if(isEnabled && !CraftingTweaks.hideButtons) {
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
        if(isEnabled && event.button instanceof GuiTweakButton) {
            switch(((GuiTweakButton) event.button).getTweakOption()) {
                case Rotate:
                    NetworkHandler.instance.sendToServer(new MessageRotate(((GuiTweakButton) event.button).getTweakId()));
                    event.setCanceled(true);
                    break;
                case Balance:
                    NetworkHandler.instance.sendToServer(new MessageBalance(((GuiTweakButton) event.button).getTweakId()));
                    event.setCanceled(true);
                    break;
                case Clear:
                    NetworkHandler.instance.sendToServer(new MessageClear(((GuiTweakButton) event.button).getTweakId()));
                    event.setCanceled(true);
                    break;
            }
        }
    }

    @Override
    public void receivedHello(EntityPlayer entityPlayer) {
        super.receivedHello(entityPlayer);
        helloTimeout = 0;
        isEnabled = true;
    }
}
