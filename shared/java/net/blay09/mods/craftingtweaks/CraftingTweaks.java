package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.network.HelloMessage;
import net.blay09.mods.craftingtweaks.network.ModNetworking;
import net.blay09.mods.balm.event.BalmEvents;
import net.blay09.mods.balm.network.BalmNetworking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CraftingTweaks {

    public static final Logger logger = LogManager.getLogger();
    public static final String MOD_ID = "craftingtweaks";

    public static boolean isServerSideInstalled;

    public static void initialize() {
        CraftingTweaksConfig.initialize();
        ModNetworking.initialize();

        CraftingTweaksAPI.setupAPI(new InternalMethodsImpl());

        Compatibility.vanilla();

        BalmEvents.onPlayerLogin(player -> BalmNetworking.sendTo(player, new HelloMessage()));
    }

}
