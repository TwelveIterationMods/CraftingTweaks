package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nullable;
import java.util.List;

public abstract class GuiTweakButton extends GuiImageButton implements ITooltipProvider {

    public enum TweakOption {
        Rotate,
        Balance,
        Clear
    }

    private final TweakOption tweakOption;
    private final int tweakId;
    private final ContainerScreen<?> parentGui;
    private int lastGuiLeft;
    private int lastGuiTop;

    public GuiTweakButton(@Nullable ContainerScreen<?> parentGui, int xPosition, int yPosition, int texCoordX, int texCoordY, TweakOption tweakOption, int tweakId) {
        super(xPosition, yPosition, texCoordX, texCoordY);
        this.parentGui = parentGui;
        this.tweakOption = tweakOption;
        this.tweakId = tweakId;
    }

    public TweakOption getTweakOption() {
        return tweakOption;
    }

    public int getTweakId() {
        return tweakId;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int oldX = x;
        int oldY = y;
        // If parentGui is set, we only store the relative position in the button for mods that do hacky things where guiLeft/guiTop constantly changes
        if (parentGui != null) {
            x += lastGuiLeft;
            y += lastGuiTop;
        }
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        x = oldX;
        y = oldY;
        return result;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        playDownSound(Minecraft.getInstance().getSoundHandler());
        PlayerEntity player = Minecraft.getInstance().player;
        Container container = player.openContainer;
        TweakProvider<Container> provider = CraftingTweaksProviderManager.getProvider(container);
        if (provider != null) {
            ClientProvider clientProvider = CraftingTweaks.craftingTweaksClient
                    .map(CraftingTweaksClient::getClientProvider)
                    .orElseThrow(() -> new IllegalStateException("Missing CraftingTweaks ClientProvider."));

            onTweakButtonClicked(player, container, provider, clientProvider);
        }
    }

    protected abstract void onTweakButtonClicked(PlayerEntity player, Container container, TweakProvider<Container> provider, ClientProvider clientProvider);

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int oldX = x;
        int oldY = y;
        // If parentGui is set, we only store the relative position in the button for mods that do hacky things where guiLeft/guiTop constantly changes
        if (parentGui != null) {
            lastGuiLeft = parentGui.getGuiLeft();
            lastGuiTop = parentGui.getGuiTop();
            x += lastGuiLeft;
            y += lastGuiTop;
        }
        int oldTexCoordX = texCoordX;
        if (Screen.hasShiftDown()) {
            texCoordX += 48;
        }

        super.render(mouseX, mouseY, partialTicks);
        texCoordX = oldTexCoordX;
        x = oldX;
        y = oldY;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int oldX = x;
        int oldY = y;
        if (parentGui != null) {
            lastGuiLeft = parentGui.getGuiLeft();
            lastGuiTop = parentGui.getGuiTop();
            x += lastGuiLeft;
            y += lastGuiTop;
        }
        boolean result = super.isMouseOver(mouseX, mouseY);
        x = oldX;
        y = oldY;
        return result;
    }

    @Override
    public void addInformation(List<String> tooltip) {
        switch (tweakOption) {
            case Rotate:
                tooltip.add(I18n.format("tooltip.craftingtweaks.rotate"));
                break;
            case Clear:
                if (Screen.hasShiftDown()) {
                    tooltip.add(I18n.format("tooltip.craftingtweaks.forceClear"));
                    tooltip.add("\u00a77" + I18n.format("tooltip.craftingtweaks.forceClearInfo"));
                } else {
                    tooltip.add(I18n.format("tooltip.craftingtweaks.clear"));
                }
                break;
            case Balance:
                if (Screen.hasShiftDown()) {
                    tooltip.add(I18n.format("tooltip.craftingtweaks.spread"));
                } else {
                    tooltip.add(I18n.format("tooltip.craftingtweaks.balance"));
                }
                break;
        }
    }
}
