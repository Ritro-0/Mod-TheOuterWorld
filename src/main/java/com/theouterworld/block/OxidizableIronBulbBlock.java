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
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

/**
 * Iron bulb that works exactly like copper bulbs:
 * - Toggles lit state on rising edge of redstone signal
 * - Uses scheduled ticks for redstone updates
 * - Oxidizes over time in the Outerworld
 */
public class OxidizableIronBulbBlock extends Block implements Oxidizable {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    
    private final Oxidizable.OxidationLevel degradationLevel;
    private Block waxedVersion;

    public OxidizableIronBulbBlock(Oxidizable.OxidationLevel degradationLevel, Settings settings) {
        super(settings);
        this.degradationLevel = degradationLevel;
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
    }

    public void setWaxedVersion(Block waxedVersion) {
        this.waxedVersion = waxedVersion;
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && world instanceof ServerWorld serverWorld) {
            // Schedule immediate tick for initial power check
            serverWorld.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView scheduledTickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (world instanceof ServerWorld serverWorld) {
            boolean poweredNow = serverWorld.isReceivingRedstonePower(pos);
            boolean wasPowered = state.get(POWERED);
            if (poweredNow && !wasPowered) {
                // Rising edge: schedule tick for toggle (matches vanilla CopperBulbBlock exactly)
                scheduledTickView.scheduleBlockTick(pos, this, 1);
            }
            if (poweredNow != wasPowered) {
                // Update POWERED state for model/observers/comparator
                return state.with(POWERED, poweredNow);
            }
        }
        return state;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Only toggle if still powered (prevents short pulses from toggling)
        if (!state.get(POWERED) || !world.isReceivingRedstonePower(pos)) {
            return;
        }
        // Toggle LIT
        BlockState newState = state.cycle(LIT);
        world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        // Play vanilla copper bulb toggle sound
        world.playSound(null, pos, newState.get(LIT) ? SoundEvents.BLOCK_COPPER_BULB_TURN_ON : SoundEvents.BLOCK_COPPER_BULB_TURN_OFF,
                        SoundCategory.BLOCKS, 0.4F, newState.get(LIT) ? 0.8F : 1.2F);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Only oxidize in Outerworld dimension
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
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).map(block -> {
            BlockState newState = block.getDefaultState();
            if (newState.contains(LIT)) {
                newState = newState.with(LIT, state.get(LIT));
            }
            if (newState.contains(POWERED)) {
                newState = newState.with(POWERED, state.get(POWERED));
            }
            return newState;
        });
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        // Like copper bulbs: output signal based on oxidation level when lit
        // Higher oxidation = lower output (opposite of light level)
        if (!state.get(LIT)) {
            return 0;
        }
        return switch (this.degradationLevel) {
            case UNAFFECTED -> 15;  // Full signal when fresh
            case EXPOSED -> 12;
            case WEATHERED -> 8;
            case OXIDIZED -> 4;
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        if (stack.isOf(Items.HONEYCOMB) && waxedVersion != null) {
            if (!world.isClient()) {
                BlockState newState = waxedVersion.getDefaultState()
                    .with(LIT, state.get(LIT))
                    .with(POWERED, state.get(POWERED));
                world.setBlockState(pos, newState);
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
                    BlockState newState = previousBlock.get().getDefaultState();
                    if (newState.contains(LIT)) {
                        newState = newState.with(LIT, state.get(LIT));
                    }
                    if (newState.contains(POWERED)) {
                        newState = newState.with(POWERED, state.get(POWERED));
                    }
                    world.setBlockState(pos, newState);
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
