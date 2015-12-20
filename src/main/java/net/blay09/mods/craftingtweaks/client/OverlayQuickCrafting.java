package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class OverlayQuickCrafting {

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(player != null) {
            TweakProvider tweakProvider = CraftingTweaks.instance.getProvider(player.openContainer);
            if(event.gui instanceof GuiContainer) {
                GuiContainer guiContainer = (GuiContainer) event.gui;
                List inventorySlots = guiContainer.inventorySlots.inventorySlots;
                for(Object obj : inventorySlots) {
                    Slot slot = (Slot) obj;
                    int slotX = guiContainer.guiLeft + slot.xDisplayPosition;
                    int slotY = guiContainer.guiTop + slot.yDisplayPosition;
                    if(event.mouseX >= slotX && event.mouseX < slotX + 16 && event.mouseY >= slotY && event.mouseY < slotY + 16) {
                        if(slot.inventory == player.inventory) {

                        }
                    }
                }
            }
        }
    }


}
