package net.blay09.mods.craftingtweaks.registry;

import com.google.gson.Gson;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonCompatLoader implements ResourceManagerReloadListener {

    private static final Logger logger = LoggerFactory.getLogger(JsonCompatLoader.class);
    private static final Gson gson = new Gson();
    private static final FileToIdConverter COMPAT_JSONS = FileToIdConverter.json("craftingtweaks_compat");

    private final List<CraftingGridProvider> providersFromDataPacks = new ArrayList<>();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for (CraftingGridProvider providersFromDataPack : providersFromDataPacks) {
            CraftingTweaksAPI.unregisterCraftingGridProvider(providersFromDataPack);
        }
        providersFromDataPacks.clear();

        for (Map.Entry<ResourceLocation, Resource> entry : COMPAT_JSONS.listMatchingResources(resourceManager).entrySet()) {
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                CraftingGridProvider gridProvider = load(gson.fromJson(reader, CraftingTweaksRegistrationData.class));
                if (gridProvider != null) {
                    providersFromDataPacks.add(gridProvider);
                }
            } catch (Exception e) {
                logger.error("Parsing error loading CraftingTweaks data file at {}", entry.getKey(), e);
            }
        }
    }

    private static boolean isCompatEnabled(String modId) {
        return !CraftingTweaksConfig.getActive().client.disabledAddons.contains(modId);
    }

    private static CraftingGridProvider load(CraftingTweaksRegistrationData data) {
        String modId = data.getModId();
        if ((!modId.equals("minecraft") && !Balm.isModLoaded(modId)) || !isCompatEnabled(modId)) {
            return null;
        }

        CraftingGridProvider gridProvider = DataDrivenGridFactory.createGridProvider(data);
        CraftingTweaksAPI.registerCraftingGridProvider(gridProvider);
        logger.info("{} has registered {} for CraftingTweaks via data pack", data.getModId(), data.getContainerClass());
        return gridProvider;
    }

}
