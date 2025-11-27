package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class OxidizableCutIronStairsBlock extends StairsBlock implements Oxidizable {
    private final Oxidizable.OxidationLevel degradationLevel;
    private Block waxedVersion;

    public OxidizableCutIronStairsBlock(BlockState baseBlockState, Oxidizable.OxidationLevel degradationLevel, Settings settings) {
        super(baseBlockState, settings);
        this.degradationLevel = degradationLevel;
    }

    public void setWaxedVersion(Block waxedVersion) {
        this.waxedVersion = waxedVersion;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (OxidizableIronBehavior.shouldOxidize(world, pos)) {
            this.doOxidation(state, world, pos, random);
        }
    }

    private void doOxidation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Optional<BlockState> nextState = this.getDegradationResult(state);
        if (nextState.isPresent()) {
            float chance = OxidizableIronBehavior.getOxidationChance(this.degradationLevel, 0);
            if (random.nextFloat() < chance) {
                world.setBlockState(pos, nextState.get());
            }
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    public Oxidizable.OxidationLevel getDegradationLevel() {
        return this.degradationLevel;
    }

    @Override
    public Optional<BlockState> getDegradationResult(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        if (stack.isOf(Items.HONEYCOMB) && waxedVersion != null) {
            if (!world.isClient()) {
                world.setBlockState(pos, waxedVersion.getStateWithProperties(state));
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        }
        
        if (stack.getItem() instanceof AxeItem) {
            Optional<Block> previousBlock = Oxidizable.getDecreasedOxidationBlock(state.getBlock());
            if (previousBlock.isPresent()) {
                if (!world.isClient()) {
                    world.setBlockState(pos, previousBlock.get().getStateWithProperties(state));
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                    world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    
                    if (!player.isCreative()) {
                        stack.damage(1, player, player.getActiveHand());
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        
        return ActionResult.PASS;
    }
}

