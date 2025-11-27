package com.theouterworld.mixin;

import com.theouterworld.entity.OxidizableIronGolemEntity;
import com.theouterworld.registry.ModDimensions;
import com.theouterworld.registry.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to intercept iron golem spawns in the Outerworld dimension.
 * Replaces vanilla iron golems with oxidizable iron golems.
 */
@Mixin(ServerWorld.class)
public abstract class IronGolemSpawnMixin {

    @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
    private void theouterworld$interceptIronGolemSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerWorld world = (ServerWorld) (Object) this;
        
        // Only intercept in Outerworld dimension
        if (!world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return;
        }
        
        // Check if this is a vanilla iron golem (not already our custom one)
        if (entity.getType() == EntityType.IRON_GOLEM && !(entity instanceof OxidizableIronGolemEntity)) {
            IronGolemEntity vanillaGolem = (IronGolemEntity) entity;
            
            // Create our oxidizable iron golem instead
            OxidizableIronGolemEntity oxidizableGolem = ModEntities.OXIDIZABLE_IRON_GOLEM.create(world, SpawnReason.CONVERSION);
            if (oxidizableGolem != null) {
                // Copy position and rotation
                oxidizableGolem.refreshPositionAndAngles(
                    vanillaGolem.getX(), 
                    vanillaGolem.getY(), 
                    vanillaGolem.getZ(),
                    vanillaGolem.getYaw(),
                    vanillaGolem.getPitch()
                );
                
                // Copy health state
                oxidizableGolem.setHealth(vanillaGolem.getHealth());
                
                // Copy player-created status
                if (vanillaGolem.isPlayerCreated()) {
                    oxidizableGolem.setPlayerCreated(true);
                }
                
                // Spawn our golem instead
                world.spawnEntityAndPassengers(oxidizableGolem);
                
                // Cancel the vanilla golem spawn
                cir.setReturnValue(false);
            }
        }
    }
}

