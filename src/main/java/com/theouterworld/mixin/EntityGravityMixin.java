package com.theouterworld.mixin;

import com.theouterworld.config.OuterworldConfig;
import com.theouterworld.registry.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Redirect mixin that intercepts getGravity() calls to apply Mars gravity (0.377x)
 * when entities are in the Outerworld dimension.
 * 
 * Strategy:
 * - @Redirect intercepts ALL getGravity() calls on Entity
 * - Checks if entity is in Outerworld dimension
 * - Returns scaled gravity if yes, original value if no
 * 
 * This works because getGravity() is called from tickMovement() and other places.
 */
@Mixin(Entity.class)
public class EntityGravityMixin {
    
    /**
     * Redirects ALL getGravity() calls to scale gravity in Outerworld.
     * 
     * @param instance The Entity instance (this)
     * @return The scaled gravity value if in Outerworld, original value otherwise
     */
    @Redirect(
        method = "*",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getGravity()D"
        )
    )
    private double redirectGetGravity(Entity instance) {
        // Get the original gravity value via accessor (bypasses redirect to avoid recursion)
        double originalGravity = ((EntityAccessor) instance).invokeGetGravity();
        
        // Get the entity's world and check if it's null
        World world = ((EntityAccessor) instance).accessor$getWorld();
        if (world == null) {
            return originalGravity;
        }
        
        // Get the dimension registry key
        RegistryKey<World> dimensionKey = world.getRegistryKey();
        
        // Apply gravity scaling if in Outerworld dimension
        if (dimensionKey.equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
			// Preserve vanilla gravity while elytra gliding to keep "movement the same"
			if (instance instanceof LivingEntity living && ((EntityAccessor) living).invokeGetFlag(7)) {
				return originalGravity;
			}
            return originalGravity * OuterworldConfig.get().gravityMultiplier;
        }
        
        // Return original gravity for all other dimensions
        return originalGravity;
    }
}

