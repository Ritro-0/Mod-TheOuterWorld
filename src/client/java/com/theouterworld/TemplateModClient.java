package com.theouterworld;

import com.theouterworld.client.DustStormHandler;
import com.theouterworld.network.DustStormSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class TemplateModClient implements ClientModInitializer {
	public static boolean isDustStormActive = false; // Synced from server via packet
	
	@Override
	public void onInitializeClient() {
		TemplateMod.LOGGER.info("Hello Fabric world from TheOuterWorld Client!");
		
		// Register packet receiver (packet type is registered in common init)
		ClientPlayNetworking.registerGlobalReceiver(DustStormSyncPacket.ID, (payload, context) -> {
			isDustStormActive = payload.active();
		});
		
		// Register dust storm handler (particles)
		DustStormHandler.register();
		
		// Register dust storm visual effects (FOV, overlay)
		com.theouterworld.client.DustStormVisualEffects.register();
	}
}

