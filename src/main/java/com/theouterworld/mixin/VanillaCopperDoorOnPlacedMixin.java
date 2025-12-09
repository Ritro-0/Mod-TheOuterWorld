package com.theouterworld.mixin;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.registry.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to replace vanilla copper doors with non-oxidizable versions when placed in the Outerworld.
 * 
 * DoorBlock.onPlaced has custom logic to place the upper half, so we need a separate mixin.
 * This fires AFTER the door is fully placed (both halves).
 */
@Mixin(DoorBlock.class)
public abstract class VanillaCopperDoorOnPlacedMixin {

    @Inject(method = "onPlaced", at = @At("TAIL"))
    private void theouterworld$replaceCopperDoor(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        // Only run on server side
        if (world.isClient() || !(world instanceof ServerWorld serverWorld)) {
            return;
        }

        // Only replace in Outerworld dimension
        if (!serverWorld.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return;
        }

        // Check if this is an oxidizable copper door
        Block currentBlock = state.getBlock();
        if (!(currentBlock instanceof Oxidizable)) {
            return;
        }

        // Map to non-oxidizable version
        Block replacement = null;
        if (currentBlock == Blocks.COPPER_DOOR) {
            replacement = ModBlocks.NON_OXIDIZABLE_COPPER_DOOR;
        } else if (currentBlock == Blocks.EXPOSED_COPPER_DOOR) {
            replacement = ModBlocks.NON_OXIDIZABLE_EXPOSED_COPPER_DOOR;
        } else if (currentBlock == Blocks.WEATHERED_COPPER_DOOR) {
            replacement = ModBlocks.NON_OXIDIZABLE_WEATHERED_COPPER_DOOR;
        } else if (currentBlock == Blocks.OXIDIZED_COPPER_DOOR) {
            replacement = ModBlocks.NON_OXIDIZABLE_OXIDIZED_COPPER_DOOR;
        }

        if (replacement == null) {
            return;
        }

        // Get current state and determine which half we're dealing with
        BlockState currentState = world.getBlockState(pos);
        
        // Replace lower half
        BlockState newLowerState = replacement.getStateWithProperties(currentState);
        serverWorld.setBlockState(pos, newLowerState, Block.NOTIFY_ALL);
        
        // Replace upper half (door places upper half at pos.up())
        BlockPos upperPos = pos.up();
        BlockState upperState = world.getBlockState(upperPos);
        if (upperState.getBlock() == currentBlock && upperState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            BlockState newUpperState = replacement.getStateWithProperties(upperState);
            serverWorld.setBlockState(upperPos, newUpperState, Block.NOTIFY_ALL);
        }
    }
}

