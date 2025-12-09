package com.theouterworld.block;

import com.theouterworld.entity.OxidizableIronGolemEntity;
import com.theouterworld.registry.ModEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

import java.util.Optional;

/**
 * Iron Golem Statue block - a petrified iron golem.
 * Can be scraped with an axe to de-oxidize, and scraping the unaffected version
 * will reanimate it into a living iron golem.
 * 
 * TODO: Create similar CopperGolemStatueBlock for copper golems
 * - Should follow same pattern as IronGolemStatueBlock
 * - Should reanimate into copper golem when scraped at unaffected stage
 */
public class IronGolemStatueBlock extends BlockWithEntity implements Waterloggable, Oxidizable {
    
    public static final MapCodec<IronGolemStatueBlock> CODEC = createCodec(settings -> 
        new IronGolemStatueBlock(OxidationLevel.UNAFFECTED, settings));
    
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    
    // Scaled-down iron golem statue shapes for each facing direction
    // Model is about 21 units tall, fits in ~1.3 blocks
    
    // North-facing shape (default - golem faces north/negative Z)
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
        // Legs (Y: 0-8)
        VoxelShapes.cuboid(0.265625, 0, 0.40625, 0.734375, 0.5, 0.5625),
        // Body and arms (Y: 1-16, X: 1.5 to 14.5)
        VoxelShapes.cuboid(0.09375, 0.0625, 0.40625, 0.90625, 1.0, 0.59375),
        // Head (Y: 16-21, Z: 4.25 to 8.25)
        VoxelShapes.cuboid(0.375, 1.0, 0.265625, 0.625, 1.3125, 0.515625)
    );
    
    // South-facing shape (golem faces south/positive Z)
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(
        VoxelShapes.cuboid(0.265625, 0, 0.4375, 0.734375, 0.5, 0.59375),
        VoxelShapes.cuboid(0.09375, 0.0625, 0.40625, 0.90625, 1.0, 0.59375),
        VoxelShapes.cuboid(0.375, 1.0, 0.484375, 0.625, 1.3125, 0.734375)
    );
    
    // East-facing shape (golem faces east/positive X)
    private static final VoxelShape SHAPE_EAST = VoxelShapes.union(
        VoxelShapes.cuboid(0.40625, 0, 0.265625, 0.5625, 0.5, 0.734375),
        VoxelShapes.cuboid(0.40625, 0.0625, 0.09375, 0.59375, 1.0, 0.90625),
        VoxelShapes.cuboid(0.484375, 1.0, 0.375, 0.734375, 1.3125, 0.625)
    );
    
    // West-facing shape (golem faces west/negative X)
    private static final VoxelShape SHAPE_WEST = VoxelShapes.union(
        VoxelShapes.cuboid(0.4375, 0, 0.265625, 0.59375, 0.5, 0.734375),
        VoxelShapes.cuboid(0.40625, 0.0625, 0.09375, 0.59375, 1.0, 0.90625),
        VoxelShapes.cuboid(0.265625, 1.0, 0.375, 0.515625, 1.3125, 0.625)
    );
    
    private final OxidationLevel oxidationLevel;
    private Block waxedVersion;
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public IronGolemStatueBlock(OxidationLevel oxidationLevel, Settings settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
        this.setDefaultState(this.stateManager.getDefaultState()
            .with(FACING, Direction.NORTH)
            .with(WATERLOGGED, false));
    }

    public void setWaxedVersion(Block waxedVersion) {
        this.waxedVersion = waxedVersion;
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
    public OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }

    @Override
    public Optional<BlockState> getDegradationResult(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).map(block -> {
            BlockState newState = block.getDefaultState();
            if (newState.contains(FACING)) {
                newState = newState.with(FACING, state.get(FACING));
            }
            if (newState.contains(WATERLOGGED)) {
                newState = newState.with(WATERLOGGED, state.get(WATERLOGGED));
            }
            return newState;
        });
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Only oxidize in Outerworld
        if (OxidizableIronBehavior.shouldOxidize(world, pos)) {
            this.tickDegradation(state, world, pos, random);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        // Honeycomb waxing
        if (stack.isOf(Items.HONEYCOMB) && waxedVersion != null) {
            if (!world.isClient()) {
                BlockState newState = waxedVersion.getDefaultState()
                    .with(FACING, state.get(FACING))
                    .with(WATERLOGGED, state.get(WATERLOGGED));
                world.setBlockState(pos, newState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        }
        
        // Axe interaction
        if (stack.getItem() instanceof AxeItem) {
            // Check if this is the unaffected (un-oxidized) statue
            if (this.oxidationLevel == OxidationLevel.UNAFFECTED) {
                // Reanimate the golem!
                if (!world.isClient() && world instanceof ServerWorld serverWorld) {
                    reanimateGolem(serverWorld, pos, state);
                    
                    if (!player.isCreative()) {
                        stack.damage(1, player, player.getActiveHand());
                    }
                }
                return ActionResult.SUCCESS;
            }
            
            // Otherwise, de-oxidize one stage
            Optional<Block> previousBlock = Oxidizable.getDecreasedOxidationBlock(state.getBlock());
            if (previousBlock.isPresent()) {
                if (!world.isClient()) {
                    BlockState newState = previousBlock.get().getDefaultState()
                        .with(FACING, state.get(FACING))
                        .with(WATERLOGGED, state.get(WATERLOGGED));
                    world.setBlockState(pos, newState);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                    world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    
                    // Spawn scrape particles
                    if (world instanceof ServerWorld serverWorld) {
                        for (int i = 0; i < 10; i++) {
                            serverWorld.spawnParticles(ParticleTypes.SCRAPE,
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
        }
        
        return ActionResult.PASS;
    }

    /**
     * Reanimate the statue into a living iron golem.
     */
    private void reanimateGolem(ServerWorld world, BlockPos pos, BlockState state) {
        // Get pose data from block entity
        float bodyYaw = 0;
        float headYaw = 0;
        float headPitch = 0;
        net.minecraft.text.Text customName = null;
        
        if (world.getBlockEntity(pos) instanceof IronGolemStatueBlockEntity statueEntity) {
            bodyYaw = statueEntity.getBodyYaw();
            headYaw = statueEntity.getHeadYaw();
            headPitch = statueEntity.getHeadPitch();
            customName = statueEntity.getCustomName();
        }
        
        // Remove the statue block
        world.removeBlock(pos, false);
        
        // Create the golem
        OxidizableIronGolemEntity golem = ModEntities.OXIDIZABLE_IRON_GOLEM.create(world, SpawnReason.CONVERSION);
        if (golem != null) {
            // Set position and rotation
            Direction facing = state.get(FACING);
            // Convert direction to yaw: N=180, E=270, S=0, W=90
            float rotation = switch (facing) {
                case NORTH -> 180.0f;
                case SOUTH -> 0.0f;
                case WEST -> 90.0f;
                case EAST -> 270.0f;
                default -> 0.0f;
            };
            golem.refreshPositionAndAngles(
                pos.getX() + 0.5, 
                pos.getY(), 
                pos.getZ() + 0.5,
                rotation,
                0
            );
            
            // Restore pose
            golem.bodyYaw = bodyYaw;
            golem.headYaw = headYaw;
            golem.setPitch(headPitch);
            
            // Set as un-oxidized (fresh from reanimation)
            golem.setOxidationLevel(0);
            
            // Restore custom name
            if (customName != null) {
                golem.setCustomName(customName);
            }
            
            // Spawn the golem
            world.spawnEntity(golem);
            
            // Play reanimation sound
            world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }
}

