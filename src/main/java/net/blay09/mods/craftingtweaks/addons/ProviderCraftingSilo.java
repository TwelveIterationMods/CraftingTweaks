package net.blay09.mods.craftingtweaks.addons;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
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
    public void clearGrid(PlayerEntity PlayerEntity, Container container, int id, boolean forced) {
        defaultProvider.clearGrid(this, id, PlayerEntity, container, false, forced);
    }

    @Override
    public void balanceGrid(PlayerEntity PlayerEntity, Container container, int id) {
        defaultProvider.balanceGrid(this, id, PlayerEntity, container);
    }

    @Override
    public void rotateGrid(PlayerEntity PlayerEntity, Container container, int id, boolean counterClockwise) {
        defaultProvider.rotateGrid(this, id, PlayerEntity, container, counterClockwise);
    }

    @Override
    public void spreadGrid(PlayerEntity PlayerEntity, Container container, int id) {
        defaultProvider.spreadGrid(this, id, PlayerEntity, container);
    }

    @Override
    public boolean canTransferFrom(PlayerEntity PlayerEntity, Container container, int id, Slot slot) {
        return defaultProvider.canTransferFrom(PlayerEntity, container, slot);
    }

    @Override
    public boolean transferIntoGrid(PlayerEntity PlayerEntity, Container container, int id, Slot slot) {
        return defaultProvider.transferIntoGrid(this, id, PlayerEntity, container, slot);
    }

    @Override
    public ItemStack putIntoGrid(PlayerEntity PlayerEntity, Container container, int id, ItemStack itemStack, int slotNumber) {
        return defaultProvider.putIntoGrid(this, id, PlayerEntity, container, itemStack, slotNumber);
    }

    @Override
    public IInventory getCraftMatrix(PlayerEntity PlayerEntity, Container container, int id) {
        return container.inventorySlots.get(getCraftingGridStart(PlayerEntity, container, id)).inventory;
    }

    @Override
    public int getCraftingGridStart(PlayerEntity PlayerEntity, Container container, int id) {
        return container.inventorySlots.size() - 9 - 36;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initGui(ContainerScreen<Container> guiContainer, GuiScreenEvent.InitGuiEvent event) {
        Slot firstSlot = guiContainer.getContainer().inventorySlots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.getContainer(), 0));
        int startX = firstSlot.xPos - 19;
        int startY = firstSlot.yPos;
        event.addWidget(CraftingTweaksAPI.createRotateButtonRelative(0, guiContainer, startX, startY));
        event.addWidget(CraftingTweaksAPI.createBalanceButtonRelative(0, guiContainer, startX, startY + 18));
        event.addWidget(CraftingTweaksAPI.createClearButtonRelative(0, guiContainer, startX, startY + 36));
    }

}
