package net.blay09.mods.craftingtweaks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.craftingtweaks.CraftingTweaksProviderManager;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.forbic.mixin.AbstractContainerScreenAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

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
    private final AbstractContainerScreen<?> parentGui;
    private int lastGuiLeft;
    private int lastGuiTop;

    public GuiTweakButton(@Nullable AbstractContainerScreen<?> parentGui, int xPosition, int yPosition, int texCoordX, int texCoordY, TweakOption tweakOption, int tweakId) {
        super(xPosition, yPosition, texCoordX, texCoordY);
        this.parentGui = parentGui;
        if (parentGui != null) {
            lastGuiLeft = ((AbstractContainerScreenAccessor) parentGui).getLeftPos();
            lastGuiTop = ((AbstractContainerScreenAccessor) parentGui).getTopPos();
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
        playDownSound(Minecraft.getInstance().getSoundManager());
        Player player = Minecraft.getInstance().player;
        AbstractContainerMenu container = player.containerMenu;
        TweakProvider<AbstractContainerMenu> provider = CraftingTweaksProviderManager.getProvider(container);
        if (provider != null) {
            ClientProvider clientProvider = CraftingTweaksClient.getClientProvider();
            onTweakButtonClicked(player, container, provider, clientProvider);
        }
    }

    protected abstract void onTweakButtonClicked(Player player, AbstractContainerMenu container, TweakProvider<AbstractContainerMenu> provider, ClientProvider clientProvider);

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (parentGui != null) {
            final int guiLeft = ((AbstractContainerScreenAccessor) parentGui).getLeftPos();
            final int guiTop = ((AbstractContainerScreenAccessor) parentGui).getTopPos();
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

    @Override
    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();
        switch (tweakOption) {
            case Rotate:
                tooltip.add(new TranslatableComponent("tooltip.craftingtweaks.rotate"));
                break;
            case Clear:
                if (Screen.hasShiftDown()) {
                    tooltip.add(new TranslatableComponent("tooltip.craftingtweaks.forceClear"));
                    final TranslatableComponent forceClearInfoText = new TranslatableComponent("tooltip.craftingtweaks.forceClear");
                    forceClearInfoText.withStyle(ChatFormatting.GRAY);
                    tooltip.add(forceClearInfoText);
                } else {
                    tooltip.add(new TranslatableComponent("tooltip.craftingtweaks.clear"));
                }
                break;
            case Balance:
                if (Screen.hasShiftDown()) {
                    tooltip.add(new TranslatableComponent("tooltip.craftingtweaks.spread"));
                } else {
                    tooltip.add(new TranslatableComponent("tooltip.craftingtweaks.balance"));
                }
                break;
        }
        return tooltip;
    }
}
