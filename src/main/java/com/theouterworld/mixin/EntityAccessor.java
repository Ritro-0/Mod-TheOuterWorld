package com.theouterworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Accessor mixin to expose methods and fields from Entity.
 */
@Mixin(Entity.class)
public interface EntityAccessor {
    
    /**
     * Invoker for Entity.getGravity().
     * This calls the original implementation regardless of overrides.
     * 
     * @return The original gravity value (default 0.08D, overridden in subclasses)
     */
    @Invoker("getGravity")
    double invokeGetGravity();
    
    /**
     * Accessor for Entity.world field.
     * Allows accessing the world field from mixins.
     */
    @Accessor("world")
    World accessor$getWorld();

    /**
     * Invoker for Entity.getFlag(int).
     * Used to check boolean entity flags like fall flying.
     */
    @Invoker("getFlag")
    boolean invokeGetFlag(int flag);
}

