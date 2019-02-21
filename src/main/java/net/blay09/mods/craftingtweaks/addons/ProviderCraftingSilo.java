package net.blay09.mods.craftingtweaks.addons;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;

public class ProviderCraftingSilo implements TweakProvider<Container> {

    private final DefaultProviderV2 defaultProvider = CraftingTweaksAPI.createDefaultProviderV2();

    @Override
    public String getModId() {
        return "storagesilo";
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public void clearGrid(EntityPlayer entityPlayer, Container container, int id, boolean forced) {
        defaultProvider.clearGrid(this, id, entityPlayer, container, false, forced);
    }

    @Override
    public void balanceGrid(EntityPlayer entityPlayer, Container container, int id) {
        defaultProvider.balanceGrid(this, id, entityPlayer, container);
    }

    @Override
    public void rotateGrid(EntityPlayer entityPlayer, Container container, int id, boolean counterClockwise) {
        defaultProvider.rotateGrid(this, id, entityPlayer, container, counterClockwise);
    }

    @Override
    public void spreadGrid(EntityPlayer entityPlayer, Container container, int id) {
        defaultProvider.spreadGrid(this, id, entityPlayer, container);
    }

    @Override
    public boolean canTransferFrom(EntityPlayer entityPlayer, Container container, int id, Slot slot) {
        return defaultProvider.canTransferFrom(entityPlayer, container, slot);
    }

    @Override
    public boolean transferIntoGrid(EntityPlayer entityPlayer, Container container, int id, Slot slot) {
        return defaultProvider.transferIntoGrid(this, id, entityPlayer, container, slot);
    }

    @Override
    public ItemStack putIntoGrid(EntityPlayer entityPlayer, Container container, int id, ItemStack itemStack, int slotNumber) {
        return defaultProvider.putIntoGrid(this, id, entityPlayer, container, itemStack, slotNumber);
    }

    @Override
    public IInventory getCraftMatrix(EntityPlayer entityPlayer, Container container, int id) {
        return container.inventorySlots.get(getCraftingGridStart(entityPlayer, container, id)).inventory;
    }

    @Override
    public int getCraftingGridStart(EntityPlayer entityPlayer, Container container, int id) {
        return container.inventorySlots.size() - 9 - 36;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initGui(GuiContainer guiContainer, GuiScreenEvent.InitGuiEvent event) {
        Slot firstSlot = guiContainer.inventorySlots.inventorySlots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.inventorySlots, 0));
        int startX = firstSlot.xPos - 19;
        int startY = firstSlot.yPos;
        event.addButton(CraftingTweaksAPI.createRotateButtonRelative(0, guiContainer, startX, startY));
        event.addButton(CraftingTweaksAPI.createBalanceButtonRelative(0, guiContainer, startX, startY + 18));
        event.addButton(CraftingTweaksAPI.createClearButtonRelative(0, guiContainer, startX, startY + 36));
    }

}
