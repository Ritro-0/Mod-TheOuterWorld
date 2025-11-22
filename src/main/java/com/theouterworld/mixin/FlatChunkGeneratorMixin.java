package com.theouterworld.mixin;

import com.theouterworld.registry.ModDimensions;
import com.theouterworld.registry.ModFeatures;
import com.theouterworld.worldgen.BasaltPillarFeatureConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public class FlatChunkGeneratorMixin {
    
    @Inject(
        method = "populateEntities",
        at = @At("RETURN")
    )
    private void addBasaltPillars(net.minecraft.world.ChunkRegion region, CallbackInfo ci) {
        // Get the chunk from the region
        net.minecraft.util.math.ChunkPos chunkPos = region.getCenterPos();
        net.minecraft.world.chunk.Chunk chunk = region.getChunk(chunkPos.x, chunkPos.z);
        
        // Get the world from the region
        net.minecraft.server.world.ServerWorld world = region.toServerWorld();
        
        // Only generate pillars in the Outerworld dimension
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return;
        }
        
        // Only generate for flat chunks (check if it's a flat generator)
        if (!(world.getChunkManager().getChunkGenerator() instanceof net.minecraft.world.gen.chunk.FlatChunkGenerator)) {
            return;
        }
        
        // Generate 1-3 pillars per chunk
        long seed = world.getSeed();
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        net.minecraft.util.math.random.Random random = net.minecraft.util.math.random.Random.create(
            seed + chunkX * 341873128712L + chunkZ * 132897987541L
        );
        
        int numPillars = 1 + random.nextInt(3);
        
        // Get height limits from dimension
        int topY = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, chunkX * 16 + 8, chunkZ * 16 + 8);
        int bottomY = world.getBottomY();
        
        for (int i = 0; i < numPillars; i++) {
            // Random position within chunk (avoid edges to prevent overlap)
            int x = chunkX * 16 + 4 + random.nextInt(8);
            int z = chunkZ * 16 + 4 + random.nextInt(8);
            
            // Get surface height by scanning from top down
            int surfaceY = topY;
            for (int y = topY; y >= bottomY; y--) {
                BlockPos checkPos = new BlockPos(x, y, z);
                if (!region.getBlockState(checkPos).isAir()) {
                    surfaceY = y + 1;
                    break;
                }
            }
            
            BlockPos origin = new BlockPos(x, surfaceY, z);
            
            // Create feature config
            BasaltPillarFeatureConfig config = new BasaltPillarFeatureConfig(7, 24, 5, 12);
            
            // Generate the pillar using FeatureContext
            // Constructor signature: (Optional<ConfiguredFeature>, StructureWorldAccess, ChunkGenerator, Random, BlockPos, FeatureConfig)
            net.minecraft.world.gen.chunk.ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
            
            net.minecraft.world.gen.feature.util.FeatureContext<BasaltPillarFeatureConfig> context = 
                new net.minecraft.world.gen.feature.util.FeatureContext<BasaltPillarFeatureConfig>(
                    java.util.Optional.empty(), // Optional<ConfiguredFeature> - empty for manual generation
                    region,
                    chunkGenerator,
                    random,
                    origin,
                    config
                );
            
            ModFeatures.BASALT_PILLAR.generate(context);
        }
    }
}

