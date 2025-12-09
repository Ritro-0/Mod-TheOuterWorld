package com.theouterworld.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public record OxidizedBasaltPebbleFeatureConfig() implements FeatureConfig {
    public static final Codec<OxidizedBasaltPebbleFeatureConfig> CODEC = Codec.unit(new OxidizedBasaltPebbleFeatureConfig());
}

