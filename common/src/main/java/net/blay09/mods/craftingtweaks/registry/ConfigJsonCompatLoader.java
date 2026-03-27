package net.blay09.mods.craftingtweaks.registry;

import com.google.gson.Gson;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConfigJsonCompatLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigJsonCompatLoader.class);
    private static final Gson gson = new Gson();

    private static final List<CraftingGridProvider> providersFromConfig = new ArrayList<>();

    public static void load() {
        for (CraftingGridProvider providersFromDataPack : providersFromConfig) {
            CraftingTweaksAPI.unregisterCraftingGridProvider(providersFromDataPack);
        }
        providersFromConfig.clear();

        final var configDir = new File(Balm.config().getConfigDir(), "craftingtweaks/grids");
        if (!configDir.exists() && !configDir.mkdirs()) {
            logger.error("Failed to create CraftingTweaks config directory {}", configDir);
            return;
        }

        final var files = configDir.listFiles(it -> it.getName().endsWith(".json"));
        if (files == null) {
            return;
        }

        for (final var file : files) {
            try (final var reader = Files.newBufferedReader(file.toPath())) {
                final var gridProvider = load(file, gson.fromJson(reader, CraftingTweaksRegistrationData.class));
                if (gridProvider != null) {
                    providersFromConfig.add(gridProvider);
                }
            } catch (IOException e) {
                logger.error("Failed to load CraftingTweaks file {}", file, e);
            }
        }
    }

    private static boolean isCompatEnabled(String modId) {
        return !CraftingTweaksConfig.getActive().client.disabledAddons.contains(modId);
    }

    private static @Nullable CraftingGridProvider load(File resource, CraftingTweaksRegistrationData data) {
        String modId = data.getModId();
        if ((!modId.equals("minecraft") && !Balm.platform().isModLoaded(modId)) || !isCompatEnabled(modId) || !data.isEnabled()) {
            return null;
        }

        CraftingGridProvider gridProvider = DataDrivenGridFactory.createGridProvider(data);
        if (gridProvider != null) {
            CraftingTweaksAPI.registerCraftingGridProvider(gridProvider);
            logger.info("Config file {} has registered {} of {} with CraftingTweaks", resource, data.getContainerClass(), modId);
        }
        return gridProvider;
    }

}
