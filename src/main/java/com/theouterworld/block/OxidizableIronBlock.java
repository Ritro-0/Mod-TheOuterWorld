package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
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
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class OxidizableIronBlock extends Block implements Oxidizable {
    private final Oxidizable.OxidationLevel degradationLevel;
    private Block waxedVersion;

    public OxidizableIronBlock(Oxidizable.OxidationLevel degradationLevel, Settings settings) {
        super(settings);
        this.degradationLevel = degradationLevel;
    }

    public void setWaxedVersion(Block waxedVersion) {
        this.waxedVersion = waxedVersion;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Only oxidize in Outerworld dimension
        if (OxidizableIronBehavior.shouldOxidize(world, pos)) {
            this.doOxidation(state, world, pos, random);
        }
    }

    private void doOxidation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Get the next oxidation stage
        Optional<BlockState> nextState = this.getDegradationResult(state);
        if (nextState.isPresent()) {
            // Base oxidation chance (~5.6% like vanilla copper)
            float chance = OxidizableIronBehavior.getOxidationChance(this.degradationLevel, 0);
            if (random.nextFloat() < chance) {
                world.setBlockState(pos, nextState.get());
            }
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        // Use Fabric API to check if there's a next oxidation stage
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    public Oxidizable.OxidationLevel getDegradationLevel() {
        return this.degradationLevel;
    }

    @Override
    public Optional<BlockState> getDegradationResult(BlockState state) {
        // This returns the NEXT oxidation stage (more oxidized)
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
    }
    
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        // Return vanilla iron block for middle-click in creative
        return new ItemStack(net.minecraft.block.Blocks.IRON_BLOCK);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        // Right-click with honeycomb to wax (prevent oxidation)
        if (stack.isOf(Items.HONEYCOMB) && waxedVersion != null) {
            if (!world.isClient()) {
                world.setBlockState(pos, waxedVersion.getStateWithProperties(state));
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                // Consume honeycomb
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        }
        
        // Right-click with axe to de-oxidize one stage (scrape)
        if (stack.getItem() instanceof AxeItem) {
            // Get the previous (less oxidized) stage
            Optional<Block> previousBlock = Oxidizable.getDecreasedOxidationBlock(state.getBlock());
            if (previousBlock.isPresent()) {
                if (!world.isClient()) {
                    world.setBlockState(pos, previousBlock.get().getStateWithProperties(state));
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                    world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    
                    // Damage the axe
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
