package com.theouterworld.worldgen;

import com.mojang.serialization.Codec;
import com.theouterworld.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.HashSet;
import java.util.Set;

public class BasaltPillarFeature extends Feature<BasaltPillarFeatureConfig> {
    public BasaltPillarFeature(Codec<BasaltPillarFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<BasaltPillarFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        BasaltPillarFeatureConfig config = context.getConfig();

        // Get surface height
        int topY = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ());
        int bottomY = world.getBottomY();
        int surfaceY = topY;
        for (int y = topY; y >= bottomY; y--) {
            BlockPos checkPos = new BlockPos(origin.getX(), y, origin.getZ());
            if (!world.getBlockState(checkPos).isAir()) {
                surfaceY = y + 1;
                break;
            }
        }

        // Random dimensions based on config (clamped to safe values)
        int minHeight = Math.max(1, config.minHeight());
        int maxHeight = Math.max(minHeight, config.maxHeight());
        int height = minHeight + random.nextInt(maxHeight - minHeight + 1);

        int minWidth = Math.max(2, config.minWidth());
        int maxWidth = Math.max(minWidth, config.maxWidth());
        int baseWidth = minWidth + random.nextInt(maxWidth - minWidth + 1);
        int baseLength = minWidth + random.nextInt(maxWidth - minWidth + 1);

        // Generate the main pillar body (spike-like, pointy)
        Set<BlockPos> interiorBlocks = new HashSet<>();
        Set<BlockPos> surfaceBlocks = new HashSet<>();
        
        int centerX = origin.getX();
        int centerZ = origin.getZ();
        int startY = surfaceY;
        int endY = surfaceY + height;
        
        // First, collect all blocks that will be part of the pillar
        Set<BlockPos> allPillarBlocks = new HashSet<>();
        
        // Generate pillar shape (cone/pyramid-like by strong taper)
        for (int y = startY; y < endY; y++) {
            // Strong taper for spiky shape
            float yProgress = (float)(y - startY) / height;
            // Exponential taper to get a sharp point near the top
            double taper = Math.pow(1.0 - yProgress, 1.8); // 1.8 exponent makes pointier spikes

            int currentWidth = Math.max(1, (int)Math.round(baseWidth * taper));
            int currentLength = Math.max(1, (int)Math.round(baseLength * taper));

            for (int x = -currentWidth / 2; x <= currentWidth / 2; x++) {
                for (int z = -currentLength / 2; z <= currentLength / 2; z++) {
                    BlockPos pos = new BlockPos(centerX + x, y, centerZ + z);
                    
                    // Circular/elliptical cross-section that tightens with height
                    double normX = currentWidth <= 1 ? 0.0 : (double)x / (currentWidth / 2.0);
                    double normZ = currentLength <= 1 ? 0.0 : (double)z / (currentLength / 2.0);
                    double dist = Math.sqrt(normX * normX + normZ * normZ);
                    boolean inShape = dist <= 1.0;
                    
                    if (inShape) {
                        allPillarBlocks.add(pos);
                    }
                }
            }
        }
        
        // Add jutting bits from sides (more frequent, slight upward stepping for parkour)
        addJuttingBits(world, random, centerX, centerZ, startY, endY, baseWidth, baseLength, allPillarBlocks);
        
        // Now determine which blocks are surface (exposed to air or not part of pillar)
        for (BlockPos pos : allPillarBlocks) {
            boolean isSurface = isExposedToAirOrOutside(world, pos, allPillarBlocks);
            
            if (isSurface) {
                surfaceBlocks.add(pos);
            } else {
                interiorBlocks.add(pos);
            }
        }
        
        // Place blocks
        BlockState oxidizedBasalt = ModBlocks.OXIDIZED_BASALT.getDefaultState();
        BlockState basalt = Blocks.BASALT.getDefaultState();
        
        // Place surface blocks (100% oxidized)
        for (BlockPos pos : surfaceBlocks) {
            if (world.getBlockState(pos).isAir() || world.getBlockState(pos).isReplaceable()) {
                world.setBlockState(pos, oxidizedBasalt, 3);
            }
        }
        
        // Place interior blocks (60% oxidized, 40% basalt)
        for (BlockPos pos : interiorBlocks) {
            if (!surfaceBlocks.contains(pos) && (world.getBlockState(pos).isAir() || world.getBlockState(pos).isReplaceable())) {
                BlockState state = random.nextFloat() < 0.60f ? oxidizedBasalt : basalt;
                world.setBlockState(pos, state, 3);
            }
        }
        
