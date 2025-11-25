package com.theouterworld.block;

import com.theouterworld.registry.ModDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class OxidizableIronBehavior {
    /**
     * Check if a block state is an unwaxed oxidizable iron block
     */
    public static boolean isUnwaxedOxidizableIron(BlockState state) {
        return state.getBlock() instanceof OxidizableIronBlock;
    }

    /**
     * Count nearby unwaxed oxidizable iron blocks at or above the given oxidation level
     * Checks all 26 neighbors (3x3x3 minus center)
     */
    public static int countNearbyUnwaxedOxidizableIron(WorldView world, BlockPos pos, Oxidizable.OxidationLevel minLevel) {
        int count = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Skip center
                    
                    mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState neighborState = world.getBlockState(mutable);
                    
                    if (isUnwaxedOxidizableIron(neighborState)) {
                        OxidizableIronBlock neighborBlock = (OxidizableIronBlock) neighborState.getBlock();
                        if (neighborBlock.getDegradationLevel().ordinal() >= minLevel.ordinal()) {
                            count++;
                        }
                    }
                }
            }
        }
        
        return count;
    }

    /**
     * Get the oxidation chance based on current level and neighbor count
     * Based on vanilla CopperBlock logic
     */
    public static float getOxidationChance(Oxidizable.OxidationLevel currentLevel, int higherNeighborCount) {
        if (currentLevel == Oxidizable.OxidationLevel.OXIDIZED) {
            return 0.0f; // Can't oxidize further
        }
        
        float baseChance = 0.05688889f; // Base chance from vanilla
        float neighborMultiplier = 1.0f + (higherNeighborCount * 0.05f); // 5% increase per neighbor
        
        // Stage-specific multipliers (from vanilla)
        float stageMultiplier = switch (currentLevel) {
            case UNAFFECTED -> 1.0f;
            case EXPOSED -> 0.75f;
            case WEATHERED -> 0.5f;
            case OXIDIZED -> 0.0f;
        };
        
        return baseChance * neighborMultiplier * stageMultiplier;
    }

    /**
     * Get the delay before next oxidation check
     * Based on vanilla CopperBlock delays
     */
    public static int getNextOxidationDelay(Oxidizable.OxidationLevel currentLevel) {
        return switch (currentLevel) {
            case UNAFFECTED -> 50; // ~2.5 seconds
            case EXPOSED -> 100; // ~5 seconds
            case WEATHERED -> 200; // ~10 seconds
            case OXIDIZED -> Integer.MAX_VALUE; // Never
        };
    }

    /**
     * Check if oxidation should occur in this dimension
     * Only oxidizes in the Outerworld dimension
     */
    public static boolean shouldOxidize(ServerWorld world, BlockPos pos) {
        return world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY);
    }
}

