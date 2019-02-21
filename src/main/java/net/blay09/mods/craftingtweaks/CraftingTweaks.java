package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.addons.CraftingTweaksAddons;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.blay09.mods.craftingtweaks.client.KeyBindings;
import net.blay09.mods.craftingtweaks.net.NetworkHandler;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Mod(CraftingTweaks.MOD_ID)
public class CraftingTweaks {

    public static boolean TEST_CLIENT_SIDE = false;

    public static final Logger logger = LogManager.getLogger();
    public static final String MOD_ID = "craftingtweaks";

    public static boolean isServerSideInstalled;
    public static Optional<CraftingTweaksClient> craftingTweaksClient = Optional.empty();

    public CraftingTweaks() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processInterMod);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CraftingTweaksConfig.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CraftingTweaksConfig.clientSpec);

        CraftingTweaksAPI.setupAPI(new InternalMethodsImpl());
    }

    @SuppressWarnings("unchecked")
    private void processInterMod(InterModProcessEvent event) {
        event.getIMCStream(it -> it.equals("RegisterProvider") || it.equals("RegisterProviderV2") || it.equals("RegisterProviderV3")).forEach(message -> {
            NBTTagCompound tagCompound = (NBTTagCompound) message.getMessageSupplier().get();
            String sender = message.getModId(); // TODO No way to get the sender of an IMC Message currently?
            String containerClassName = tagCompound.getString("ContainerClass");
            SimpleTweakProvider provider = new SimpleTweakProviderImpl(sender);
            int buttonOffsetX = tagCompound.hasKey("ButtonOffsetX") ? tagCompound.getInt("ButtonOffsetX") : -16;
            int buttonOffsetY = tagCompound.hasKey("ButtonOffsetY") ? tagCompound.getInt("ButtonOffsetY") : 16;

            EnumFacing alignToGrid = null;
            String alignToGridName = tagCompound.getString("AlignToGrid");
            switch (alignToGridName.toLowerCase()) {
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

            NBTTagCompound rotateCompound = tagCompound.getCompound("TweakRotate");
            provider.setTweakRotate(getBoolOr(rotateCompound, "Enabled", true), getBoolOr(rotateCompound, "ShowButton", true),
                    buttonOffsetX + getIntOr(rotateCompound, "ButtonX", 0), buttonOffsetY + getIntOr(rotateCompound, "ButtonY", 0));

            NBTTagCompound balanceCompound = tagCompound.getCompound("TweakBalance");
            provider.setTweakBalance(getBoolOr(balanceCompound, "Enabled", true), getBoolOr(balanceCompound, "ShowButton", true),
                    buttonOffsetX + getIntOr(balanceCompound, "ButtonX", 0), buttonOffsetY + getIntOr(balanceCompound, "ButtonY", 18));

            NBTTagCompound clearCompound = tagCompound.getCompound("TweakClear");
            provider.setTweakClear(getBoolOr(clearCompound, "Enabled", true), getBoolOr(clearCompound, "ShowButton", true),
                    buttonOffsetX + getIntOr(clearCompound, "ButtonX", 0), buttonOffsetY + getIntOr(clearCompound, "ButtonY", 36));

            String validContainerPredicateLegacy = tagCompound.getString("ContainerCallback");
            if (!validContainerPredicateLegacy.isEmpty()) {
                try {
                    Class<?> functionClass = Class.forName(validContainerPredicateLegacy);
                    if (!Function.class.isAssignableFrom(functionClass)) {
                        logger.error("{} sent a container callback that's not even a function", sender);
                        return;
                    }
                    Function<Container, Boolean> function = (Function<Container, Boolean>) functionClass.newInstance();
                    // This doesn't compile as a lambda for some weird Javaish reason, so leave it as is
                    provider.setContainerValidPredicate(new Predicate<Container>() {
                        @Override
                        public boolean test(@Nullable Container input) {
                            Boolean result = function.apply(input);
                            return result != null ? result : false;
                        }
                    });
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    logger.error("{} sent an invalid container callback.", sender);
                }
            }

            String validContainerPredicate = tagCompound.getString("ValidContainerPredicate");
            if (!validContainerPredicate.isEmpty()) {
                try {
                    Class<?> predicateClass = Class.forName(validContainerPredicate);
                    if (!Predicate.class.isAssignableFrom(predicateClass)) {
                        logger.error("{} sent an invalid ValidContainerPredicate - it must implement Predicate<Container>", sender);
                        return;
                    }
                    Predicate<Container> predicate = (Predicate<Container>) predicateClass.newInstance();
                    provider.setContainerValidPredicate(predicate);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    logger.error("{} sent an invalid ValidContainerPredicate: {}", sender, e.getMessage());
                }
            }

            String getGridStartFunction = tagCompound.getString("GetGridStartFunction");
            if (!getGridStartFunction.isEmpty()) {
                try {
                    Class<?> functionClass = Class.forName(getGridStartFunction);
                    if (!Function.class.isAssignableFrom(functionClass)) {
                        logger.error("{} sent an invalid GetGridStartFunction - it must implement Function<Container, Integer>", sender);
                        return;
                    }
                    Function<Container, Integer> function = (Function<Container, Integer>) functionClass.newInstance();
                    provider.setGetGridStartFunction(function);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    logger.error("{} sent an invalid GetGridStartFunction: {}", sender, e.getMessage());
                }
            }

            CraftingTweaksProviderManager.registerProvider(containerClassName, provider);
            logger.info("{} has registered {} for CraftingTweaks", sender, containerClassName);
        });
    }

    private void setup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(NetworkHandler::init);

        Compatibility.vanilla();
        CraftingTweaksAddons.loadAddons();
    }

    private void setupClient(FMLClientSetupEvent event) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            craftingTweaksClient = Optional.of(new CraftingTweaksClient());

            DeferredWorkQueue.runLater(() -> {
                KeyBindings.init();

                craftingTweaksClient.ifPresent(MinecraftForge.EVENT_BUS::register);
            });
        });
    }

    private static int getIntOr(NBTTagCompound tagCompound, String key, int defaultVal) {
        return (tagCompound.hasKey(key) ? tagCompound.getInt(key) : defaultVal);
    }

    private static boolean getBoolOr(NBTTagCompound tagCompound, String key, boolean defaultVal) {
        return (tagCompound.hasKey(key) ? tagCompound.getBoolean(key) : defaultVal);
    }


}
