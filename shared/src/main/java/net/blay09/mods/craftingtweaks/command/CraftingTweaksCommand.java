package net.blay09.mods.craftingtweaks.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.blay09.mods.craftingtweaks.CraftingTweaks;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CraftingTweaksCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("craftingtweaks")
                .then(Commands.literal("debug").executes(CraftingTweaksCommand::toggleCraftingTweaksDebug))
        );
    }

    private static int toggleCraftingTweaksDebug(CommandContext<CommandSourceStack> context) throws CommandRuntimeException {
        CommandSourceStack source = context.getSource();
        CraftingTweaks.debugMode = !CraftingTweaks.debugMode;
        source.sendSuccess(() -> Component.literal("Crafting Tweaks debug mode: " + CraftingTweaks.debugMode), true);
        return Command.SINGLE_SUCCESS;
    }

}
