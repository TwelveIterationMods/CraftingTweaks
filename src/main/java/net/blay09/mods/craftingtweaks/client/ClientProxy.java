package net.blay09.mods.craftingtweaks.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.blay09.mods.craftingtweaks.CommonProxy;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.net.MessageClear;
import net.blay09.mods.craftingtweaks.net.MessageRotate;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.blay09.mods.craftingtweaks.provider.TweakProvider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    private boolean wasRotated;
    private boolean wasCleared;
    private GuiButton btnRotate;
    private GuiButton btnClear;

    @Override
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
        if(entityPlayer != null) {
            Container container = entityPlayer.openContainer;
            if (container != null) {
                if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
                    if (!wasRotated) {
                        NetworkHandler.instance.sendToServer(new MessageRotate());
                        wasRotated = true;
                    }
                } else {
                    wasRotated = false;
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                    if (!wasCleared) {
                        NetworkHandler.instance.sendToServer(new MessageClear());
                        wasCleared = true;
                    }
                } else {
                    wasCleared = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent event) {
        if(event.gui instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) event.gui;
            TweakProvider provider = CraftingTweaks.instance.getProvider(guiContainer.inventorySlots);
            if(provider != null) {
                int paddingTop = 16;
                btnRotate = new GuiImageButton(-1, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop, 16, 0);
                event.buttonList.add(btnRotate);
                btnClear = new GuiImageButton(-2, guiContainer.guiLeft - 16, guiContainer.guiTop + paddingTop + 17, 32, 0);
                event.buttonList.add(btnClear);
            }
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent event) {
        if(event.button == btnRotate) {
            NetworkHandler.instance.sendToServer(new MessageRotate());
            event.setCanceled(true);
        } else if(event.button == btnClear) {
            NetworkHandler.instance.sendToServer(new MessageClear());
            event.setCanceled(true);
        }
    }

}
