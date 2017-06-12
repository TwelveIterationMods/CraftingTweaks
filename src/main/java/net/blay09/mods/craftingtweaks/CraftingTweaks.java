package net.blay09.mods.craftingtweaks;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.blay09.mods.craftingtweaks.addons.CraftingTweaksAddons;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.api.TweakProvider;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Mod(modid = CraftingTweaks.MOD_ID, name = "Crafting Tweaks", acceptedMinecraftVersions = "[1.12]", guiFactory = "net.blay09.mods.craftingtweaks.client.GuiFactory")
public class CraftingTweaks {

    public static boolean TEST_CLIENT_SIDE = false;

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

    public static Configuration config;

    private final Map<String, ModSupportState> configMap = Maps.newHashMap();
    private final Map<Class<? extends Container>, TweakProvider> providerMap = Maps.newHashMap();

    public static boolean hideButtons;
    public static boolean rightClickCraftsStack;
    public static boolean compressAnywhere;
    public static boolean hideButtonTooltips;
    public static List<String> compressBlacklist;

    public static boolean isServerSideInstalled;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
        CraftingTweaksAPI.setupAPI(new InternalMethodsImpl());

        MinecraftForge.EVENT_BUS.register(this);

        configMap.put("minecraft", ModSupportState.ENABLED);

