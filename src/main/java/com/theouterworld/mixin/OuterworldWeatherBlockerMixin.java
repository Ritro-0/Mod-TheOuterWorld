package com.theouterworld.mixin;

import com.theouterworld.registry.ModDimensions;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents vanilla rain, snow, and storms from occurring in the Outerworld.
 * The Outerworld is Mars - it NEVER rains water. Only dust storms occur.
 */
@Mixin(ServerWorld.class)
public class OuterworldWeatherBlockerMixin {
    
    @Inject(
        method = "tickWeather",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventVanillaWeatherInOuterworld(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        
        // Check if we're in the Outerworld
        if (world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            // Cancel vanilla weather ticking - Mars has no water rain
            // This prevents rain, snow, and thunder from naturally occurring
            ci.cancel();
        }
    }
    
    @Inject(
        method = "setWeather(IIZZ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventVanillaWeatherSetInOuterworld(
        int clearDuration,
        int rainDuration,
        boolean raining,
        boolean thundering,
        CallbackInfo ci
    ) {
        ServerWorld world = (ServerWorld) (Object) this;
        
        // Check if we're in the Outerworld
        if (world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            // Block ALL rain in Outerworld - Mars doesn't rain water!
            // This includes rain from vanilla weather, commands, or the dust storm system
            if (raining) {
                ci.cancel();
                return;
            }
            
            // Block thunderstorms too (thundering=true)
            if (thundering) {
                ci.cancel();
                return;
            }
            
            // Allow clearing weather (raining=false, thundering=false) if needed
            // This allows clearing any residual weather states
        }
    }
}

