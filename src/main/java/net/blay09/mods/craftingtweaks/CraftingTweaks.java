package net.blay09.mods.craftingtweaks;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;

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
    public static boolean compressAnywhere;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CraftingTweaksAPI.setupAPI(new InternalMethodsImpl());

        configMap.put("minecraft", ModSupportState.ENABLED);
        configMap.put("TConstruct", ModSupportState.ENABLED);
        configMap.put("appliedenergistics2", ModSupportState.BUTTONS_ONLY);
        configMap.put("DraconicEvolution", ModSupportState.ENABLED);
        configMap.put("Natura", ModSupportState.ENABLED);
        configMap.put("Thaumcraft", ModSupportState.ENABLED);
        configMap.put("MineFactoryReloaded", ModSupportState.ENABLED);
        configMap.put("Forestry", ModSupportState.ENABLED);
        configMap.put("Railcraft", ModSupportState.ENABLED);
        configMap.put("BuildCraft|Factory", ModSupportState.ENABLED);
        configMap.put("RotaryCraft", ModSupportState.ENABLED);
        configMap.put("RotaryCraft", ModSupportState.ENABLED);
        configMap.put("TwilightForest", ModSupportState.ENABLED);
        configMap.put("terrafirmacraft", ModSupportState.ENABLED);
        configMap.put("ganyssurface", ModSupportState.ENABLED);
        configMap.put("jacb", ModSupportState.ENABLED);
        configMap.put("bluepower", ModSupportState.ENABLED);
        configMap.put("BiblioCraft", ModSupportState.ENABLED);
        configMap.put("ThermalExpansion", ModSupportState.ENABLED);
        configMap.put("Backpack", ModSupportState.ENABLED);

        config = new Configuration(event.getSuggestedConfigurationFile());
        hideButtons = config.getBoolean("hideButtons", "general", false, "This option is toggled by the 'Toggle Buttons' key that can be defined in the Controls settings.");
        compressAnywhere = config.getBoolean("compressAnywhere", "general", false, "Set this to true if you want the (de)compress feature to work outside of crafting GUIs (only works if installed on server)");
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
        configMap.put("appliedenergistics2", ModSupportState.BUTTONS_ONLY);

        Compatiblity.vanilla();
        Compatiblity.appliedenergistics2();
        Compatiblity.avaritia();
        Compatiblity.backpacks();
        Compatiblity.bibliocraft();
        Compatiblity.bluepower();
        Compatiblity.buildcraft();
        Compatiblity.jacb();
        Compatiblity.ganyssurface();
        Compatiblity.forestry();
        Compatiblity.draconicevolution();
        Compatiblity.natura();
        Compatiblity.minefactoryreloaded();
        Compatiblity.twilightforest();
        Compatiblity.tinkersconstruct();
        Compatiblity.thermalexpansion();
        Compatiblity.railcraft();
        Compatiblity.thaumcraft4();
        Compatiblity.rotarycraft();
        Compatiblity.terrafirmacraft();

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

    @SideOnly(Side.CLIENT)
    public static boolean onGuiClick(int mouseX, int mouseY, int button) {
        if(Mouse.getEventButtonState()) {
            GuiClickEvent event = new GuiClickEvent(FMLClientHandler.instance().getClient().currentScreen, mouseX, mouseY, button);
            MinecraftForge.EVENT_BUS.post(event);
            return event.isCanceled();
        }
        return false;
    }
}
