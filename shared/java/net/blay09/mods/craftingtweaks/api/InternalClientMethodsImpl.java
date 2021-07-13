package net.blay09.mods.craftingtweaks.api;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.blay09.mods.craftingtweaks.client.ClientProvider;
import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.blay09.mods.craftingtweaks.client.GuiTweakButton;
import net.blay09.mods.craftingtweaks.network.BalanceMessage;
import net.blay09.mods.craftingtweaks.network.ClearMessage;
import net.blay09.mods.craftingtweaks.network.RotateMessage;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class InternalClientMethodsImpl implements InternalClientMethods {
    @Override
    public Button createBalanceButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y) {
        return new GuiTweakButton(screen, x, y, 48, 0, grid, TweakType.Balance) {
            @Override
            protected void onTweakButtonClicked(Player player, AbstractContainerMenu container, CraftingGrid grid) {
                boolean isShiftDown = Screen.hasShiftDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    BalmNetworking.sendToServer(new BalanceMessage(grid.getId(), isShiftDown));
                } else {
                    ClientProvider clientProvider = CraftingTweaksClient.getClientProvider();
                    if (isShiftDown) {
                        clientProvider.spreadGrid(player, container, grid);
                    } else {
                        clientProvider.balanceGrid(player, container, grid);
                    }
                }
            }
        };
    }

    @Override
    public Button createRotateButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> screen, int x, int y) {
        return new GuiTweakButton(screen, x, y, 16, 0, grid, TweakType.Rotate) {
            @Override
            protected void onTweakButtonClicked(Player player, AbstractContainerMenu container, CraftingGrid grid) {
                boolean isShiftDown = Screen.hasShiftDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    BalmNetworking.sendToServer(new RotateMessage(grid.getId(), isShiftDown));
                } else {
                    ClientProvider clientProvider = CraftingTweaksClient.getClientProvider();
                    clientProvider.rotateGrid(player, container, grid, isShiftDown);
                }
            }
        };
    }

    @Override
    public Button createClearButton(CraftingGrid grid, @Nullable AbstractContainerScreen<?> parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 32, 0, grid, TweakType.Clear) {
            @Override
            protected void onTweakButtonClicked(Player player, AbstractContainerMenu container, CraftingGrid grid) {
                boolean isShiftDown = Screen.hasShiftDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    BalmNetworking.sendToServer(new ClearMessage(grid.getId(), isShiftDown));
                } else {
                    ClientProvider clientProvider = CraftingTweaksClient.getClientProvider();
                    clientProvider.clearGrid(player, container, grid, isShiftDown);
                }
            }
        };
    }
}
