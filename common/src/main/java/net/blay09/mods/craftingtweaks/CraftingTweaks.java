package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.balm.platform.event.callback.ItemCallback;
import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.command.CraftingTweaksCommand;
import net.blay09.mods.craftingtweaks.compat.VanillaCraftingGridProvider;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.crafting.ShapedRecipeMatrixMapper;
import net.blay09.mods.craftingtweaks.crafting.ShapelessRecipeMatrixMapper;
import net.blay09.mods.craftingtweaks.registry.ConfigJsonCompatLoader;
import net.blay09.mods.craftingtweaks.registry.ModFileJsonCompatLoader;
import net.blay09.mods.craftingtweaks.registry.LegacyJsonCompatLoader;
import net.blay09.mods.craftingtweaks.network.HelloMessage;
import net.blay09.mods.craftingtweaks.network.ModNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftingTweaks {

    public static final String MOD_ID = "craftingtweaks";
    public static boolean debugMode;

    public static final Logger logger = LoggerFactory.getLogger(CraftingTweaks.class);

    public static boolean isServerSideInstalled = true;

    public static void initialize(BalmRegistrars registrars) {
        CraftingTweaksConfig.initialize();
        ModNetworking.initialize(Balm.networking());

        Balm.commands().register(CraftingTweaksCommand::register);

        registrars.resourceReloadListeners(registrar
                -> registrar.register("json_registry", new LegacyJsonCompatLoader()));

        CraftingTweaksAPI.registerCraftingGridProvider(new VanillaCraftingGridProvider());
        CraftingTweaksAPI.registerRecipeMapper(ShapedRecipe.class, new ShapedRecipeMatrixMapper());
        CraftingTweaksAPI.registerRecipeMapper(ShapelessRecipe.class, new ShapelessRecipeMatrixMapper());

        ServerPlayerCallback.Login.EVENT.register(player -> Balm.networking().sendTo(player, HelloMessage.INSTANCE));
        ItemCallback.Craft.EVENT.register((player, itemStack, craftMatrix) -> {
            final var level = player.level();
            if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
                final var recipeManager = serverLevel.getServer().getRecipeManager();
                if (craftMatrix instanceof CraftingContainer craftingContainer) {
                    final var optionalRecipeHolder = recipeManager.getRecipeFor(RecipeType.CRAFTING, craftingContainer.asCraftInput(), level);
                    optionalRecipeHolder.ifPresent(recipeHolder -> CraftingTweaksAPI.setLastCraftedRecipe(serverPlayer, recipeHolder));
                }
            }
        });

        Balm.config().onConfigAvailable(CraftingTweaksConfig.class, config -> {
            ModFileJsonCompatLoader.load();
            ConfigJsonCompatLoader.load();
        });
    }

}
