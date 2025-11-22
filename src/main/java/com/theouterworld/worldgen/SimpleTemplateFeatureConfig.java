package com.theouterworld.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public record SimpleTemplateFeatureConfig(Identifier template, int yOffset) implements FeatureConfig {
	public static final Codec<SimpleTemplateFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Identifier.CODEC.fieldOf("template").forGetter(SimpleTemplateFeatureConfig::template),
			Codec.INT.fieldOf("yOffset").orElse(0).forGetter(SimpleTemplateFeatureConfig::yOffset)
		).apply(instance, SimpleTemplateFeatureConfig::new)
	);
}


