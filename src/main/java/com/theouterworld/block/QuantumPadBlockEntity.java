package com.theouterworld.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import java.util.Optional;
import com.theouterworld.world.QuantumPadLinkState;
import net.minecraft.text.Text;

public class QuantumPadBlockEntity extends BlockEntity {
    private BlockPos linkedPos = null; // Coordinates of the linked pad in the other dimension
    
    public QuantumPadBlockEntity(BlockPos pos, BlockState state) {
        super(com.theouterworld.registry.ModBlockEntities.QUANTUM_PAD, pos, state);
    }
    
    public BlockPos getLinkedPos() {
        return linkedPos;
    }
    
    public void setLinkedPos(BlockPos pos) {
        this.linkedPos = pos;
        this.markDirty();
    }
    
    public boolean hasLinkedPos() {
        return linkedPos != null;
    }
    
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("LinkedX") && nbt.contains("LinkedY") && nbt.contains("LinkedZ")) {
            Optional<Integer> xOpt = nbt.getInt("LinkedX");
            Optional<Integer> yOpt = nbt.getInt("LinkedY");
            Optional<Integer> zOpt = nbt.getInt("LinkedZ");
            if (xOpt.isPresent() && yOpt.isPresent() && zOpt.isPresent()) {
                linkedPos = new BlockPos(xOpt.get(), yOpt.get(), zOpt.get());
            } else {
                linkedPos = null;
            }
        } else {
            linkedPos = null;
        }
    }
    
    protected void writeNbt(NbtCompound nbt) {
        if (linkedPos != null) {
            nbt.putInt("LinkedX", linkedPos.getX());
            nbt.putInt("LinkedY", linkedPos.getY());
            nbt.putInt("LinkedZ", linkedPos.getZ());
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, QuantumPadBlockEntity blockEntity) {
        if (world.isClient()) return;
        
		// If link is severed in persistent memory, force deactivate and do not allow relight
		ServerWorld sw = (ServerWorld) world;
		if (QuantumPadLinkState.get(sw.getServer()).isSevered(world.getRegistryKey(), pos)) {
			if (state.get(QuantumPadBlock.ACTIVATED)) {
				world.setBlockState(pos, state.with(QuantumPadBlock.ACTIVATED, false));
				sw.getServer().getPlayerManager().broadcast(
					Text.literal("[QuantumPad] Pad at " + QuantumPadLinkState.formatPos(pos) + " in " + QuantumPadLinkState.formatWorldName(world.getRegistryKey()) + " deactivated due to severed link."),
					false
				);
			}
			return;
		}
		
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
