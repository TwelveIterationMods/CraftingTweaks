package net.blay09.mods.craftingtweaks.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.event.callback.ScreenCallback;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.registry.CraftingTweaksRegistrationData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CraftingTweaksDebugger {

    private static final Logger logger = LoggerFactory.getLogger(CraftingTweaksDebugger.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Rect2i currentMenuLabelRect = new Rect2i(0, 0, 0, 0);

    private static @Nullable Component currentMenuLabel;
    private static @Nullable Slot startDragSlot;
    private static @Nullable Slot endDragSlot;

    public static void initialize() {
        ScreenCallback.Init.After.EVENT.register(screen -> {
            if (!CraftingTweaks.debugMode) {
                return;
            }

            if (CraftingTweaks.debugMode && screen instanceof AbstractContainerScreen<?> containerScreen) {
                var menu = containerScreen.getMenu();
                String modId = getModId(menu);
                printJson(modId, menu.getClass().getName(), 1, 9);
                currentMenuLabel = Component.literal(menu.getClass().getName());
                int labelWidth = Minecraft.getInstance().font.width(currentMenuLabel);
                currentMenuLabelRect.setX(((AbstractContainerScreenAccessor) screen).getLeftPos() + ((AbstractContainerScreenAccessor) screen).getImageWidth() / 2 - labelWidth / 2);
                currentMenuLabelRect.setY(((AbstractContainerScreenAccessor) screen).getTopPos() - 20);
                currentMenuLabelRect.setWidth(labelWidth);
                currentMenuLabelRect.setHeight(16);
            }
        });

        ScreenCallback.MouseRelease.Before.EVENT.register(CraftingTweaksDebugger::onMouseRelease);
        ScreenCallback.MousePress.Before.EVENT.register(CraftingTweaksDebugger::onMouseClick);
        ScreenCallback.Render.AFTER_BACKGROUND.register(CraftingTweaksDebugger::onScreenDrawn);
    }

    public static void onScreenDrawn(Screen screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        if (!CraftingTweaks.debugMode) {
            return;
        }

        if (startDragSlot != null && screen instanceof AbstractContainerScreenAccessor accessor) {
            endDragSlot = accessor.getHoveredSlot();

            // draw highlight on each slot from startDragSlot to endDragSlot
            if (endDragSlot != null) {
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(accessor.getLeftPos(), accessor.getTopPos());
                int startX = startDragSlot.x;
                int startY = startDragSlot.y;
                int endX = endDragSlot.x;
                int endY = endDragSlot.y;
                if (startX > endX) {
                    int tmp = startX;
                    startX = endX;
                    endX = tmp;
                }
                if (startY > endY) {
                    int tmp = startY;
                    startY = endY;
                    endY = tmp;
                }
                for (int x = startX; x <= endX; x++) {
                    for (int y = startY; y <= endY; y++) {
                        guiGraphics.fillGradient(x, y, x + 16, y + 16, 0x1900FF00, 0x1900FF00);
                    }
                }
                guiGraphics.pose().popMatrix();
            }
        }

        if (currentMenuLabel != null) {
            guiGraphics.setTooltipForNextFrame(
                    Minecraft.getInstance().font,
                    List.of(currentMenuLabel),
                    Optional.empty(),
                    currentMenuLabelRect.getX() - 12, currentMenuLabelRect.getY() + 12);
        }
    }

    private static boolean onMouseRelease(Screen screen, double mouseX, double mouseY, int button) {
        if (!CraftingTweaks.debugMode) {
            return false;
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen && button == InputConstants.MOUSE_BUTTON_LEFT) {
            if (startDragSlot != null) {
                var menu = containerScreen.getMenu();
                String modId = getModId(menu);
                endDragSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
                if (endDragSlot != null) {
                    var gridSize = endDragSlot.index - startDragSlot.index + 1;
                    printJson(modId, menu.getClass().getName(), startDragSlot.index, gridSize);
                }
                startDragSlot = null;
                return true;
            }
        }

        return false;
    }

    private static boolean onMouseClick(Screen screen, MouseButtonEvent event) {
        if (!CraftingTweaks.debugMode) {
            return false;
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen && event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            startDragSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
            return startDragSlot != null;
        }

        return false;
    }

    private static String getModId(AbstractContainerMenu menu) {
        try {
            var key = BuiltInRegistries.MENU.getKey(menu.getType());
            return key != null ? key.getNamespace() : "minecraft";
        } catch (UnsupportedOperationException e) {
            return "minecraft";
        }
    }

    private static void printJson(String modId, String containerClass, int gridSlotNumber, int gridSize) {
        CraftingTweaksRegistrationData data = new CraftingTweaksRegistrationData();
        data.setModId(modId);
        data.setContainerClass(containerClass);
        data.setGridSlotNumber(gridSlotNumber);
        data.setGridSize(gridSize);
        logger.info("\n\nExample for Crafting Tweaks datapack: datapacks/mypack/data/mypack/craftingtweaks_compat/{}.json\n\n{}\n\n", modId, gson.toJson(data));
    }
}
