package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
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

/**
 * Waxed iron bulb - works like copper bulbs but cannot oxidize.
 * Uses scheduled ticks for redstone updates.
 */
public class WaxedIronBulbBlock extends Block {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    
    private final Block unwaxedVersion;

    public WaxedIronBulbBlock(Block unwaxedVersion, Settings settings) {
        super(settings);
        this.unwaxedVersion = unwaxedVersion;
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
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
        // Play iron block click sound (using metal block sounds)
        world.playSound(null, pos, newState.get(LIT) ? SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
                        SoundCategory.BLOCKS, 0.4F, newState.get(LIT) ? 0.8F : 1.2F);
    }

    // TODO: Implement comparator output based on oxidation level (15/12/8/4 when lit, 0 when not lit)
    // This requires proper method override or mixin - needs investigation for Minecraft 1.21.10

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        if (stack.getItem() instanceof AxeItem) {
            if (!world.isClient()) {
                BlockState newState = unwaxedVersion.getDefaultState()
                    .with(LIT, state.get(LIT))
                    .with(POWERED, state.get(POWERED));
                world.setBlockState(pos, newState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.playSound(null, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                if (!player.isCreative()) {
                    stack.damage(1, player, player.getActiveHand());
                }
            }
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }

    public Block getUnwaxedVersion() {
        return unwaxedVersion;
    }
}
