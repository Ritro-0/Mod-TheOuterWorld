package com.theouterworld;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.command.DustStormCommand;
import com.theouterworld.config.OuterworldConfig;
import com.theouterworld.event.VanillaIronReplacementListener;
import com.theouterworld.event.VanillaCopperReplacementListener;
import com.theouterworld.item.ModItemGroups;
import com.theouterworld.item.ModItems;
import com.theouterworld.block.ProcessorBlockEntity;
import com.theouterworld.network.DustStormSyncPacket;
import com.theouterworld.network.ProcessorModeTogglePacket;
import com.theouterworld.particle.ModParticles;
import com.theouterworld.registry.ModBlockEntities;
import com.theouterworld.registry.ModDimensions;
import com.theouterworld.registry.ModEntities;
import com.theouterworld.registry.ModFeatures;
import com.theouterworld.registry.ModScreenHandlers;
import com.theouterworld.weather.DustStormTicker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OuterWorldMod implements ModInitializer {
	public static final String MOD_ID = "theouterworld";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world from TheOuterWorld!");
		
		// Register and load configuration
		OuterworldConfig.register();
		OuterworldConfig config = OuterworldConfig.get();
		LOGGER.info("Loaded Outerworld config: gravityMultiplier={}", config.gravityMultiplier);
		
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModScreenHandlers.registerModScreenHandlers();
		ModParticles.registerModParticles();
		ModDimensions.registerModDimensions();
		ModFeatures.registerModFeatures();
		ModEntities.registerModEntities();
		
		// Register packet types (register once on common/server side)
		PayloadTypeRegistry.playS2C().register(DustStormSyncPacket.ID, DustStormSyncPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(DustStormSyncPacket.ID, DustStormSyncPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(ProcessorModeTogglePacket.ID, ProcessorModeTogglePacket.CODEC);
		
		// Handle processor mode toggle packets
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(
			ProcessorModeTogglePacket.ID,
			(packet, context) -> {
				context.server().execute(() -> {
					var player = context.player();
					var world = (net.minecraft.server.world.ServerWorld) player.getEntityWorld();
					var pos = packet.pos();
					LOGGER.info("[Processor] Toggle packet received for position: {}", pos);
					var blockEntity = world.getBlockEntity(pos);
					if (blockEntity instanceof ProcessorBlockEntity processor) {
						// Verify player is close enough
						double distance = player.squaredDistanceTo(
							pos.getX() + 0.5,
							pos.getY() + 0.5,
							pos.getZ() + 0.5);
						LOGGER.info("[Processor] BlockEntity found, distance: {}, heatMode before: {}", distance, processor.isHeatMode());
						if (distance < 64) {
							processor.toggleMode();
							LOGGER.info("[Processor] Mode toggled! New heatMode: {}", processor.isHeatMode());
						} else {
							LOGGER.warn("[Processor] Player too far away: {}", distance);
						}
					} else {
						LOGGER.warn("[Processor] No BlockEntity found at position: {}", pos);
					}
				});
			}
		);
		
		// Register dust storm ticker
		DustStormTicker.register();
		
		// Register dust storm effects (slowness, mining fatigue)
		com.theouterworld.weather.DustStormEffects.register();
		
		// Register vanilla iron replacement listener (for Outerworld oxidation)
		VanillaIronReplacementListener.register();
		
		// Register vanilla copper replacement listener (to prevent oxidation in Outerworld)
		VanillaCopperReplacementListener.register();
		
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
