package net.blay09.mods.craftingtweaks.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
        if (parentGui != null) {
            lastGuiLeft = parentGui.getGuiLeft();
            lastGuiTop = parentGui.getGuiTop();
        }
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (parentGui != null) {
            final int guiLeft = parentGui.getGuiLeft();
            final int guiTop = parentGui.getGuiTop();
            if (guiLeft != lastGuiLeft || guiTop != lastGuiTop) {
                x += guiLeft - lastGuiLeft;
                y += guiTop - lastGuiTop;
            }
            lastGuiLeft = guiLeft;
            lastGuiTop = guiTop;
        }

        int oldTexCoordX = texCoordX;
        if (Screen.hasShiftDown()) {
            texCoordX += 48;
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        texCoordX = oldTexCoordX;
    }

    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();
        switch (tweakOption) {
            case Rotate:
                tooltip.add(new TranslationTextComponent("tooltip.craftingtweaks.rotate"));
                break;
            case Clear:
                if (Screen.hasShiftDown()) {
                    tooltip.add(new TranslationTextComponent("tooltip.craftingtweaks.forceClear"));
                    final TranslationTextComponent forceClearInfoText = new TranslationTextComponent("tooltip.craftingtweaks.forceClear");
                    forceClearInfoText.mergeStyle(TextFormatting.GRAY);
                    tooltip.add(forceClearInfoText);
                } else {
                    tooltip.add(new TranslationTextComponent("tooltip.craftingtweaks.clear"));
                }
                break;
            case Balance:
                if (Screen.hasShiftDown()) {
                    tooltip.add(new TranslationTextComponent("tooltip.craftingtweaks.spread"));
                } else {
                    tooltip.add(new TranslationTextComponent("tooltip.craftingtweaks.balance"));
                }
                break;
        }
        return tooltip;
    }
}
