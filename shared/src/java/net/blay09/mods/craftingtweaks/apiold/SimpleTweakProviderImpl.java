//package net.blay09.mods.craftingtweaks.api;
//
//import net.blay09.mods.craftingtweaks.apiv2.CraftingTweaksAPI;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.components.AbstractWidget;
//import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//import net.minecraft.core.Direction;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.inventory.Slot;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
//public class SimpleTweakProviderImpl<T extends AbstractContainerMenu> implements SimpleTweakProvider<T> {
//
//    private Predicate<AbstractContainerMenu> isContainerValidPredicate;
//    private Function<AbstractContainerMenu, Integer> getGridStartFunction;
//
//    private int getButtonY(AbstractContainerScreen<T> guiContainer, int index) {
//        Slot firstSlot = guiContainer.getMenu().slots.get(getCraftingGridStart(Minecraft.getInstance().player, guiContainer.getMenu(), 0));
//
//        return 0;
//    }
//
//    @Override
//    public void setContainerValidPredicate(Predicate<AbstractContainerMenu> predicate) {
//        this.isContainerValidPredicate = predicate;
//    }
//
//    @Override
//    public void setGetGridStartFunction(Function<AbstractContainerMenu, Integer> function) {
//        this.getGridStartFunction = function;
//    }
//
//    @Override
//    public boolean isValidContainer(AbstractContainerMenu menu) {
//        return isContainerValidPredicate == null || isContainerValidPredicate.test(menu);
//    }
//}
