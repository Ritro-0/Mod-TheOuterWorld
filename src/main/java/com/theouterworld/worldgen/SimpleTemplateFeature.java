package com.theouterworld.worldgen;

import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Optional;

public class SimpleTemplateFeature extends Feature<SimpleTemplateFeatureConfig> {
	public SimpleTemplateFeature() {
		super(SimpleTemplateFeatureConfig.CODEC);
	}
	
	@Override
	public boolean generate(FeatureContext<SimpleTemplateFeatureConfig> context) {
		StructureWorldAccess world = context.getWorld();
		BlockPos origin = context.getOrigin();
		var random = context.getRandom();
		
		Identifier templateId = context.getConfig().template();
		StructureTemplateManager manager = world.toServerWorld().getStructureTemplateManager();
		Optional<StructureTemplate> maybeTemplate = manager.getTemplate(templateId);
		if (maybeTemplate.isEmpty()) {
			return false;
		}
		StructureTemplate template = maybeTemplate.get();
		
		int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, origin.getX(), origin.getZ());
		BlockPos placePos = new BlockPos(origin.getX(), surfaceY + context.getConfig().yOffset(), origin.getZ());
		
		StructurePlacementData placement = new StructurePlacementData()
			.setMirror(BlockMirror.NONE)
			.setRotation(BlockRotation.NONE)
			.setIgnoreEntities(false)
			.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		
		return template.place(world, placePos, placePos, placement, random, 2);
	}
}


