package com.theouterworld;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.command.DustStormCommand;
import com.theouterworld.config.OuterworldConfig;
import com.theouterworld.item.ModItemGroups;
import com.theouterworld.network.DustStormSyncPacket;
import com.theouterworld.particle.ModParticles;
import com.theouterworld.registry.ModBlockEntities;
import com.theouterworld.registry.ModDimensions;
import com.theouterworld.registry.ModFeatures;
import com.theouterworld.weather.DustStormTicker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateMod implements ModInitializer {
	public static final String MOD_ID = "theouterworld";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world from TheOuterWorld!");
		
		// Register and load configuration
		OuterworldConfig.register();
		OuterworldConfig config = OuterworldConfig.get();
		LOGGER.info("Loaded Outerworld config: gravityMultiplier={}", config.gravityMultiplier);
		
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModParticles.registerModParticles();
		ModDimensions.registerModDimensions();
		ModFeatures.registerModFeatures();
		
		// Register packet types (register once on common/server side)
		PayloadTypeRegistry.playS2C().register(DustStormSyncPacket.ID, DustStormSyncPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(DustStormSyncPacket.ID, DustStormSyncPacket.CODEC);
		
		// Register dust storm ticker
		DustStormTicker.register();
		
		// Register dust storm effects (slowness, mining fatigue)
		com.theouterworld.weather.DustStormEffects.register();
		
		// Sync dust storm state when player joins
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			server.execute(() -> {
				net.minecraft.server.world.ServerWorld world = (net.minecraft.server.world.ServerWorld) handler.player.getEntityWorld();
				if (world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
					var manager = DustStormTicker.getManager(world);
					if (manager != null) {
						DustStormSyncPacket packet = new DustStormSyncPacket(manager.isStormActive());
						ServerPlayNetworking.send(handler.player, packet);
					}
				}
			});
		});
		
		// Register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			DustStormCommand.register(dispatcher, registryAccess, environment);
		});
	}
}

