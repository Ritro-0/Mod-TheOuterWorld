package com.theouterworld.registry;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.worldgen.BasaltPillarFeature;
import com.theouterworld.worldgen.BasaltPillarFeatureConfig;
import com.theouterworld.worldgen.OxidizedBasaltPebbleFeature;
import com.theouterworld.worldgen.OxidizedBasaltPebbleFeatureConfig;
import com.theouterworld.worldgen.SimpleTemplateFeature;
import com.theouterworld.worldgen.SimpleTemplateFeatureConfig;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
    public static final Feature<BasaltPillarFeatureConfig> BASALT_PILLAR = Registry.register(
        Registries.FEATURE,
        Identifier.of(OuterWorldMod.MOD_ID, "basalt_pillar"),
        new BasaltPillarFeature(BasaltPillarFeatureConfig.CODEC)
    );
    
    public static final Feature<SimpleTemplateFeatureConfig> SIMPLE_TEMPLATE = Registry.register(
        Registries.FEATURE,
        Identifier.of(OuterWorldMod.MOD_ID, "simple_template"),
        new SimpleTemplateFeature()
    );

    public static final Feature<OxidizedBasaltPebbleFeatureConfig> OXIDIZED_BASALT_PEBBLE = Registry.register(
        Registries.FEATURE,
        Identifier.of(OuterWorldMod.MOD_ID, "oxidized_basalt_pebble"),
        new OxidizedBasaltPebbleFeature(OxidizedBasaltPebbleFeatureConfig.CODEC)
    );

    public static void registerModFeatures() {
        OuterWorldMod.LOGGER.info("Registering features for " + OuterWorldMod.MOD_ID);
        
        // TODO: Add basalt cathedral structure
        // - Create BasaltCathedralFeature class or use SimpleTemplateFeature
        // - Should be a large structure made of oxidized basalt blocks
        // - Should contain lunar warden boss fight arena
        // - Should have unique loot and rewards
        // - Should spawn in specific biomes or at specific locations
        // - Register structure in worldgen configuration
    }
}

