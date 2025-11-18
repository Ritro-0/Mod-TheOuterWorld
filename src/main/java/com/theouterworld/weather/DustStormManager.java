package com.theouterworld.weather;

import com.theouterworld.network.DustStormSyncPacket;
import com.theouterworld.registry.ModDimensions;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DustStormManager {
    private static final int MIN_STORM_INTERVAL = 15 * 60 * 20; // 15 minutes in ticks
    private static final int MAX_STORM_INTERVAL = 30 * 60 * 20; // 30 minutes in ticks
    private static final int MIN_STORM_DURATION = 2 * 60 * 20; // 2 minutes in ticks
    private static final int MAX_STORM_DURATION = 5 * 60 * 20; // 5 minutes in ticks
    
    private long nextStormTime = -1;
    private long stormEndTime = -1;
    private boolean stormActive = false;
    
    public void tick(ServerWorld world) {
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return;
        }
        
        long currentTime = world.getTime();
        Random random = world.getRandom();
        
        // If no storm is scheduled and none is active, schedule a new one
        if (!stormActive && nextStormTime == -1) {
            int interval = MIN_STORM_INTERVAL + random.nextInt(MAX_STORM_INTERVAL - MIN_STORM_INTERVAL);
            nextStormTime = currentTime + interval;
        }
        
        // Start the storm when it's time
        if (!stormActive && nextStormTime != -1 && currentTime >= nextStormTime) {
            startStorm(world, random);
        }
        
        // End the storm when it's time
        if (stormActive && stormEndTime != -1 && currentTime >= stormEndTime) {
            endStorm(world);
        }
    }
    
    public void startStorm(ServerWorld world, Random random) {
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return;
        }
        
        if (stormActive) return; // Already active
        
        stormActive = true;
        int duration = MIN_STORM_DURATION + random.nextInt(MAX_STORM_DURATION - MIN_STORM_DURATION);
        stormEndTime = world.getTime() + duration;
        nextStormTime = -1;
        
        // Don't use vanilla rain mechanics - Mars doesn't rain water!
        // The dust storm uses particles and sync packets instead
        
        // Sync state to all clients via packet
        syncToAllClients(world);
        
        com.theouterworld.TemplateMod.LOGGER.info("Dust storm started! Duration: {} ticks ({} seconds)", 
            duration, duration / 20);
    }
    
    public void endStorm(ServerWorld world) {
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return;
        }
        
        if (!stormActive) return; // Already inactive
        
        stormActive = false;
        stormEndTime = -1;
        nextStormTime = -1;
        
        // Don't use vanilla weather mechanics - we never set rain, so no need to clear it
        
        // Sync state to all clients via packet
        syncToAllClients(world);
        
        com.theouterworld.TemplateMod.LOGGER.info("Dust storm stopped!");
    }
    
    public void forceStorm(ServerWorld world) {
        Random random = world.getRandom();
        startStorm(world, random);
    }
    
    public boolean isStormActive() {
        return stormActive;
    }
    
    private void syncToAllClients(ServerWorld world) {
        DustStormSyncPacket packet = new DustStormSyncPacket(this.stormActive);
        PlayerLookup.world(world).forEach(player -> 
            ServerPlayNetworking.send(player, packet)
        );
    }
}

