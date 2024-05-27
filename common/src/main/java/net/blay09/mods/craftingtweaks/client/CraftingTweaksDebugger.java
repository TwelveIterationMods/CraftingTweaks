package net.blay09.mods.craftingtweaks.client;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.client.screen.ContainerScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenInitEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenMouseEvent;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.registry.CraftingTweaksRegistrationData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CraftingTweaksDebugger {

    private static final Logger logger = LoggerFactory.getLogger(CraftingTweaksDebugger.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Rect2i currentMenuLabelRect = new Rect2i(0, 0, 0, 0);

    private static Component currentMenuLabel;
    private static Slot startDragSlot;
    private static Slot endDragSlot;

    public static void initialize() {
        Balm.getEvents().onEvent(ScreenInitEvent.Post.class, event -> {
            if (!CraftingTweaks.debugMode) {
                return;
            }

            var screen = event.getScreen();
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

        Balm.getEvents().onEvent(ScreenMouseEvent.Release.Pre.class, CraftingTweaksDebugger::onMouseRelease);
        Balm.getEvents().onEvent(ScreenMouseEvent.Click.Pre.class, CraftingTweaksDebugger::onMouseClick);
        Balm.getEvents().onEvent(ContainerScreenDrawEvent.Background.class, CraftingTweaksDebugger::onScreenDrawn);
    }

    public static void onScreenDrawn(ContainerScreenDrawEvent.Background event) {
        if (!CraftingTweaks.debugMode) {
            return;
        }

        var screen = event.getScreen();
        var graphics = event.getGuiGraphics();

        if (startDragSlot != null && screen instanceof AbstractContainerScreenAccessor accessor) {
            endDragSlot = accessor.getHoveredSlot();

            // draw highlight on each slot from startDragSlot to endDragSlot
            if (endDragSlot != null) {
                graphics.pose().pushPose();
                graphics.pose().translate(accessor.getLeftPos(), accessor.getTopPos(), 0);
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
                        graphics.setColor(1f, 1f, 1f, 0.1f);
                        graphics.fillGradient(x, y, x + 16, y + 16, 0x1000FF00, 0x1000FF00);
                        graphics.setColor(1f, 1f, 1f, 1f);
                    }
                }
                graphics.pose().popPose();
            }
        }

        if (currentMenuLabel != null) {
            graphics.renderTooltip(
                    Minecraft.getInstance().font,
                    Lists.newArrayList(currentMenuLabel),
                    Optional.empty(),
                    currentMenuLabelRect.getX() - 12, currentMenuLabelRect.getY() + 12);
        }
    }

    private static void onMouseRelease(ScreenMouseEvent.Release.Pre event) {
        if (!CraftingTweaks.debugMode) {
            return;
        }

        if (event.getScreen() instanceof AbstractContainerScreen<?> screen && event.getButton() == 0) {
            if (startDragSlot != null) {
                var menu = screen.getMenu();
                String modId = getModId(menu);
                endDragSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
                if (endDragSlot != null) {
                    var gridSize = endDragSlot.index - startDragSlot.index + 1;
                    printJson(modId, menu.getClass().getName(), startDragSlot.index, gridSize);
                }
                startDragSlot = null;
                event.setCanceled(true);
            }
        }
    }

    private static void onMouseClick(ScreenMouseEvent.Click.Pre event) {
        if (!CraftingTweaks.debugMode) {
            return;
        }

        if (event.getScreen() instanceof AbstractContainerScreen<?> screen && event.getButton() == 0) {
            startDragSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
            if (startDragSlot != null) {
                event.setCanceled(true);
            }
        }
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
