package net.blay09.mods.craftingtweaks.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.blay09.mods.craftingtweaks.CommonProxy;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.net.MessageBalance;
import net.blay09.mods.craftingtweaks.net.MessageClear;
import net.blay09.mods.craftingtweaks.net.MessageRotate;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClientProxy extends CommonProxy {

    private boolean wasRotated;
    private boolean wasCleared;
    private boolean wasBalanced;

    private Field neiSearchField;
    private Method focused;

    @Override
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
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
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
        if(entityPlayer != null) {
            Container container = entityPlayer.openContainer;
            if (container != null) {
                if(!areHotkeysEnabled()) {
                    return;
                }
                TweakProvider provider = CraftingTweaks.instance.getProvider(container);
                if(provider != null) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
                        if(!wasRotated && provider.areHotkeysEnabled(entityPlayer, container)) {
                            NetworkHandler.instance.sendToServer(new MessageRotate(0));
                            wasRotated = true;
                        }
                    } else {
                        wasRotated = false;
                    }
                    if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                        if(!wasCleared && provider.areHotkeysEnabled(entityPlayer, container)) {
                            NetworkHandler.instance.sendToServer(new MessageClear(0));
                            wasCleared = true;
                        }
                    } else {
                        wasCleared = false;
                    }
                    if (Keyboard.isKeyDown(Keyboard.KEY_B) ) {
                        if(!wasBalanced && provider.areHotkeysEnabled(entityPlayer, container)) {
                            NetworkHandler.instance.sendToServer(new MessageBalance(0));
                            wasBalanced = true;
                        }
                    } else {
                        wasBalanced = false;
                    }
                }
            }
        }
    }

    public boolean areHotkeysEnabled() {
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

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if(event.gui instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) event.gui;
            TweakProvider provider = CraftingTweaks.instance.getProvider(guiContainer.inventorySlots);
            if(provider != null) {
                provider.initGui(guiContainer, event.buttonList);
            }
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent event) {
        if(event.button instanceof GuiTweakButton) {
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

}
