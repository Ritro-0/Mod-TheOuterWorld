package com.theouterworld.worldgen;

import com.mojang.serialization.Codec;
import com.theouterworld.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class OxidizedBasaltPebbleFeature extends Feature<OxidizedBasaltPebbleFeatureConfig> {
    public OxidizedBasaltPebbleFeature(Codec<OxidizedBasaltPebbleFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<OxidizedBasaltPebbleFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        // Get the surface position at this location
        int surfaceY = world.getTopY(
            net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
            origin.getX(),
            origin.getZ()
        );

        BlockPos surfacePos = new BlockPos(origin.getX(), surfaceY, origin.getZ());
        BlockState currentState = world.getBlockState(surfacePos);

        // Only place if the position is air or replaceable, and we're on a solid surface
        if (currentState.isAir() || currentState.isReplaceable()) {
            BlockPos belowPos = surfacePos.down();
            BlockState belowState = world.getBlockState(belowPos);
            
            // Check if there's a solid block below to place the pebble on
            if (!belowState.isAir() && belowState.isSolidBlock(world, belowPos)) {
                BlockState pebbleState = ModBlocks.OXIDIZED_BASALT_PEBBLE.getDefaultState();
                world.setBlockState(surfacePos, pebbleState, 3);
                return true;
            }
        }

        return false;
    }
}

