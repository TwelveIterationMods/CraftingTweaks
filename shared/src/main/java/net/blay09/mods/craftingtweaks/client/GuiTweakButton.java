package net.blay09.mods.craftingtweaks.client;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.api.TweakType;
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
    private final Tooltip normalTooltip;
    private final Tooltip shiftedTooltip;
    private int lastGuiLeft;
    private int lastGuiTop;

    public GuiTweakButton(@Nullable AbstractContainerScreen<?> screen, int x, int y, int textureX, int textureY, CraftingGrid grid, TweakType tweak) {
        super(x, y, textureX, textureY);
        this.screen = screen;
        if (screen != null) {
            lastGuiLeft = ((AbstractContainerScreenAccessor) screen).getLeftPos();
            lastGuiTop = ((AbstractContainerScreenAccessor) screen).getTopPos();
        }
        this.grid = grid;
        this.tweak = tweak;

        normalTooltip = createTooltip();
        shiftedTooltip = createShiftedTooltip();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        playDownSound(Minecraft.getInstance().getSoundManager());
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            onTweakButtonClicked(player, screen != null ? screen.getMenu() : player.containerMenu, grid);
        }
    }

    protected abstract void onTweakButtonClicked(Player player, AbstractContainerMenu container, CraftingGrid grid);

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        setTooltip(Screen.hasShiftDown() ? shiftedTooltip : normalTooltip);

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

        int oldTexCoordX = texCoordX;
        if (Screen.hasShiftDown()) {
            texCoordX += 48;
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        texCoordX = oldTexCoordX;
    }

    private Tooltip createTooltip() {
        return switch (tweak) {
            case Rotate -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.rotate"));
            case Clear -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.clear"));
            case Balance -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.balance"));
        };
    }

    private Tooltip createShiftedTooltip() {
        return switch (tweak) {
            case Rotate -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.rotate"));
            case Clear -> Tooltip.create(
                    Component.translatable("tooltip.craftingtweaks.forceClear")
                            .append(Component.literal("\n"))
                            .append(Component.translatable("tooltip.craftingtweaks.forceClearInfo").withStyle(ChatFormatting.GRAY)));
            case Balance -> Tooltip.create(Component.translatable("tooltip.craftingtweaks.spread"));
        };
    }

    @Override
    public List<Component> getTooltipComponents() {
        List<Component> tooltip = new ArrayList<>();
        switch (tweak) {
            case Rotate:
                tooltip.add(Component.translatable("tooltip.craftingtweaks.rotate"));
                break;
            case Clear:
                if (Screen.hasShiftDown()) {
                    tooltip.add(Component.translatable("tooltip.craftingtweaks.forceClear"));
                    final MutableComponent forceClearInfoText = Component.translatable("tooltip.craftingtweaks.forceClearInfo");
                    forceClearInfoText.withStyle(ChatFormatting.GRAY);
                    tooltip.add(forceClearInfoText);
                } else {
                    tooltip.add(Component.translatable("tooltip.craftingtweaks.clear"));
                }
                break;
            case Balance:
                if (Screen.hasShiftDown()) {
                    tooltip.add(Component.translatable("tooltip.craftingtweaks.spread"));
                } else {
                    tooltip.add(Component.translatable("tooltip.craftingtweaks.balance"));
                }
                break;
        }
        return tooltip;
    }
}
