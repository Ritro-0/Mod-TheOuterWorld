package com.theouterworld.client;

import com.theouterworld.OuterWorldClient;
import com.theouterworld.registry.ModDimensions;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class DustStormHandler {
    // Three-layer dust storm system:
    // Layer 1: Distant cloud (wall of dust approaching)
    // Layer 2: Intermediate sphere (fills gaps when player moves)
    // Layer 3: Dense personal fog (blinding effect around player)
    
    // Tick counters for spacing distant layers
    private static int distantLayerTickCounter = 0;
    private static int intermediateLayerTickCounter = 0;
    
    // Tick spacing: distant layers spawn every 3 ticks, intermediate every 2 ticks
    private static final int DISTANT_LAYER_TICK_INTERVAL = 3;
    private static final int INTERMEDIATE_LAYER_TICK_INTERVAL = 2;
    
    public static void register() {
        ClientTickEvents.END_WORLD_TICK.register(DustStormHandler::onWorldTick);
    }
    
    private static void onWorldTick(ClientWorld world) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        
        // Only spawn particles in the Outerworld dimension during dust storms (when it's raining)
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return;
        }
        
        // Use synced dust storm state from server (reliable, bypasses vanilla weather sync issues)
        if (!OuterWorldClient.isDustStormActive) {
            return;
        }
        
        // Get particle setting multiplier
        float particleMultiplier = getParticleMultiplier(client);
        
        Random random = world.getRandom();
        Vec3d eyePos = client.player.getEyePos();
        
        // Increment tick counters
        distantLayerTickCounter++;
        intermediateLayerTickCounter++;
        
        // Big distant wall of dust (the approaching storm) - spawn every 3 ticks
        if (distantLayerTickCounter >= DISTANT_LAYER_TICK_INTERVAL) {
            distantLayerTickCounter = 0;
            int particleCount = (int) (22 * particleMultiplier);
            for (int i = 0; i < particleCount; i++) {
                spawnParticle(world, eyePos, 55.0, random, false);
            }
        }
        
        // Intermediate sphere - fills gaps when player moves around - spawn every 2 ticks
        if (intermediateLayerTickCounter >= INTERMEDIATE_LAYER_TICK_INTERVAL) {
            intermediateLayerTickCounter = 0;
            int particleCount = (int) (28 * particleMultiplier);
            for (int i = 0; i < particleCount; i++) {
                spawnParticle(world, eyePos, 18.0, random, false);
            }
        }
        
        // Dense cloud around the player's head (blinds you) - spawn every tick (most important for visibility)
        int particleCount = (int) (38 * particleMultiplier);
        for (int i = 0; i < particleCount; i++) {
            spawnParticle(world, eyePos, 5.5, random, true);
        }
    }
    
    /**
     * Gets the particle multiplier based on player's particle settings.
     * @param client The Minecraft client instance
     * @return Multiplier: 1.0 for ALL, 0.5 for DECREASED, 0.2 for MINIMAL
     */
    private static float getParticleMultiplier(MinecraftClient client) {
        // Access particle setting via options - use toString() to get the enum name
        // This works regardless of the actual enum class name
        Object particleValue = client.options.getParticles().getValue();
        String particleName = particleValue.toString();
        
        // Match against known particle setting names (case-insensitive)
        if (particleName.equalsIgnoreCase("ALL") || particleName.contains("ALL")) {
            return 1.0f;
        } else if (particleName.equalsIgnoreCase("DECREASED") || particleName.contains("DECREASED")) {
            return 0.5f;
        } else if (particleName.equalsIgnoreCase("MINIMAL") || particleName.contains("MINIMAL")) {
            return 0.2f;
        }
        
        // Default to full particles if we can't determine the setting
        return 1.0f;
    }
    
    private static void spawnParticle(ClientWorld world, Vec3d center, double radius, Random random, boolean dense) {
        double theta = random.nextDouble() * Math.PI * 2.0;
        double phi = random.nextDouble() * Math.PI;
        double r = radius * Math.cbrt(random.nextDouble());
        
        double x = center.x + r * Math.sin(phi) * Math.cos(theta);
        double y = center.y + r * Math.cos(phi);
        double z = center.z + r * Math.sin(phi) * Math.sin(theta);
        
        // No velocity/wind effect - particles are stationary
        double vx = 0.0;
        double vy = 0.0;
        double vz = 0.0;
        
        // Use red sand falling dust particles
        BlockStateParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, Blocks.RED_SAND.getDefaultState());
        
        // Use addParticleClient with both flags for maximum rendering
        // Signature: addParticleClient(ParticleEffect, boolean alwaysSpawn, boolean decrease, double x, double y, double z, double vx, double vy, double vz)
        world.addParticleClient(
            particle,
            true,   // Force render even when far (alwaysSpawn)
            false,  // Don't decrease particle count (ignore distance culling effect)
            x, y, z,
            vx, vy, vz
        );
    }
}

