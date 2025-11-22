package com.theouterworld.mixin;

import com.theouterworld.registry.ModDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents vanilla monsters from naturally spawning in the Outerworld dimension.
 * Makes the Outerworld dimension invalid for monster spawns, just like Nether/End.
 */
@Mixin(ServerWorld.class)
public class OuterworldSpawnBlockerMixin {
    
    @Inject(
        method = "spawnEntity(Lnet/minecraft/entity/Entity;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventVanillaMonsterSpawnInOuterworld(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerWorld world = (ServerWorld) (Object) this;
        
        // Check if we're in the Outerworld
        if (world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            // Check if this entity is a mob
            if (entity instanceof MobEntity) {
                EntityType<?> type = entity.getType();
                
                // Block all monsters in Outerworld that are freshly spawned
                if (type.getSpawnGroup() == SpawnGroup.MONSTER) {
                    // Log all monster spawn attempts in Outerworld for debugging
                    com.theouterworld.OuterWorldMod.LOGGER.info("Monster spawn attempt in Outerworld: {} (age: {}, hasCustomName: {})", 
                        type, entity.age, entity.hasCustomName());
                    
                    // Block ALL monsters with age == 0 that don't have custom names
                    // Natural spawns have age == 0 and no custom name
                    // Commands/spawn eggs might have age == 0, but often have custom names or other indicators
                    // For now, block all monsters with age == 0 and no custom name
                    if (entity.age == 0 && !entity.hasCustomName()) {
                        // Block natural monster spawns in Outerworld
                        com.theouterworld.OuterWorldMod.LOGGER.info("BLOCKED monster spawn in Outerworld: {}", type);
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}