        config = new Configuration(event.getSuggestedConfigurationFile());
        reloadConfig();
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void imc(FMLInterModComms.IMCEvent event) {
        for(FMLInterModComms.IMCMessage message : event.getMessages()) {
            if(message.isNBTMessage() && (message.key.equals("RegisterProvider")
                    || message.key.equals("RegisterProviderV2")
                    || message.key.equals("RegisterProviderV3"))) {
                NBTTagCompound tagCompound = message.getNBTValue();
                String containerClassName = tagCompound.getString("ContainerClass");
                SimpleTweakProvider provider = new SimpleTweakProviderImpl(message.getSender());

                int buttonOffsetX = tagCompound.hasKey("ButtonOffsetX") ? tagCompound.getInteger("ButtonOffsetX") : -16;
                int buttonOffsetY = tagCompound.hasKey("ButtonOffsetY") ? tagCompound.getInteger("ButtonOffsetY") : 16;
                EnumFacing alignToGrid = null;
                String alignToGridName = tagCompound.getString("AlignToGrid");
                switch(alignToGridName.toLowerCase()) {
                    case "north":
                    case "up":
                        alignToGrid = EnumFacing.UP;
                        break;
                    case "south":
                    case "down":
                        alignToGrid = EnumFacing.DOWN;
                        break;
                    case "east":
                    case "right":
                        alignToGrid = EnumFacing.EAST;
                        break;
                    case "west":
                    case "left":
                        alignToGrid = EnumFacing.WEST;
                        break;
                }
                provider.setAlignToGrid(alignToGrid);

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

                String validContainerPredicateLegacy = tagCompound.getString("ContainerCallback");
                if(!validContainerPredicateLegacy.isEmpty()) {
                    try {
                        Class<?> functionClass = Class.forName(validContainerPredicateLegacy);
                        if (!Function.class.isAssignableFrom(functionClass)) {
                            logger.error(message.getSender() + " sent a container callback that's not even a function");
                            return;
                        }
                        Function<Container, Boolean> function = (Function<Container, Boolean>) functionClass.newInstance();
                        provider.setContainerValidPredicate(new Predicate<Container>() { // TODO this doesn't compile as a lambda for some weird Javaish reason, so leave it as is
                            @Override
                            public boolean apply(@Nullable Container input) {
                                Boolean result = function.apply(input);
                                return result != null ? result : false;
                            }
                        });
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        logger.error(message.getSender() + " sent an invalid container callback.");
                    }
                }

                String validContainerPredicate = tagCompound.getString("ValidContainerPredicate");
                if(!validContainerPredicate.isEmpty()) {
                    try {
                        Class<?> predicateClass = Class.forName(validContainerPredicate);
                        if (!Predicate.class.isAssignableFrom(predicateClass)) {
                            logger.error(message.getSender() + " sent an invalid ValidContainerPredicate - it must implement Predicate<Container>");
                            return;
                        }
                        Predicate<Container> predicate = (Predicate<Container>) predicateClass.newInstance();
                        provider.setContainerValidPredicate(predicate);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        logger.error(message.getSender() + " sent an invalid ValidContainerPredicate: " + e.getMessage());
                    }
                }

                String getGridStartFunction = tagCompound.getString("GetGridStartFunction");
                if(!getGridStartFunction.isEmpty()) {
                    try {
                        Class<?> functionClass = Class.forName(getGridStartFunction);
                        if (!Function.class.isAssignableFrom(functionClass)) {
                            logger.error("%s sent an invalid GetGridStartFunction - it must implement Function<Container, Integer>", message.getSender());
                            return;
                        }
                        Function<Container, Integer> function = (Function<Container, Integer>) functionClass.newInstance();
                        provider.setGetGridStartFunction(function);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        logger.error("%s sent an invalid GetGridStartFunction: %s", message.getSender(), e.getMessage());
                    }
                }


                registerProvider(containerClassName, provider);
                logger.info(message.getSender() + " has registered " + containerClassName + " for CraftingTweaks");
            } else {
                logger.error("CraftingTweaks received an invalid IMC message from " + message.getSender());
            }
        }
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        NetworkHandler.init();
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        Compatibility.vanilla();
        CraftingTweaksAddons.postInit(event);

        if(config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent event) {
        if(event.getModID().equals(MOD_ID)) {
            reloadConfig();
            if(config.hasChanged()) {
                config.save();
            }
        }
    }

    @NetworkCheckHandler
    public boolean checkNetwork(Map<String, String> map, Side side) {
        if(side == Side.SERVER) {
            isServerSideInstalled = !TEST_CLIENT_SIDE && map.containsKey(MOD_ID);
        }
        return true;
    }

    public void reloadConfig() {
        hideButtons = config.getBoolean("hideButtons", "general", false, "This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.");
        rightClickCraftsStack = config.getBoolean("rightClickCraftsStack", "general", true, "If set to true, right-clicking the result slot in a crafting table will craft a full stack.");
        hideButtonTooltips = config.getBoolean("hideButtonTooltips", "general", false, "Set this to true if you don't want the tweak buttons' tooltips to show.");
        compressAnywhere = config.getBoolean("compressAnywhere", "general", false, "Set this to true if you want the (de)compress feature to work outside of crafting GUIs (only works if installed on server)");
        compressBlacklist = Lists.newArrayList(config.getStringList("compressBlacklist", "general", new String[] {"ExtraUtilities:decorativeBlock1", "minecraft:sandstone", "minecraft:iron_trapdoor"}, "A list of modid:name entries that will not be crafted by the compress key."));
        config.setCategoryComment("addons", "Here you can control whether support for a mod should be enabled, buttons_only, hotkeys_only or disabled. For Vanilla Minecraft, see the option 'minecraft'. Mods are identified by their mod ids.");
        config.getString("minecraft", "addons", ModSupportState.ENABLED.name().toLowerCase(), "", ModSupportState.getValidValues());
        // Load all options (including those from non-included addons)
        for(Property property : config.getCategory("addons").values()) {
            configMap.put(property.getName(), ModSupportState.fromName(config.getString(property.getName(), "addons", ModSupportState.ENABLED.name().toLowerCase(), "enabled, buttons_only, hotkeys_only or disabled", ModSupportState.getValidValues())));
        }
    }

    public static void saveConfig() {
        config.get("general", "hideButtons", false, "This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.").set(hideButtons);
        config.save();
    }

    public <T extends Container> void registerProvider(Class<T> clazz, TweakProvider<T> provider) {
        if(!provider.getModId().equals("minecraft") && !Loader.isModLoaded(provider.getModId())) {
            return;
        }
        if(provider.load()) {
            providerMap.put(clazz, provider);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerProvider(String className, TweakProvider provider) {
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

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Container> TweakProvider<T> getProvider(@Nullable T container) {
        if(container == null) {
            return null;
        }
        for(Class<? extends Container> clazz : providerMap.keySet()) {
            if(container.getClass() == clazz) {
                return (TweakProvider<T>) providerMap.get(clazz);
            }
        }
        for(Class<? extends Container> clazz : providerMap.keySet()) {
            if(clazz.isAssignableFrom(container.getClass())) {
                return (TweakProvider<T>) providerMap.get(clazz);
            }
        }
        return null;
    }

    public ModSupportState getModSupportState(String modId) {
        ModSupportState supportState = configMap.get(modId);
        if(supportState == null) {
            supportState = ModSupportState.ENABLED;
            configMap.put(modId, supportState);
        }
        return supportState;
    }

    private static int getIntOr(NBTTagCompound tagCompound, String key, int defaultVal) {
        return (tagCompound.hasKey(key) ? tagCompound.getInteger(key) : defaultVal);
    }

    private static boolean getBoolOr(NBTTagCompound tagCompound, String key, boolean defaultVal) {
        return (tagCompound.hasKey(key) ? tagCompound.getBoolean(key) : defaultVal);
    }


}
