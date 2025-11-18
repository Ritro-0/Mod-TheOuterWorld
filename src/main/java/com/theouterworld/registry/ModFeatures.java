package com.theouterworld.registry;

import com.theouterworld.TemplateMod;
import com.theouterworld.worldgen.BasaltPillarFeature;
import com.theouterworld.worldgen.BasaltPillarFeatureConfig;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
    public static final Feature<BasaltPillarFeatureConfig> BASALT_PILLAR = Registry.register(
        Registries.FEATURE,
        Identifier.of(TemplateMod.MOD_ID, "basalt_pillar"),
        new BasaltPillarFeature(BasaltPillarFeatureConfig.CODEC)
    );

    public static void registerModFeatures() {
        TemplateMod.LOGGER.info("Registering features for " + TemplateMod.MOD_ID);
    }
}

