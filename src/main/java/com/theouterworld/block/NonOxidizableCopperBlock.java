package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

/**
 * Non-oxidizable copper block - looks like vanilla copper but never oxidizes.
 * Used to replace vanilla copper blocks when placed in the Outerworld dimension.
 */
public class NonOxidizableCopperBlock extends Block {
    private final Block vanillaCounterpart;
    
    public NonOxidizableCopperBlock(Block vanillaCounterpart, Settings settings) {
        super(settings);
        this.vanillaCounterpart = vanillaCounterpart;
    }
    
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        // Return vanilla counterpart for middle-click in creative
        return new ItemStack(vanillaCounterpart);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        // Right-click with axe to scrape (de-oxidize) - but this block doesn't oxidize, so this is a no-op
        // We keep this for consistency with vanilla copper behavior, but it won't do anything
        if (stack.getItem() instanceof AxeItem) {
            if (world instanceof ServerWorld serverWorld) {
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
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
}

