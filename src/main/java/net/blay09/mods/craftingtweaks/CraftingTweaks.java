package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import net.blay09.mods.craftingtweaks.addon.BackpacksTweakProvider;
import net.blay09.mods.craftingtweaks.addon.JACBTweakProvider;
import net.blay09.mods.craftingtweaks.addon.ThaumCraft5TweakProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
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
                provider.setGrid(tagCompound.hasKey("GridSlotNumber") ? tagCompound.getInteger("GridSlotNumber") : 1, tagCompound.hasKey("GridSize") ? tagCompound.getInteger("GridSize") : 9);
                provider.setHideButtons(tagCompound.getBoolean("HideButtons"));
                NBTTagCompound rotateCompound = tagCompound.getCompoundTag("TweakRotate");
                provider.setTweakRotate(!rotateCompound.hasKey("Enabled") || rotateCompound.getBoolean("Enabled"), buttonOffsetX + (rotateCompound.hasKey("ButtonX") ? rotateCompound.getInteger("ButtonX") : 0), buttonOffsetY + (rotateCompound.hasKey("ButtonY") ? rotateCompound.getInteger("ButtonY") : 0));
                NBTTagCompound balanceCompound = tagCompound.getCompoundTag("TweakBalance");
                provider.setTweakBalance(!balanceCompound.hasKey("Enabled") || balanceCompound.getBoolean("Enabled"), buttonOffsetX + (balanceCompound.hasKey("ButtonX") ? balanceCompound.getInteger("ButtonX") : 0), buttonOffsetY + (balanceCompound.hasKey("ButtonY") ? balanceCompound.getInteger("ButtonY") : 18));
                NBTTagCompound clearCompound = tagCompound.getCompoundTag("TweakClear");
                provider.setTweakClear(!clearCompound.hasKey("Enabled") || clearCompound.getBoolean("Enabled"), buttonOffsetX + (clearCompound.hasKey("ButtonX") ? clearCompound.getInteger("ButtonX") : 0), buttonOffsetY + (clearCompound.hasKey("ButtonY") ? clearCompound.getInteger("ButtonY") : 18 + 18));
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

        SimpleTweakProvider provider = CraftingTweaksAPI.registerSimpleProvider("minecraft", ContainerWorkbench.class);
        provider.setTweakRotate(true, 10, 17);
        provider.setTweakBalance(true, 10, 17 + 18);
        provider.setTweakClear(true, 10, 17 + 18 + 18);

        registerProvider("thaumcraft.common.container.ContainerArcaneWorkbench", new ThaumCraft5TweakProvider());
        registerProvider("tv.vanhal.jacb.gui.BenchContainer", new JACBTweakProvider());
        registerProvider("de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack", new BackpacksTweakProvider());

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

}
