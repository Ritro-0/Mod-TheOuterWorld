package com.theouterworld.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record BasaltPillarFeatureConfig(int minHeight, int maxHeight, int minWidth, int maxWidth) implements FeatureConfig {
    public static final Codec<BasaltPillarFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("minHeight").forGetter(BasaltPillarFeatureConfig::minHeight),
            Codec.INT.fieldOf("maxHeight").forGetter(BasaltPillarFeatureConfig::maxHeight),
            Codec.INT.fieldOf("minWidth").forGetter(BasaltPillarFeatureConfig::minWidth),
            Codec.INT.fieldOf("maxWidth").forGetter(BasaltPillarFeatureConfig::maxWidth)
        ).apply(instance, BasaltPillarFeatureConfig::new)
    );
}

