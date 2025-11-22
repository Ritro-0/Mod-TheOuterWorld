package com.theouterworld.mixin;

import com.theouterworld.config.OuterworldConfig;
import com.theouterworld.registry.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Mixin for ArrowEntity (and other projectiles) that have hardcoded gravity values.
 * 
 * ArrowEntity uses a hardcoded -0.05D gravity value in its tick() method:
 *   this.setVelocity(this.getVelocity().add(0.0D, -0.05D * (double)(...), 0.0D))
 * 
 * This mixin intercepts that hardcoded constant and scales it when in Outerworld.
 * 
 * Strategy:
 * - @ModifyConstant intercepts the literal -0.05D constant
 * - Scales it by config multiplier if in Outerworld
 * - Preserves vanilla behavior elsewhere
 * 
 * Note: Other projectiles (TridentEntity, SpectralArrowEntity, etc.) may also
 * need similar mixins if they hardcode gravity. Check their tick() methods.
 */
@Mixin(ArrowEntity.class)
public class ArrowEntityGravityMixin {
    /**
     * Modifies the hardcoded gravity constant (-0.05D) in ArrowEntity.tick().
     * 
     * @param original The original hardcoded gravity constant (-0.05D)
     * @return The scaled gravity value if in Outerworld, original value otherwise
     */
    @ModifyConstant(
        method = "tick()V",
        constant = @Constant(doubleValue = -0.05D)
    )
    private double modifyArrowGravity(double original) {
        ArrowEntity instance = (ArrowEntity) (Object) this;
        
        // Get the entity's world via Entity accessor (ArrowEntity extends Entity)
        World world = ((EntityAccessor) instance).accessor$getWorld();
        if (world == null) {
            return original;
        }
        
        // Get the dimension registry key
        RegistryKey<World> dimensionKey = world.getRegistryKey();
        
        // Apply gravity scaling if in Outerworld dimension
        if (dimensionKey.equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            OuterworldConfig config = OuterworldConfig.get();
            return original * config.gravityMultiplier;
        }
        
        // Return original gravity for all other dimensions
        return original;
    }
}

