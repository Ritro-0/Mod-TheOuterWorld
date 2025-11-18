package com.theouterworld.weather;

import com.theouterworld.registry.ModDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;

public class DustStormTicker {
    private static final Map<ServerWorld, DustStormManager> managers = new HashMap<>();
    
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
                DustStormManager manager = managers.computeIfAbsent(world, w -> new DustStormManager());
                manager.tick(world);
            }
        });
    }
    
    public static DustStormManager getManager(ServerWorld world) {
        if (world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return managers.computeIfAbsent(world, w -> new DustStormManager());
        }
        return null;
    }
}

