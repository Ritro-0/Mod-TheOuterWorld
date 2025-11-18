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

        // Random dimensions
        int height = 7 + random.nextInt(18); // 7-24 blocks tall
        int baseWidth = 5 + random.nextInt(8); // 5-12 blocks wide
        int baseLength = 5 + random.nextInt(8); // 5-12 blocks long
        
        // Ensure it's never 1 block thin (minimum 2x2)
        if (baseWidth < 2) baseWidth = 2;
        if (baseLength < 2) baseLength = 2;

        // Shape factor: 0.0 = rectangular prism, 1.0 = cylinder
        float shapeFactor = random.nextFloat();
        
        // Generate the main pillar body
        Set<BlockPos> interiorBlocks = new HashSet<>();
        Set<BlockPos> surfaceBlocks = new HashSet<>();
        
        int centerX = origin.getX();
        int centerZ = origin.getZ();
        int startY = surfaceY;
        int endY = surfaceY + height;
        
        // First, collect all blocks that will be part of the pillar
        Set<BlockPos> allPillarBlocks = new HashSet<>();
        
        // Generate pillar shape
        for (int y = startY; y < endY; y++) {
            // Taper the pillar slightly as it goes up
            float yProgress = (float)(y - startY) / height;
            float taper = 1.0f - (yProgress * 0.2f); // Taper up to 20%
            
            int currentWidth = Math.max(2, (int)(baseWidth * taper));
            int currentLength = Math.max(2, (int)(baseLength * taper));
            
            for (int x = -currentWidth / 2; x <= currentWidth / 2; x++) {
                for (int z = -currentLength / 2; z <= currentLength / 2; z++) {
                    BlockPos pos = new BlockPos(centerX + x, y, centerZ + z);
                    
                    // Check if this position is within the shape
                    double distX = (double)x / (currentWidth / 2.0);
                    double distZ = (double)z / (currentLength / 2.0);
                    double dist = Math.sqrt(distX * distX + distZ * distZ);
                    
                    // Interpolate between rectangle and circle
                    boolean inShape;
                    if (shapeFactor < 0.5f) {
                        // More rectangular
                        inShape = Math.abs(x) <= currentWidth / 2 && Math.abs(z) <= currentLength / 2;
                    } else {
                        // More circular
                        inShape = dist <= 1.0;
                    }
                    
                    if (inShape) {
                        allPillarBlocks.add(pos);
                    }
                }
            }
        }
        
        // Add jutting bits from sides
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
        
        // Place surface blocks (98% oxidized, 2% basalt)
        for (BlockPos pos : surfaceBlocks) {
            if (world.getBlockState(pos).isAir() || world.getBlockState(pos).isReplaceable()) {
                BlockState state = random.nextFloat() < 0.98f ? oxidizedBasalt : basalt;
                world.setBlockState(pos, state, 3);
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
        // Add 2-5 jutting bits
        int numJuts = 2 + random.nextInt(4);
        
        for (int i = 0; i < numJuts; i++) {
            // Random side (0=north, 1=south, 2=east, 3=west)
            int side = random.nextInt(4);
            // Random height along the pillar
            int jutY = startY + random.nextInt(endY - startY);
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
            
            // Create a small jutting bit (1-3 blocks)
            int jutLength = 1 + random.nextInt(3);
            for (int j = 0; j < jutLength; j++) {
                BlockPos jutPos = new BlockPos(jutX + jutDirX * j, jutY, jutZ + jutDirZ * j);
                
                // Make sure it's not too far from the pillar
                if (Math.abs(jutPos.getX() - centerX) <= baseWidth / 2 + 3 &&
                    Math.abs(jutPos.getZ() - centerZ) <= baseLength / 2 + 3) {
                    
                    allPillarBlocks.add(jutPos);
                }
            }
        }
    }
}

