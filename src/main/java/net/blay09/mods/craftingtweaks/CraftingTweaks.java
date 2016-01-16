package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = CraftingTweaks.MOD_ID, name = "Crafting Tweaks", acceptableRemoteVersions = "*")
public class CraftingTweaks {

    public enum ModSupportState {
        ENABLED,
        BUTTONS_ONLY,
        HOTKEYS_ONLY,
        DISABLED;

        public static ModSupportState fromName(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ENABLED;
            }
        }

        public static String[] getValidValues() {
            ModSupportState[] values = ModSupportState.values();
            String[] validValues = new String[values.length];
            for(int i = 0; i < values.length; i++) {
                validValues[i] = values[i].name().toLowerCase();
            }
            return validValues;
        }
    }

    public static final Logger logger = LogManager.getLogger();
    public static final String MOD_ID = "craftingtweaks";

    @Mod.Instance
    public static CraftingTweaks instance;

    @SidedProxy(clientSide = "net.blay09.mods.craftingtweaks.client.ClientProxy", serverSide = "net.blay09.mods.craftingtweaks.CommonProxy")
    public static CommonProxy proxy;

    private static Configuration config;

    private final Map<String, ModSupportState> configMap = Maps.newHashMap();
    private final Map<Class<? extends Container>, TweakProvider> providerMap = Maps.newHashMap();

    public static boolean hideButtons;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CraftingTweaksAPI.setupAPI(new InternalMethodsImpl());

        configMap.put("minecraft", ModSupportState.ENABLED);
        configMap.put("Thaumcraft", ModSupportState.ENABLED);
        configMap.put("Backpack", ModSupportState.ENABLED);
        configMap.put("jacb", ModSupportState.ENABLED);

        config = new Configuration(event.getSuggestedConfigurationFile());
        hideButtons = config.getBoolean("hideButtons", "general", false, "This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.");
        config.setCategoryComment("addons", "Here you can control whether support for a mod should be enabled, buttons_only, hotkeys_only or disabled. For Vanilla Minecraft, see the option 'minecraft'. Mods are identified by their mod ids.");
        config.getString("minecraft", "addons", ModSupportState.ENABLED.name().toLowerCase(), "", ModSupportState.getValidValues());
        // Load all options (including those from non-included addons)
        for(Property property : config.getCategory("addons").values()) {
            configMap.put(property.getName(), ModSupportState.fromName(config.getString(property.getName(), "addons", ModSupportState.ENABLED.name().toLowerCase(), "enabled, buttons_only, hotkeys_only or disabled", ModSupportState.getValidValues())));
        }
    }

    @Mod.EventHandler
    public void imc(FMLInterModComms.IMCEvent event) {
        for(FMLInterModComms.IMCMessage message : event.getMessages()) {
            if(message.isNBTMessage() && message.key.equals("RegisterProvider")) {
                NBTTagCompound tagCompound = message.getNBTValue();
                String containerClassName = tagCompound.getString("ContainerClass");
                SimpleTweakProvider provider = new SimpleTweakProviderImpl(message.getSender());

                int buttonOffsetX = tagCompound.hasKey("ButtonOffsetX") ? tagCompound.getInteger("ButtonOffsetX") : -16;
                int buttonOffsetY = tagCompound.hasKey("ButtonOffsetY") ? tagCompound.getInteger("ButtonOffsetY") : 16;

                provider.setGrid(getIntOr(tagCompound, "GridSlotNumber", 1), getIntOr(tagCompound, "GridSize", 9));
                provider.setHideButtons(tagCompound.getBoolean("HideButtons"));
                provider.setPhantomItems(tagCompound.getBoolean("PhantomItems"));

                NBTTagCompound rotateCompound = tagCompound.getCompoundTag("TweakRotate");
                provider.setTweakRotate(getBoolOr(rotateCompound, "Enabled", true), getBoolOr(rotateCompound, "ShowButton", true),
                        buttonOffsetX + getIntOr(rotateCompound, "ButtonX", 0), buttonOffsetY + getIntOr(rotateCompound, "ButtonY", 0));

                NBTTagCompound balanceCompound = tagCompound.getCompoundTag("TweakBalance");
                provider.setTweakBalance(getBoolOr(balanceCompound, "Enabled", true), getBoolOr(balanceCompound, "ShowButton", true),
                        buttonOffsetX + getIntOr(balanceCompound, "ButtonX", 0), buttonOffsetY + getIntOr(balanceCompound, "ButtonY", 18));

                NBTTagCompound clearCompound = tagCompound.getCompoundTag("TweakClear");
                provider.setTweakClear(getBoolOr(clearCompound, "Enabled", true), getBoolOr(clearCompound, "ShowButton", true),
                        buttonOffsetX + getIntOr(clearCompound, "ButtonX", 0), buttonOffsetY + getIntOr(clearCompound, "ButtonY", 36));

                registerProvider(containerClassName, provider);
                logger.info(message.getSender() + " has registered " + containerClassName + " for CraftingTweaks");
            } else {
                logger.warn("CraftingTweaks received an invalid IMC message from " + message.getSender());
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        NetworkHandler.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        Compatiblity.vanilla();
        Compatiblity.jacb();
        Compatiblity.backpack();
        Compatiblity.thaumcraft();

        config.save();
    }

    public void registerProvider(Class<? extends Container> clazz, TweakProvider provider) {
        if(!provider.getModId().equals("minecraft") && !Loader.isModLoaded(provider.getModId())) {
            return;
        }
        if(provider.load()) {
            providerMap.put(clazz, provider);
        }
    }

    @SuppressWarnings("unchecked")
    public void registerProvider(String className, TweakProvider provider) {
        config.getString(provider.getModId(), "addons", ModSupportState.ENABLED.name().toLowerCase(), "enabled, buttons_only, hotkeys_only or disabled", ModSupportState.getValidValues());
        if(Loader.isModLoaded(provider.getModId())) {
            if(provider.load()) {
                try {
                    Class clazz = Class.forName(className);
                    if (Container.class.isAssignableFrom(clazz)) {
                        providerMap.put(clazz, provider);
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }

    public TweakProvider getProvider(Container container) {
        if(container == null) {
            return null;
        }
        for(Class clazz : providerMap.keySet()) {
            if(container.getClass() == clazz) {
                return providerMap.get(clazz);
            }
        }
        for(Class<? extends Container> clazz : providerMap.keySet()) {
            if(clazz.isAssignableFrom(container.getClass())) {
                return providerMap.get(clazz);
            }
        }
        return null;
    }

    public ModSupportState getModSupportState(String modId) {
        ModSupportState suportState = configMap.get(modId);
        if(suportState == null) {
            suportState = ModSupportState.ENABLED;
            configMap.put(modId, suportState);
        }
        return suportState;
    }

    public static void saveConfig() {
        config.get("general", "hideButtons", false, "This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.").set(hideButtons);
        config.save();
    }

    private static int getIntOr(NBTTagCompound tagCompound, String key, int defaultVal) {
        return (tagCompound.hasKey(key) ? tagCompound.getInteger(key) : defaultVal);
    }

    private static boolean getBoolOr(NBTTagCompound tagCompound, String key, boolean defaultVal) {
        return (tagCompound.hasKey(key) ? tagCompound.getBoolean(key) : defaultVal);
    }
}
