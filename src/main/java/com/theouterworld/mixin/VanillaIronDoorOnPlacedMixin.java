package com.theouterworld.mixin;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.registry.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
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
 * Mixin to replace vanilla iron doors with oxidizable versions when placed in the Outerworld.
 * 
 * DoorBlock.onPlaced has custom logic to place the upper half, so we need a separate mixin.
 * This fires AFTER the door is fully placed (both halves).
 */
@Mixin(DoorBlock.class)
public abstract class VanillaIronDoorOnPlacedMixin {

    @Inject(method = "onPlaced", at = @At("TAIL"))
    private void theouterworld$replaceIronDoor(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        // Only run on server side
        if (world.isClient() || !(world instanceof ServerWorld serverWorld)) {
            return;
        }

        // Only replace in Outerworld dimension
        if (!serverWorld.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            return;
        }

        // Check if this is the vanilla iron door using state.isOf() for reliable comparison
        if (!state.isOf(Blocks.IRON_DOOR)) {
            return;
        }

        // Get current state and determine which half we're dealing with
        BlockState currentState = world.getBlockState(pos);
        
        // Replace lower half
        BlockState newLowerState = ModBlocks.UNAFFECTED_IRON_DOOR.getStateWithProperties(currentState);
        serverWorld.setBlockState(pos, newLowerState, Block.NOTIFY_ALL);
        
        // Replace upper half (door places upper half at pos.up())
        BlockPos upperPos = pos.up();
        BlockState upperState = world.getBlockState(upperPos);
        if (upperState.isOf(Blocks.IRON_DOOR) && upperState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            BlockState newUpperState = ModBlocks.UNAFFECTED_IRON_DOOR.getStateWithProperties(upperState);
            serverWorld.setBlockState(upperPos, newUpperState, Block.NOTIFY_ALL);
        }
    }
}

