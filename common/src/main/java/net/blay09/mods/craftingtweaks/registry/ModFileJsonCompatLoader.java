package net.blay09.mods.craftingtweaks.registry;

import com.google.gson.Gson;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ModFileJsonCompatLoader {

    private static final Logger logger = LoggerFactory.getLogger(ModFileJsonCompatLoader.class);
    private static final Gson gson = new Gson();

    private static final List<CraftingGridProvider> providersFromModFiles = new ArrayList<>();

    public static void load() {
        for (CraftingGridProvider providersFromDataPack : providersFromModFiles) {
            CraftingTweaksAPI.unregisterCraftingGridProvider(providersFromDataPack);
        }
        providersFromModFiles.clear();

        final var modPaths = Balm.lookupAllModPaths("craftingtweaks/grids");
        modPaths.values().forEach(path -> {
            try {
                try (final var walker = Files.walk(path)) {
                    walker.forEach(file -> {
                        if (file.toString().endsWith(".json")) {
                            try (final var reader = Files.newBufferedReader(file)) {
                                final var gridProvider = load(gson.fromJson(reader, CraftingTweaksRegistrationData.class));
                                if (gridProvider != null) {
                                    providersFromModFiles.add(gridProvider);
                                }
                            } catch (IOException e) {
                                logger.error("Failed to load CraftingTweaks file {}", file, e);
                            }

                        }
                    });
                }
            } catch (IOException e) {
                logger.error("Failed to load CraftingTweaks files from mod {}", path, e);
            }
        });
    }

    private static boolean isCompatEnabled(String modId) {
        return !CraftingTweaksConfig.getActive().client.disabledAddons.contains(modId);
    }

    private static CraftingGridProvider load(CraftingTweaksRegistrationData data) {
        String modId = data.getModId();
        if ((!modId.equals("minecraft") && !Balm.isModLoaded(modId)) || !isCompatEnabled(modId) || !data.isEnabled()) {
            return null;
        }

        CraftingGridProvider gridProvider = DataDrivenGridFactory.createGridProvider(data);
        if (gridProvider != null) {
            CraftingTweaksAPI.registerCraftingGridProvider(gridProvider);
            logger.info("{} has registered {} for CraftingTweaks", data.getModId(), data.getContainerClass());
        }
        return gridProvider;
    }

}
