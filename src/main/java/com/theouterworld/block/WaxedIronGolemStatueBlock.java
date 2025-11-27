package com.theouterworld.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

/**
 * Waxed Iron Golem Statue block - cannot oxidize further.
 * Can be scraped with an axe to remove wax and allow further oxidation/de-oxidation.
 */
public class WaxedIronGolemStatueBlock extends BlockWithEntity implements Waterloggable {
    
    public static final MapCodec<WaxedIronGolemStatueBlock> CODEC = createCodec(settings -> 
        new WaxedIronGolemStatueBlock(ModBlocks.IRON_GOLEM_STATUE, settings));
    
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    
    // Scaled-down iron golem statue shapes for each facing direction
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
        VoxelShapes.cuboid(0.265625, 0, 0.40625, 0.734375, 0.5, 0.5625),
        VoxelShapes.cuboid(0.09375, 0.0625, 0.40625, 0.90625, 1.0, 0.59375),
        VoxelShapes.cuboid(0.375, 1.0, 0.265625, 0.625, 1.3125, 0.515625)
    );
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(
        VoxelShapes.cuboid(0.265625, 0, 0.4375, 0.734375, 0.5, 0.59375),
        VoxelShapes.cuboid(0.09375, 0.0625, 0.40625, 0.90625, 1.0, 0.59375),
        VoxelShapes.cuboid(0.375, 1.0, 0.484375, 0.625, 1.3125, 0.734375)
    );
    private static final VoxelShape SHAPE_EAST = VoxelShapes.union(
        VoxelShapes.cuboid(0.40625, 0, 0.265625, 0.5625, 0.5, 0.734375),
        VoxelShapes.cuboid(0.40625, 0.0625, 0.09375, 0.59375, 1.0, 0.90625),
        VoxelShapes.cuboid(0.484375, 1.0, 0.375, 0.734375, 1.3125, 0.625)
    );
    private static final VoxelShape SHAPE_WEST = VoxelShapes.union(
        VoxelShapes.cuboid(0.4375, 0, 0.265625, 0.59375, 0.5, 0.734375),
        VoxelShapes.cuboid(0.40625, 0.0625, 0.09375, 0.59375, 1.0, 0.90625),
        VoxelShapes.cuboid(0.265625, 1.0, 0.375, 0.515625, 1.3125, 0.625)
    );
    
    private final Block unwaxedVersion;
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public WaxedIronGolemStatueBlock(Block unwaxedVersion, Settings settings) {
        super(settings);
        this.unwaxedVersion = unwaxedVersion;
        this.setDefaultState(this.stateManager.getDefaultState()
            .with(FACING, Direction.NORTH)
            .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState()
            .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
            .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView scheduledTickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(WATERLOGGED)) {
            scheduledTickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, scheduledTickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // Use model rendering - will need blockstate/model files
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IronGolemStatueBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        // Axe interaction - remove wax
        if (stack.getItem() instanceof AxeItem) {
            if (!world.isClient()) {
                BlockState newState = unwaxedVersion.getDefaultState()
                    .with(FACING, state.get(FACING))
                    .with(WATERLOGGED, state.get(WATERLOGGED));
                world.setBlockState(pos, newState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                // Spawn wax off particles
                if (world instanceof ServerWorld serverWorld) {
                    for (int i = 0; i < 10; i++) {
                        serverWorld.spawnParticles(ParticleTypes.WAX_OFF,
                            pos.getX() + 0.5 + world.random.nextGaussian() * 0.3,
                            pos.getY() + 1.0 + world.random.nextGaussian() * 0.3,
                            pos.getZ() + 0.5 + world.random.nextGaussian() * 0.3,
                            1, 0, 0, 0, 0);
                    }
                }
                
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

