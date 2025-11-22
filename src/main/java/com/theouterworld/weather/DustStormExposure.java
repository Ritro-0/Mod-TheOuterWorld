package com.theouterworld.weather;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks dust storm exposure for each player, similar to how powdered snow tracks freezing.
 * Exposure builds up over time when in a dust storm and decays when out of it.
 */
public class DustStormExposure {
    private static final Map<UUID, ExposureData> playerExposure = new HashMap<>();
    
    private static class ExposureData {
        int exposureTicks = 0;
        
        void enterDuststorm() {
            if (exposureTicks < 0) exposureTicks = 0;
        }
        
        void exitDuststorm() {
            exposureTicks = -30; // Short grace period before decay starts
        }
        
        void serverTick() {
            // Cap maximum exposure at 120 ticks (6 seconds at 20 TPS)
            if (exposureTicks > 120) exposureTicks = 120;
            
            // Increment if positive, decrement if negative (grace period)
            if (exposureTicks > -30) {
                exposureTicks += exposureTicks >= 0 ? 1 : -1;
            }
        }
        
        float getIntensity(float partialTick) {
            // Returns 0.0 to 1.0 based on exposure, same formula as powdered snow
            return MathHelper.clamp((exposureTicks + partialTick) / 120f, 0f, 1f);
        }
        
        int getExposureTicks() {
            return exposureTicks;
        }
    }
    
    public static void enterDuststorm(PlayerEntity player) {
        playerExposure.computeIfAbsent(player.getUuid(), k -> new ExposureData()).enterDuststorm();
    }
    
    public static void exitDuststorm(PlayerEntity player) {
        ExposureData data = playerExposure.get(player.getUuid());
        if (data != null) {
            data.exitDuststorm();
        }
    }
    
    public static void serverTick(PlayerEntity player) {
        ExposureData data = playerExposure.get(player.getUuid());
        if (data != null) {
            data.serverTick();
        }
    }
    
    public static float getIntensity(PlayerEntity player, float partialTick) {
        ExposureData data = playerExposure.get(player.getUuid());
        if (data == null) return 0f;
        return data.getIntensity(partialTick);
    }
    
    public static int getExposureTicks(PlayerEntity player) {
        ExposureData data = playerExposure.get(player.getUuid());
        if (data == null) return 0;
        return data.getExposureTicks();
    }
    
    public static void removePlayer(PlayerEntity player) {
        playerExposure.remove(player.getUuid());
    }
}

