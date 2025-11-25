package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WaxedIronDoorBlock extends DoorBlock {
    private final Block unwaxedVersion;

    public WaxedIronDoorBlock(Block unwaxedVersion, BlockSetType blockSetType, Settings settings) {
        super(blockSetType, settings);
        this.unwaxedVersion = unwaxedVersion;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, net.minecraft.entity.player.PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        if (stack.getItem() instanceof AxeItem) {
            if (world instanceof ServerWorld) {
                BlockState newState = unwaxedVersion.getDefaultState()
                    .with(FACING, state.get(FACING))
                    .with(OPEN, state.get(OPEN))
                    .with(HINGE, state.get(HINGE))
                    .with(HALF, state.get(HALF))
                    .with(POWERED, state.get(POWERED));
                world.setBlockState(pos, newState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.playSound(null, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                if (!player.isCreative()) {
                    stack.damage(1, player, player.getActiveHand());
                }
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        
        // Call super for normal door behavior
        return super.onUse(state, world, pos, player, hit);
    }

    public Block getUnwaxedVersion() {
        return unwaxedVersion;
    }
}

