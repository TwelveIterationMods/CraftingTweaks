package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.InternalMethods;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.client.ClientProvider;
import net.blay09.mods.craftingtweaks.client.GuiTweakButton;
import net.blay09.mods.craftingtweaks.network.BalanceMessage;
import net.blay09.mods.craftingtweaks.network.ClearMessage;
import net.blay09.mods.craftingtweaks.network.RotateMessage;
import net.blay09.mods.balm.network.BalmNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public <T extends AbstractContainerMenu> void registerProvider(Class<T> containerClass, TweakProvider<T> provider) {
        CraftingTweaksProviderManager.registerProvider(containerClass, provider);
    }

    @Override
    public <T extends AbstractContainerMenu> SimpleTweakProvider<T> registerSimpleProvider(String modId, Class<T> containerClass) {
        SimpleTweakProvider<T> simpleTweakProvider = new SimpleTweakProviderImpl<>(modId);
        CraftingTweaksProviderManager.registerProvider(containerClass, simpleTweakProvider);
        return simpleTweakProvider;
    }

    @Override
    public DefaultProviderV2 createDefaultProviderV2() {
        return new DefaultProviderV2Impl();
    }

    @Override
    public Button createBalanceButton(int id, @Nullable AbstractContainerScreen<?> parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 48, 0, GuiTweakButton.TweakOption.Balance, id) {
            @Override
            protected void onTweakButtonClicked(Player player, AbstractContainerMenu container, TweakProvider<AbstractContainerMenu> provider, ClientProvider clientProvider) {
                boolean isShiftDown = Screen.hasShiftDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    BalmNetworking.sendToServer(new BalanceMessage(this.getTweakId(), isShiftDown));
                } else {
                    if (isShiftDown) {
                        clientProvider.spreadGrid(provider, player, container, this.getTweakId());
                    } else {
                        clientProvider.balanceGrid(provider, player, container, this.getTweakId());
                    }
                }
            }
        };
    }

    @Override
    public Button createRotateButton(int id, @Nullable AbstractContainerScreen<?> parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 16, 0, GuiTweakButton.TweakOption.Rotate, id) {
            @Override
            protected void onTweakButtonClicked(Player player, AbstractContainerMenu container, TweakProvider<AbstractContainerMenu> provider, ClientProvider clientProvider) {
                boolean isShiftDown = Screen.hasShiftDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    BalmNetworking.sendToServer(new RotateMessage(this.getTweakId(), isShiftDown));
                } else {
                    clientProvider.rotateGrid(provider, player, container, this.getTweakId(), isShiftDown);
                }
            }
        };
    }

    @Override
    public Button createClearButton(int id, @Nullable AbstractContainerScreen<?> parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 32, 0, GuiTweakButton.TweakOption.Clear, id) {
            @Override
            protected void onTweakButtonClicked(Player player, AbstractContainerMenu container, TweakProvider<AbstractContainerMenu> provider, ClientProvider clientProvider) {
                boolean isShiftDown = Screen.hasShiftDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    BalmNetworking.sendToServer(new ClearMessage(this.getTweakId(), isShiftDown));
                } else {
                    clientProvider.clearGrid(provider, player, container, this.getTweakId(), isShiftDown);
                }
            }
        };
    }

}
