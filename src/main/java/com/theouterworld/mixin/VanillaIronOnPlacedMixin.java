package com.theouterworld.mixin;

import com.theouterworld.event.VanillaIronReplacementListener;
import com.theouterworld.registry.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to replace vanilla iron blocks, trapdoors, and chains with oxidizable versions when placed in the Outerworld.
 * 
 * Uses a deferred replacement queue to let placement finalize (AXIS property for chains, etc.) before swapping.
 * The actual replacement happens in the next server tick via VanillaIronReplacementListener.
 * 
 * Note: Doors have their own mixin (VanillaIronDoorOnPlacedMixin) because DoorBlock has complex two-block placement.
 */
@Mixin(Block.class)
public abstract class VanillaIronOnPlacedMixin {

    @Inject(method = "onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"))
    private void theouterworld$queueReplacement(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (world.isClient() || !(world instanceof ServerWorld serverWorld)) return;

        if (!serverWorld.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) return;

        Block placedBlock = state.getBlock();

        // Skip if already oxidizable (prevents loops)
        if (placedBlock instanceof Oxidizable) return;

        Block replacement = VanillaIronReplacementListener.getReplacement(placedBlock);

        if (replacement != null) {
            // Queue replacement for next tick - lets placement finalize (AXIS for chains, etc.)
            VanillaIronReplacementListener.PENDING_REPLACEMENTS.add(
                new VanillaIronReplacementListener.PendingReplacement(serverWorld, pos.toImmutable(), replacement)
            );
        }
    }
}