        return true;
    }
    
    private boolean isExposedToAirOrOutside(StructureWorldAccess world, BlockPos pos, Set<BlockPos> pillarBlocks) {
        BlockPos[] neighbors = {
            pos.up(),
            pos.down(),
            pos.north(),
            pos.south(),
            pos.east(),
            pos.west()
        };
        
        for (BlockPos neighbor : neighbors) {
            // If neighbor is air in world OR not part of the pillar, this is a surface block
            if (world.getBlockState(neighbor).isAir() || !pillarBlocks.contains(neighbor)) {
                return true;
            }
        }
        return false;
    }
    
    private void addJuttingBits(StructureWorldAccess world, Random random, int centerX, int centerZ, 
                                int startY, int endY, int baseWidth, int baseLength,
                                Set<BlockPos> allPillarBlocks) {
        // Add 3-6 jutting bits for parkour possibilities
        int numJuts = 3 + random.nextInt(4);
        int totalHeight = Math.max(1, endY - startY);
        
        for (int i = 0; i < numJuts; i++) {
            // Random side (0=north, 1=south, 2=east, 3=west)
            int side = random.nextInt(4);
            // Random height along the pillar
            int jutY = startY + 2 + random.nextInt(Math.max(1, (endY - startY) - 4)); // avoid very top/bottom
            // Random offset along the side
            int offset = random.nextInt(Math.max(baseWidth, baseLength));
            
            int jutX = centerX;
            int jutZ = centerZ;
            int jutDirX = 0;
            int jutDirZ = 0;
            
            switch (side) {
                case 0: // North
                    jutZ = centerZ - baseLength / 2 - 1;
                    jutX = centerX - baseWidth / 2 + offset;
                    jutDirZ = -1;
                    break;
                case 1: // South
                    jutZ = centerZ + baseLength / 2 + 1;
                    jutX = centerX - baseWidth / 2 + offset;
                    jutDirZ = 1;
                    break;
                case 2: // East
                    jutX = centerX + baseWidth / 2 + 1;
                    jutZ = centerZ - baseLength / 2 + offset;
                    jutDirX = 1;
                    break;
                case 3: // West
                    jutX = centerX - baseWidth / 2 - 1;
                    jutZ = centerZ - baseLength / 2 + offset;
                    jutDirX = -1;
                    break;
            }
            
            // Recompute layer cross-section at jutY to get real boundary for attachment
            float yProgress = (float)(jutY - startY) / (float)totalHeight;
            double taper = Math.pow(1.0 - yProgress, 1.8);
            int layerWidth = Math.max(1, (int)Math.round(baseWidth * taper));
            int layerLength = Math.max(1, (int)Math.round(baseLength * taper));

            // Direction unit from side
            int dirX = jutDirX;
            int dirZ = jutDirZ;

            // Find last in-pillar block along direction at this layer to anchor the jut
            BlockPos lastInPillar = null;
            int maxScan = Math.max(layerWidth, layerLength) + 3;
            for (int s = 0; s <= maxScan; s++) {
                BlockPos candidate = new BlockPos(centerX + dirX * s, jutY, centerZ + dirZ * s);
                if (allPillarBlocks.contains(candidate)) {
                    lastInPillar = candidate;
                } else {
                    break;
                }
            }
            if (lastInPillar == null) {
                continue; // safety: couldn't find pillar at this layer/direction
            }

            // Start jut just outside the pillar boundary to ensure it's attached
            int startJutX = lastInPillar.getX() + dirX;
            int startJutZ = lastInPillar.getZ() + dirZ;
            int startJutY = lastInPillar.getY();

            // Create a small jutting bit (3-5 blocks) with slight upward stepping for parkour
            int jutLength = 3 + random.nextInt(3);
            int yStepEvery = 1 + random.nextInt(2); // step up every 1-2 blocks
            for (int j = 0; j < jutLength; j++) {
                int stepUp = (j > 0 && j % yStepEvery == 0) ? 1 : 0;
                BlockPos jutPos = new BlockPos(startJutX + dirX * j, startJutY + stepUp, startJutZ + dirZ * j);

                // Keep juts reasonably close
                if (Math.abs(jutPos.getX() - centerX) <= baseWidth / 2 + 4 &&
                    Math.abs(jutPos.getZ() - centerZ) <= baseLength / 2 + 4) {
                    allPillarBlocks.add(jutPos);
                }
            }
        }
    }
}

