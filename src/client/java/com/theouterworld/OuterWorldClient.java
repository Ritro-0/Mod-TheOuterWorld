package com.theouterworld;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.client.DustStormHandler;
import com.theouterworld.client.GlassHelmetOverlay;
import com.theouterworld.client.KnappingTableScreen;
import com.theouterworld.client.OxidizableIronGolemEntityRenderer;
import com.theouterworld.client.ProcessorScreen;
import com.theouterworld.network.DustStormSyncPacket;
import com.theouterworld.registry.ModEntities;
import com.theouterworld.registry.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;

public class OuterWorldClient implements ClientModInitializer {
	public static boolean isDustStormActive = false; // Synced from server via packet
	
	@Override
	public void onInitializeClient() {
		OuterWorldMod.LOGGER.info("Hello Fabric world from TheOuterWorld Client!");
		
		// Register packet receiver (packet type is registered in common init)
		ClientPlayNetworking.registerGlobalReceiver(DustStormSyncPacket.ID, (payload, context) -> {
			isDustStormActive = payload.active();
		});
		
		// Register dust storm handler (particles)
		DustStormHandler.register();
		
		// Register dust storm visual effects (FOV, overlay)
		com.theouterworld.client.DustStormVisualEffects.register();

		// Register glass helmet screen overlay
		GlassHelmetOverlay.register();

		// Martian moon renderer (MartianMoonRenderer) is now its own ClientModInitializer
		// No need to register it here - it initializes itself
		
		// Register transparent blocks for cutout render layer
		registerTransparentBlocks();
		
		// Register entity renderers
		registerEntityRenderers();
		
		// Register processor screen  
		HandledScreens.register(ModScreenHandlers.PROCESSOR_SCREEN_HANDLER, ProcessorScreen::new);
		
		// Register knapping table screen
		HandledScreens.register(ModScreenHandlers.KNAPPING_TABLE_SCREEN_HANDLER, KnappingTableScreen::new);
		
		// Note: The iron golem statue block entity renderer requires the new 1.21+ 
		// BlockEntityRenderState API which has complex type requirements.
		// The statues will render as invisible blocks until this is resolved.
		// The statue mechanics (oxidation, waxing, reanimation) all work.
	}
	
	private void registerEntityRenderers() {
		EntityRendererRegistry.register(ModEntities.OXIDIZABLE_IRON_GOLEM, OxidizableIronGolemEntityRenderer::new);
	}
	
	private void registerTransparentBlocks() {
		// Iron chains (including unaffected clone)
		BlockRenderLayerMap.putBlock(ModBlocks.UNAFFECTED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		
		// Iron doors (including unaffected clone and waxed vanilla)
		BlockRenderLayerMap.putBlock(ModBlocks.UNAFFECTED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		
		// Iron trapdoors (including unaffected clone and waxed vanilla)
		BlockRenderLayerMap.putBlock(ModBlocks.UNAFFECTED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		
		// Iron grates
		BlockRenderLayerMap.putBlock(ModBlocks.IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		
		// Iron bars (including unaffected clone and waxed vanilla)
		BlockRenderLayerMap.putBlock(ModBlocks.UNAFFECTED_IRON_BARS, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_BARS, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_BARS, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_BARS, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_BARS, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_BARS, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_BARS, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_IRON_BARS, BlockRenderLayer.CUTOUT);
	}
}


