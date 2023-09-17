package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.craftingtweaks.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiTweakButton extends GuiImageButton implements ITooltipProvider {

    private final AbstractContainerScreen<?> screen;
    private final CraftingGrid grid;
    private final TweakType tweak;
    private final TweakType altTweak;
    private final Tooltip normalTooltip;
    private final Tooltip altTooltip;
    private final ButtonProperties normalProperties;
    private final ButtonProperties altProperties;
    private int lastGuiLeft;
    private int lastGuiTop;

    public GuiTweakButton(@Nullable AbstractContainerScreen<?> screen, int x, int y, ButtonStyle style, CraftingGrid grid, TweakType tweak, TweakType altTweak) {
        super(x, y, style.getTweak(tweak));
        this.screen = screen;
        if (screen != null) {
            lastGuiLeft = ((AbstractContainerScreenAccessor) screen).getLeftPos();
            lastGuiTop = ((AbstractContainerScreenAccessor) screen).getTopPos();
        }
        this.grid = grid;
        this.tweak = tweak;
        this.altTweak = altTweak;

        normalTooltip = createTooltip(tweak);
        altTooltip = createTooltip(altTweak);
        normalProperties = properties;
        altProperties = style.getTweak(altTweak);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        playDownSound(Minecraft.getInstance().getSoundManager());
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            onTweakButtonClicked(player, screen != null ? screen.getMenu() : player.containerMenu, grid, Screen.hasShiftDown() ? altTweak : tweak);
        }
    }

    protected abstract void onTweakButtonClicked(Player player, AbstractContainerMenu container, CraftingGrid grid, TweakType tweak);

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        setTooltip(Screen.hasShiftDown() ? altTooltip : normalTooltip);

        if (screen != null) {
            final int guiLeft = ((AbstractContainerScreenAccessor) screen).getLeftPos();
            final int guiTop = ((AbstractContainerScreenAccessor) screen).getTopPos();
            if (guiLeft != lastGuiLeft || guiTop != lastGuiTop) {
                setX(getX() + guiLeft - lastGuiLeft);
                setY(getY() + guiTop - lastGuiTop);
            }
            lastGuiLeft = guiLeft;
            lastGuiTop = guiTop;
        }

        if (Screen.hasShiftDown()) {
            properties = altProperties;
        } else {
            properties = normalProperties;
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private Tooltip createTooltip(TweakType tweak) {
        return switch (tweak) {
            case Rotate, RotateCounterClockwise -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.rotate"));
            case Clear -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.clear"));
            case Balance -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.balance"));
            case ForceClear -> Tooltip.create(
                    Component.translatable("tooltip.craftingtweaks.forceClear")
                            .append(Component.literal("\n"))
                            .append(Component.translatable("tooltip.craftingtweaks.forceClearInfo").withStyle(ChatFormatting.GRAY)));
            case Spread -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.spread"));
        };
    }

    @Override
    public List<Component> getTooltipComponents() {
        List<Component> tooltip = new ArrayList<>();
        switch (tweak) {
            case Rotate, RotateCounterClockwise -> tooltip.add(Component.translatable("tooltip.craftingtweaks.rotate"));
            case Clear -> tooltip.add(Component.translatable("tooltip.craftingtweaks.clear"));
            case ForceClear -> {
                tooltip.add(Component.translatable("tooltip.craftingtweaks.forceClear"));
                final MutableComponent forceClearInfoText = Component.translatable("tooltip.craftingtweaks.forceClearInfo");
                forceClearInfoText.withStyle(ChatFormatting.GRAY);
                tooltip.add(forceClearInfoText);
            }
            case Balance -> tooltip.add(Component.translatable("tooltip.craftingtweaks.balance"));
            case Spread -> tooltip.add(Component.translatable("tooltip.craftingtweaks.spread"));
        }
        return tooltip;
    }
}
