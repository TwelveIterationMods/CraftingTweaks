package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.command.CraftingTweaksCommand;
import net.blay09.mods.craftingtweaks.compat.VanillaCraftingGridProvider;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.registry.JsonCompatLoader;
import net.blay09.mods.craftingtweaks.network.HelloMessage;
import net.blay09.mods.craftingtweaks.network.ModNetworking;
import net.minecraft.resources.ResourceLocation;

public class CraftingTweaks {

    public static final String MOD_ID = "craftingtweaks";
    public static boolean debugMode;

    public static boolean isServerSideInstalled = true;

    public static void initialize() {
        CraftingTweaksConfig.initialize();
        ModNetworking.initialize(Balm.getNetworking());

        Balm.getCommands().register(CraftingTweaksCommand::register);

        Balm.addServerReloadListener(new ResourceLocation(MOD_ID, "json_registry"), new JsonCompatLoader());

        CraftingTweaksAPI.registerCraftingGridProvider(new VanillaCraftingGridProvider());

        Balm.getEvents().onEvent(PlayerLoginEvent.class, event -> Balm.getNetworking().sendTo(event.getPlayer(), new HelloMessage()));
    }

}
