package net.blay09.mods.craftingtweaks;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.ItemCraftedEvent;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.command.CraftingTweaksCommand;
import net.blay09.mods.craftingtweaks.compat.VanillaCraftingGridProvider;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.blay09.mods.craftingtweaks.crafting.ShapedRecipeMatrixMapper;
import net.blay09.mods.craftingtweaks.crafting.ShapelessRecipeMatrixMapper;
import net.blay09.mods.craftingtweaks.registry.JsonCompatLoader;
import net.blay09.mods.craftingtweaks.network.HelloMessage;
import net.blay09.mods.craftingtweaks.network.ModNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class CraftingTweaks {

    public static final String MOD_ID = "craftingtweaks";
    public static boolean debugMode;

    public static boolean isServerSideInstalled = true;

    public static void initialize() {
        CraftingTweaksConfig.initialize();
        ModNetworking.initialize(Balm.getNetworking());

        Balm.getCommands().register(CraftingTweaksCommand::register);

        Balm.addServerReloadListener(ResourceLocation.fromNamespaceAndPath(MOD_ID, "json_registry"), new JsonCompatLoader());

        CraftingTweaksAPI.registerCraftingGridProvider(new VanillaCraftingGridProvider());
        CraftingTweaksAPI.registerRecipeMatrixMapper(ShapedRecipe.class, new ShapedRecipeMatrixMapper());
        CraftingTweaksAPI.registerRecipeMatrixMapper(ShapelessRecipe.class, new ShapelessRecipeMatrixMapper());

        Balm.getEvents().onEvent(PlayerLoginEvent.class, event -> Balm.getNetworking().sendTo(event.getPlayer(), new HelloMessage()));
        Balm.getEvents().onEvent(ItemCraftedEvent.class, event -> {
            final var player = event.getPlayer();
            final var level = player.level();
            final var craftMatrix = event.getCraftMatrix();
            final var recipeManager = level.getRecipeManager();
            if (craftMatrix instanceof CraftingContainer craftingContainer) {
                final var optionalRecipeHolder = recipeManager.getRecipeFor(RecipeType.CRAFTING, craftingContainer.asCraftInput(), level);
                optionalRecipeHolder.ifPresent(recipeHolder -> CraftingTweaksAPI.setLastCraftedRecipe(player, recipeHolder));
            }
        });
    }

}
