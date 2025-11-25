package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
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

public class OxidizableIronBlock extends Block implements Oxidizable {
    private final Oxidizable.OxidationLevel degradationLevel;
    private Block nextOxidizedBlock;
    private Block previousOxidizedBlock;
    private Block waxedVersion;

    public OxidizableIronBlock(Oxidizable.OxidationLevel degradationLevel, Block nextOxidizedBlock, Block previousOxidizedBlock, Settings settings) {
        super(settings);
        this.degradationLevel = degradationLevel;
        this.nextOxidizedBlock = nextOxidizedBlock;
        this.previousOxidizedBlock = previousOxidizedBlock;
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

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return OxidizableIronBehavior.getNextOxidationDelay(this.getDegradationLevel()) < Integer.MAX_VALUE;
    }

    protected void tickDegradation(BlockState state, World world, BlockPos pos, Random random) {
        // Only oxidize in Outerworld dimension
        if (!(world instanceof ServerWorld serverWorld) || !OxidizableIronBehavior.shouldOxidize(serverWorld, pos)) {
            return;
        }

        if (this.getDegradationLevel() == Oxidizable.OxidationLevel.OXIDIZED) {
            return; // Can't oxidize further
        }

        if (nextOxidizedBlock == null) {
            return; // No next stage
        }

        // Count nearby unwaxed oxidizable iron blocks at higher oxidation levels
        Oxidizable.OxidationLevel nextLevel = this.getNextDegradationLevel();
        int higherNeighborCount = OxidizableIronBehavior.countNearbyUnwaxedOxidizableIron(world, pos, nextLevel);

        // Calculate oxidation chance
        float chance = OxidizableIronBehavior.getOxidationChance(this.degradationLevel, higherNeighborCount);

        if (random.nextFloat() < chance) {
            // Oxidize to next stage
            world.setBlockState(pos, nextOxidizedBlock.getDefaultState());
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
    public java.util.Optional<BlockState> getDegradationResult(BlockState state) {
        if (previousOxidizedBlock != null) {
            return java.util.Optional.of(previousOxidizedBlock.getDefaultState());
        }
        return java.util.Optional.empty();
    }

    public Block getNextOxidizedBlock() {
        return nextOxidizedBlock;
    }

    public Block getPreviousOxidizedBlock() {
        return previousOxidizedBlock;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, net.minecraft.entity.player.PlayerEntity player, net.minecraft.util.Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        
        // Right-click with honeycomb to wax (prevent oxidation)
        if (stack.isOf(Items.HONEYCOMB) && waxedVersion != null) {
            if (world instanceof ServerWorld serverWorld) {
                world.setBlockState(pos, waxedVersion.getDefaultState());
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                // Consume honeycomb
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        
        // Right-click with axe to de-oxidize one stage (scrape)
        if (stack.getItem() instanceof AxeItem) {
            if (world instanceof ServerWorld serverWorld && previousOxidizedBlock != null) {
                world.setBlockState(pos, previousOxidizedBlock.getDefaultState());
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                // Damage the axe
                if (!player.isCreative()) {
                    stack.damage(1, player, hand);
                }
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }
}

