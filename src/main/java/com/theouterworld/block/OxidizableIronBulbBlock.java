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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class OxidizableIronBulbBlock extends Block implements Oxidizable {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    
    private final Oxidizable.OxidationLevel degradationLevel;
    private Block nextOxidizedBlock;
    private Block previousOxidizedBlock;
    private Block waxedVersion;

    public OxidizableIronBulbBlock(Oxidizable.OxidationLevel degradationLevel, Block nextOxidizedBlock, Block previousOxidizedBlock, Settings settings) {
        super(settings);
        this.degradationLevel = degradationLevel;
        this.nextOxidizedBlock = nextOxidizedBlock;
        this.previousOxidizedBlock = previousOxidizedBlock;
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
    }

    public void setNextOxidizedBlock(Block nextOxidizedBlock) {
        this.nextOxidizedBlock = nextOxidizedBlock;
    }

    public void setPreviousOxidizedBlock(Block previousOxidizedBlock) {
        this.previousOxidizedBlock = previousOxidizedBlock;
    }

    public void setWaxedVersion(Block waxedVersion) {
        this.waxedVersion = waxedVersion;
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient()) {
            return;
        }
        boolean isPowered = world.isReceivingRedstonePower(pos);
        boolean wasPowered = state.get(POWERED);
        
        if (isPowered && !wasPowered) {
            // Rising edge - toggle lit state
            boolean newLit = !state.get(LIT);
            world.setBlockState(pos, state.with(POWERED, true).with(LIT, newLit), Block.NOTIFY_ALL);
            if (newLit) {
                world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_TURN_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        } else if (!isPowered && wasPowered) {
            // Falling edge - just update powered state
            world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return OxidizableIronBehavior.getNextOxidationDelay(this.getDegradationLevel()) < Integer.MAX_VALUE;
    }

    protected void tickDegradation(BlockState state, World world, BlockPos pos, Random random) {
        if (!(world instanceof ServerWorld serverWorld) || !OxidizableIronBehavior.shouldOxidize(serverWorld, pos)) {
            return;
        }

        if (this.getDegradationLevel() == Oxidizable.OxidationLevel.OXIDIZED || nextOxidizedBlock == null) {
            return;
        }

        Oxidizable.OxidationLevel nextLevel = this.getNextDegradationLevel();
        int higherNeighborCount = OxidizableIronBehavior.countNearbyUnwaxedOxidizableIron(world, pos, nextLevel);
        float chance = OxidizableIronBehavior.getOxidationChance(this.degradationLevel, higherNeighborCount);

        if (random.nextFloat() < chance) {
            // Copy lit and powered state to new block
            BlockState newState = nextOxidizedBlock.getDefaultState()
                .with(LIT, state.get(LIT))
                .with(POWERED, state.get(POWERED));
            world.setBlockState(pos, newState);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_TURN_OFF, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
    }

    @Override
    public Oxidizable.OxidationLevel getDegradationLevel() {
        return this.degradationLevel;
    }

    public Oxidizable.OxidationLevel getNextDegradationLevel() {
        return switch (this.degradationLevel) {
            case UNAFFECTED -> Oxidizable.OxidationLevel.EXPOSED;
            case EXPOSED -> Oxidizable.OxidationLevel.WEATHERED;
            case WEATHERED -> Oxidizable.OxidationLevel.OXIDIZED;
            case OXIDIZED -> Oxidizable.OxidationLevel.OXIDIZED;
        };
    }

    @Override
    public Optional<BlockState> getDegradationResult(BlockState state) {
        if (previousOxidizedBlock != null) {
            return Optional.of(previousOxidizedBlock.getDefaultState()
                .with(LIT, state.get(LIT))
                .with(POWERED, state.get(POWERED)));
        }
        return Optional.empty();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        if (stack.isOf(Items.HONEYCOMB) && waxedVersion != null) {
            if (world instanceof ServerWorld) {
                BlockState newState = waxedVersion.getDefaultState()
                    .with(LIT, state.get(LIT))
                    .with(POWERED, state.get(POWERED));
                world.setBlockState(pos, newState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        
        if (stack.getItem() instanceof AxeItem && previousOxidizedBlock != null) {
            if (world instanceof ServerWorld) {
                BlockState newState = previousOxidizedBlock.getDefaultState()
                    .with(LIT, state.get(LIT))
                    .with(POWERED, state.get(POWERED));
                world.setBlockState(pos, newState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
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

