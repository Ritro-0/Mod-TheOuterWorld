package com.theouterworld.registry;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.worldgen.BasaltPillarFeature;
import com.theouterworld.worldgen.BasaltPillarFeatureConfig;
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

    public static void registerModFeatures() {
        OuterWorldMod.LOGGER.info("Registering features for " + OuterWorldMod.MOD_ID);
    }
}

