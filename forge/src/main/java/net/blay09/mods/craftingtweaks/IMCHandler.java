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
            data.setContainerClass(tagCompound.getString("ContainerClass"));
            data.setContainerCallbackClass(tagCompound.getString("ContainerCallback"));
            data.setValidContainerPredicateClass(tagCompound.getString("ValidContainerPredicate"));
            data.setGetGridStartFunctionClass(tagCompound.getString("GetGridStartFunction"));
            data.setGridSlotNumber(getIntOr(tagCompound, "GridSlotNumber", 1));
            data.setGridSize(getIntOr(tagCompound, "GridSize", 9));
            if (tagCompound.contains("ButtonOffsetX")) {
                data.setButtonOffsetX(tagCompound.getInt("ButtonOffsetX"));
            }
            if (tagCompound.contains("ButtonOffsetY")) {
                data.setButtonOffsetY(tagCompound.getInt("ButtonOffsetY"));
            }
            data.setAlignToGrid(tagCompound.getString("AlignToGrid"));
            data.setHideButtons(tagCompound.getBoolean("HideButtons"));
            data.setPhantomItems(tagCompound.getBoolean("PhantomItems"));

            CompoundTag rotateCompound = tagCompound.getCompound("TweakRotate");
            var rotateTweak = new CraftingTweaksRegistrationData.TweakData();
            rotateTweak.setEnabled(getBoolOr(rotateCompound, "Enabled", true));
            rotateTweak.setShowButton(getBoolOr(rotateCompound, "ShowButton", true));
            if (rotateCompound.contains("ButtonX")) {
                rotateTweak.setButtonX(rotateCompound.getInt("ButtonX"));
            }
            if (rotateCompound.contains("ButtonY")) {
                rotateTweak.setButtonY(rotateCompound.getInt("ButtonY"));
            }
            data.setTweakRotate(rotateTweak);

            CompoundTag balanceCompound = tagCompound.getCompound("TweakBalance");
            var balanceTweak = new CraftingTweaksRegistrationData.TweakData();
            balanceTweak.setEnabled(getBoolOr(balanceCompound, "Enabled", true));
            balanceTweak.setShowButton(getBoolOr(balanceCompound, "ShowButton", true));
            if (balanceCompound.contains("ButtonX")) {
                balanceTweak.setButtonX(balanceCompound.getInt("ButtonX"));
            }
            if (balanceCompound.contains("ButtonY")) {
                balanceTweak.setButtonY(balanceCompound.getInt("ButtonY"));
            }
            data.setTweakBalance(balanceTweak);

            CompoundTag clearCompound = tagCompound.getCompound("TweakClear");
            var clearTweak = new CraftingTweaksRegistrationData.TweakData();
            clearTweak.setEnabled(getBoolOr(clearCompound, "Enabled", true));
            clearTweak.setShowButton(getBoolOr(clearCompound, "ShowButton", true));
            if (clearCompound.contains("ButtonX")) {
                clearTweak.setButtonX(clearCompound.getInt("ButtonX"));
            }
            if (clearCompound.contains("ButtonY")) {
                clearTweak.setButtonY(clearCompound.getInt("ButtonY"));
            }
            data.setTweakClear(clearTweak);

            CraftingGridProvider gridProvider = DataDrivenGridFactory.createGridProvider(data);
            CraftingTweaksAPI.registerCraftingGridProvider(gridProvider);
            logger.info("{} has registered {} for CraftingTweaks via IMC", data.getModId(), data.getContainerClass());
        });
    }

    private static int getIntOr(CompoundTag tagCompound, String key, int defaultVal) {
        return (tagCompound.contains(key) ? tagCompound.getInt(key) : defaultVal);
    }

    private static boolean getBoolOr(CompoundTag tagCompound, String key, boolean defaultVal) {
        return (tagCompound.contains(key) ? tagCompound.getBoolean(key) : defaultVal);
    }
}
