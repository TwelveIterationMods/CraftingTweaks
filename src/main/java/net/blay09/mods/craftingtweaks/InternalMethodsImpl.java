package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.DefaultProviderV2;
import net.blay09.mods.craftingtweaks.api.InternalMethods;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.client.ClientProvider;
import net.blay09.mods.craftingtweaks.client.GuiTweakButton;
import net.blay09.mods.craftingtweaks.net.MessageBalance;
import net.blay09.mods.craftingtweaks.net.MessageClear;
import net.blay09.mods.craftingtweaks.net.MessageRotate;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public <T extends Container> void registerProvider(Class<T> containerClass, TweakProvider<T> provider) {
        CraftingTweaksProviderManager.registerProvider(containerClass, provider);
    }

    @Override
    public <T extends Container> SimpleTweakProvider<T> registerSimpleProvider(String modid, Class<T> containerClass) {
        SimpleTweakProvider<T> simpleTweakProvider = new SimpleTweakProviderImpl<>(modid);
        CraftingTweaksProviderManager.registerProvider(containerClass, simpleTweakProvider);
        return simpleTweakProvider;
    }

    @Override
    public DefaultProviderV2 createDefaultProviderV2() {
        return new DefaultProviderV2Impl();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GuiButton createBalanceButton(int id, @Nullable GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 48, 0, GuiTweakButton.TweakOption.Balance, id) {
            @Override
            protected void onTweakButtonClicked(EntityPlayer player, Container container, TweakProvider<Container> provider, ClientProvider clientProvider) {
                boolean isShiftDown = GuiScreen.isShiftKeyDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    NetworkHandler.channel.sendToServer(new MessageBalance(this.getTweakId(), isShiftDown));
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
    @OnlyIn(Dist.CLIENT)
    public GuiButton createRotateButton(int id, @Nullable GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 16, 0, GuiTweakButton.TweakOption.Rotate, id) {
            @Override
            protected void onTweakButtonClicked(EntityPlayer player, Container container, TweakProvider<Container> provider, ClientProvider clientProvider) {
                boolean isShiftDown = GuiScreen.isShiftKeyDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    NetworkHandler.channel.sendToServer(new MessageRotate(this.getTweakId(), isShiftDown));
                } else {
                    clientProvider.rotateGrid(provider, player, container, this.getTweakId(), isShiftDown);
                }
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GuiButton createClearButton(int id, @Nullable GuiContainer parentGui, int x, int y) {
        return new GuiTweakButton(parentGui, x, y, 32, 0, GuiTweakButton.TweakOption.Clear, id) {
            @Override
            protected void onTweakButtonClicked(EntityPlayer player, Container container, TweakProvider<Container> provider, ClientProvider clientProvider) {
                boolean isShiftDown = GuiScreen.isShiftKeyDown();
                if (CraftingTweaks.isServerSideInstalled) {
                    NetworkHandler.channel.sendToServer(new MessageClear(this.getTweakId(), isShiftDown));
                } else {
                    clientProvider.clearGrid(provider, player, container, this.getTweakId(), isShiftDown);
                }
            }
        };
    }

}
