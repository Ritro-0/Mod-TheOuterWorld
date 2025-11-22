package com.theouterworld.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.theouterworld.registry.ModDimensions;
import com.theouterworld.weather.DustStormManager;
import com.theouterworld.weather.DustStormTicker;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class DustStormCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
            literal("duststorm")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("start")
                    .executes(context -> startDustStorm(context))
                )
                .then(literal("stop")
                    .executes(context -> stopDustStorm(context))
                )
                .then(literal("status")
                    .executes(context -> getDustStormStatus(context))
                )
        );
    }
    
    private static int startDustStorm(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            source.sendError(Text.literal("Dust storms can only be triggered in the Outerworld dimension!"));
            return 0;
        }
        
        DustStormManager manager = DustStormTicker.getManager(world);
        if (manager != null) {
            manager.forceStorm(world);
        }
        
        source.sendFeedback(() -> Text.literal("Dust storm started!"), true);
        return 1;
    }
    
    private static int stopDustStorm(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            source.sendError(Text.literal("Dust storms can only be stopped in the Outerworld dimension!"));
            return 0;
        }
        
        DustStormManager manager = DustStormTicker.getManager(world);
        if (manager != null) {
            manager.endStorm(world);
            // Verify it was stopped
            boolean stillActive = manager.isStormActive();
            boolean stillRaining = world.isRaining();
            source.sendFeedback(() -> Text.literal(String.format(
                "Dust storm stop command executed. Manager active: %s, World raining: %s",
                stillActive, stillRaining
            )), true);
        } else {
            source.sendError(Text.literal("Failed to get dust storm manager!"));
            return 0;
        }
        
        return 1;
    }
    
    private static int getDustStormStatus(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            source.sendError(Text.literal("Dust storms only occur in the Outerworld dimension!"));
            return 0;
        }
        
        DustStormManager manager = DustStormTicker.getManager(world);
        if (manager == null) {
            source.sendError(Text.literal("Failed to get dust storm manager!"));
            return 0;
        }
        
        boolean managerActive = manager.isStormActive();
        boolean worldRaining = world.isRaining();
        boolean worldThundering = world.isThundering();
        
        source.sendFeedback(() -> Text.literal(String.format(
            "Dust storm status - Manager: %s, World Raining: %s, World Thundering: %s",
            managerActive, worldRaining, worldThundering
        )), false);
        
        return 1;
    }
}

