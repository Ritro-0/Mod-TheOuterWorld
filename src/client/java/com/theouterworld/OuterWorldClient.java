package com.theouterworld;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.client.DustStormHandler;
import com.theouterworld.client.GlassHelmetOverlay;
import com.theouterworld.network.DustStormSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
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
	}
	
	private void registerTransparentBlocks() {
		// Iron chains
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_CHAIN, BlockRenderLayer.CUTOUT);
		
		// Iron doors
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_DOOR, BlockRenderLayer.CUTOUT);
		
		// Iron trapdoors
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_TRAPDOOR, BlockRenderLayer.CUTOUT);
		
		// Iron grates
		BlockRenderLayerMap.putBlock(ModBlocks.IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.EXPOSED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WEATHERED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.OXIDIZED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_EXPOSED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_WEATHERED_IRON_GRATE, BlockRenderLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(ModBlocks.WAXED_OXIDIZED_IRON_GRATE, BlockRenderLayer.CUTOUT);
	}
}


