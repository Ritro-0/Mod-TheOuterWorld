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
        
        Random random = world.getRandom();
        Vec3d eyePos = client.player.getEyePos();
        
        // Big distant wall of dust (the approaching storm)
        for (int i = 0; i < 22; i++) {
            spawnParticle(world, eyePos, 55.0, random, false);
        }
        
        // Intermediate sphere - fills gaps when player moves around
        for (int i = 0; i < 28; i++) {
            spawnParticle(world, eyePos, 18.0, random, false);
        }
        
        // Dense cloud around the player's head (blinds you)
        for (int i = 0; i < 38; i++) {
            spawnParticle(world, eyePos, 5.5, random, true);
        }
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

