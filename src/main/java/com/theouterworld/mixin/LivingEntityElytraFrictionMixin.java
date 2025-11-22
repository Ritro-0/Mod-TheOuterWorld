package com.theouterworld.mixin;

import com.theouterworld.registry.ModDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adjust elytra gliding friction in the Outerworld dimension.
 *
 * Goal:
 * - Keep movement "the same" by preserving vanilla gravity while gliding (handled in EntityGravityMixin)
 * - Reduce friction to 0.337% of vanilla friction while gliding in the Outerworld
 *
 * Implementation:
 * - Inject at the end of tickMovement and apply a compensation multiplier to velocity
 *   that converts vanilla's applied friction to our desired effective friction.
 *
 * Notes:
 * - Vanilla elytra uses approximate drag factors of 0.99 (X/Z) and 0.98 (Y).
 * - We compute new factors by reducing the "drag deficit" (1 - base) by 0.00337,
 *   then compute a compensation multiplier = newFactor / baseFactor and apply it.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityElytraFrictionMixin {
	@Inject(method = "tickMovement()V", at = @At("TAIL"))
	private void theouterworld$adjustElytraFriction(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		// Check fall flying via entity flag 7 (elytra gliding)
		if (!((EntityAccessor) self).invokeGetFlag(7)) {
			return;
		}

		World world = ((EntityAccessor) self).accessor$getWorld();
		if (world == null || !world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
			return;
		}

		// Desired fraction of vanilla friction: 0.337% (0.00337)
		final double desiredFrictionFraction = 0.00337D;

		// Vanilla elytra approximate base friction per axis
		final double baseFrictionXZ = 0.99D;
		final double baseFrictionY = 0.98D;

		// Compute the new effective friction factors (close to 1.0)
		final double newFrictionXZ = 1.0D - (1.0D - baseFrictionXZ) * desiredFrictionFraction;
		final double newFrictionY = 1.0D - (1.0D - baseFrictionY) * desiredFrictionFraction;

		// Compensation multipliers to convert vanilla-frictioned velocity into our target
		final double compensationXZ = newFrictionXZ / baseFrictionXZ;
		final double compensationY = newFrictionY / baseFrictionY;

		Vec3d v = self.getVelocity();
		self.setVelocity(v.x * compensationXZ, v.y * compensationY, v.z * compensationXZ);
	}
}


