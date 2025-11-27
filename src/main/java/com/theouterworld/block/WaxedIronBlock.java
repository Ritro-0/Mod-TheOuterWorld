package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
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

public class WaxedIronBlock extends Block {
    private final Block unwaxedVersion;

    public WaxedIronBlock(Block unwaxedVersion, Settings settings) {
        super(settings);
        this.unwaxedVersion = unwaxedVersion;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        // Right-click with axe to unwax (revert one stage, no honeycomb drop)
        if (stack.getItem() instanceof AxeItem) {
            if (world instanceof ServerWorld serverWorld) {
                world.setBlockState(pos, unwaxedVersion.getDefaultState());
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.playSound(null, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                // Damage the axe
                if (!player.isCreative()) {
                    stack.damage(1, player, player.getActiveHand());
                }
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }

    public Block getUnwaxedVersion() {
        return unwaxedVersion;
    }
}
