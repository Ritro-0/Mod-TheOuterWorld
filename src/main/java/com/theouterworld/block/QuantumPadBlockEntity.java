package com.theouterworld.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class QuantumPadBlockEntity extends BlockEntity {
    public QuantumPadBlockEntity(BlockPos pos, BlockState state) {
        super(com.theouterworld.registry.ModBlockEntities.QUANTUM_PAD, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, QuantumPadBlockEntity blockEntity) {
        if (world.isClient()) return;
        
        // Only spawn particles if activated
        if (state.get(QuantumPadBlock.ACTIVATED)) {
            spawnContinuousParticles((ServerWorld) world, pos);
        }
    }

    private static void spawnContinuousParticles(ServerWorld world, BlockPos pos) {
        Random random = world.getRandom();
        
        // Spawn shriek particles going upward
        if (random.nextInt(10) == 0) { // 10% chance per tick
            double centerX = pos.getX() + 0.5;
            double centerZ = pos.getZ() + 0.5;
            double startY = pos.getY() + 1.0;
            
            // Spawn shriek particles going up
            for (int i = 0; i < 3; i++) {
                double y = startY + (i * 0.3);
                int delay = i * 5;
                ShriekParticleEffect shriekEffect = new ShriekParticleEffect(delay);
                world.spawnParticles(
                    shriekEffect,
                    centerX, y, centerZ,
                    1, 0.1, 0.1, 0.1, 0.0
                );
            }
        }
        
        // Spawn flame particles around the base
        if (random.nextInt(5) == 0) { // 20% chance per tick
            double centerX = pos.getX() + 0.5;
            double centerZ = pos.getZ() + 0.5;
            double y = pos.getY() + 0.1;
            
            // Create a circle of flame particles
            int particleCount = 8;
            for (int i = 0; i < particleCount; i++) {
                double angle = (i * 2 * Math.PI) / particleCount;
                double radius = 0.4;
                
                double x = centerX + Math.cos(angle) * radius;
                double z = centerZ + Math.sin(angle) * radius;
                
                world.spawnParticles(
                    ParticleTypes.FLAME,
                    x, y, z,
                    1, 0.05, 0.05, 0.05, 0.02
                );
            }
        }
    }
}
