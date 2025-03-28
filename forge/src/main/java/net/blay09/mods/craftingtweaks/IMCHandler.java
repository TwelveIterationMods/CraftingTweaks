package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.registry.CraftingTweaksRegistrationData;
import net.blay09.mods.craftingtweaks.registry.DataDrivenGridFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMCHandler {

    private static final Logger logger = LoggerFactory.getLogger(IMCHandler.class);

    public static void processInterMod(InterModProcessEvent event) {
        event.getIMCStream(it -> it.equals("RegisterProvider") || it.equals("RegisterProviderV2") || it.equals("RegisterProviderV3")).forEach(message -> {
            CompoundTag tagCompound = (CompoundTag) message.messageSupplier().get();
            var data = new CraftingTweaksRegistrationData();
            data.setModId(message.senderModId());
            data.setContainerClass(tagCompound.getStringOr("ContainerClass", ""));
            data.setContainerCallbackClass(tagCompound.getStringOr("ContainerCallback", ""));
            data.setValidContainerPredicateClass(tagCompound.getStringOr("ValidContainerPredicate", ""));
            data.setGetGridStartFunctionClass(tagCompound.getStringOr("GetGridStartFunction", ""));
            data.setGridSlotNumber(tagCompound.getIntOr("GridSlotNumber", 1));
            data.setGridSize(tagCompound.getIntOr("GridSize", 9));
            tagCompound.getInt("ButtonOffsetX").ifPresent(data::setButtonOffsetX);
            tagCompound.getInt("ButtonOffsetY").ifPresent(data::setButtonOffsetY);
            data.setAlignToGrid(tagCompound.getStringOr("AlignToGrid", ""));
            data.setButtonStyle(tagCompound.getStringOr("ButtonStyle", ""));
            data.setHideButtons(tagCompound.getBooleanOr("HideButtons", false));
            data.setPhantomItems(tagCompound.getBooleanOr("PhantomItems", false));

            CompoundTag rotateCompound = tagCompound.getCompoundOrEmpty("TweakRotate");
            var rotateTweak = new CraftingTweaksRegistrationData.TweakData();
            rotateTweak.setEnabled(rotateCompound.getBooleanOr("Enabled", true));
            rotateTweak.setShowButton(rotateCompound.getBooleanOr("ShowButton", true));
            rotateCompound.getInt("ButtonX").ifPresent(rotateTweak::setButtonX);
            rotateCompound.getInt("ButtonY").ifPresent(rotateTweak::setButtonY);
            data.setTweakRotate(rotateTweak);

            CompoundTag balanceCompound = tagCompound.getCompoundOrEmpty("TweakBalance");
            var balanceTweak = new CraftingTweaksRegistrationData.TweakData();
            balanceTweak.setEnabled(balanceCompound.getBooleanOr("Enabled", true));
            balanceTweak.setShowButton(balanceCompound.getBooleanOr("ShowButton", true));
            balanceCompound.getInt("ButtonX").ifPresent(balanceTweak::setButtonX);
            balanceCompound.getInt("ButtonY").ifPresent(balanceTweak::setButtonY);
            data.setTweakBalance(balanceTweak);

            CompoundTag clearCompound = tagCompound.getCompoundOrEmpty("TweakClear");
            var clearTweak = new CraftingTweaksRegistrationData.TweakData();
            clearTweak.setEnabled(clearCompound.getBooleanOr("Enabled", true));
            clearTweak.setShowButton(clearCompound.getBooleanOr("ShowButton", true));
            clearCompound.getInt("ButtonX").ifPresent(clearTweak::setButtonX);
            clearCompound.getInt("ButtonY").ifPresent(clearTweak::setButtonY);
            data.setTweakClear(clearTweak);

            CraftingGridProvider gridProvider = DataDrivenGridFactory.createGridProvider(data);
            if (gridProvider != null) {
                CraftingTweaksAPI.registerCraftingGridProvider(gridProvider);
                logger.info("{} has registered {} for CraftingTweaks via IMC", data.getModId(), data.getContainerClass());
            }
        });
    }
}
