package com.theouterworld.weather;

import com.theouterworld.registry.ModDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * Server-side handler that applies dust storm effects (slowness, mining fatigue)
 * to players when they're exposed to an active dust storm.
 */
public class DustStormEffects {
    
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Get the world - if manager exists, we're in Outer World (manager only exists for that dimension)
                ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();
                DustStormManager manager = DustStormTicker.getManager(serverWorld);
                
                // If no manager or storm not active, player is not in an active dust storm
                boolean inActiveStorm = manager != null && manager.isStormActive();
                
                if (inActiveStorm && !player.isSpectator() && !player.isCreative()) {
                    // Player is in an active dust storm
                    DustStormExposure.enterDuststorm(player);
                    
                    // Apply effects based on exposure intensity
                    float intensity = DustStormExposure.getIntensity(player, 0f);
                    if (intensity > 0.1f) { // Start applying effects after 10% exposure
                        // Slowness effect - same as powdered snow (level 4 = very slow)
                        player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SLOWNESS, 
                            25, // Duration: 1.25 seconds
                            4,  // Amplifier: level 5 (0-indexed, so level 4 = amplifier 4)
                            false, // ambient
                            false, // showParticles
                            false  // showIcon
                        ));
                        
                        // Mining fatigue for added difficulty
                        player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.MINING_FATIGUE,
                            25, // Duration: 1.25 seconds
                            2,  // Amplifier: level 3
                            false,
                            false,
                            false
                        ));
                    }
                } else {
                    // Not in storm or creative/spectator - exit exposure
                    DustStormExposure.exitDuststorm(player);
                }
                
                // Always tick exposure (handles decay when out of storm)
                DustStormExposure.serverTick(player);
            }
        });
    }
}

