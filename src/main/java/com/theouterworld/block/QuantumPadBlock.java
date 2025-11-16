package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import com.theouterworld.TemplateMod;
import com.theouterworld.block.ModBlocks;
import com.theouterworld.registry.ModDimensions;

public class QuantumPadBlock extends BlockWithEntity {
    public static final MapCodec<QuantumPadBlock> CODEC = createCodec(QuantumPadBlock::new);
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");
    
    // Custom shape for the block (smaller than full block since it's a pad)
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(0, 0, 0, 16, 1, 16), // Base pad
        Block.createCuboidShape(7.36, 0, 7.64, 8.36, 24, 8.64) // Center pillar
    );

    public QuantumPadBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ACTIVATED, false));
    }

    @Override
    public MapCodec<QuantumPadBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new QuantumPadBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) return null;
        return (type == com.theouterworld.registry.ModBlockEntities.QUANTUM_PAD)
            ? (w, p, s, be) -> QuantumPadBlockEntity.tick(w, p, s, (QuantumPadBlockEntity) be)
            : null;
    }

    // Internal method with Hand parameter
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack held = player.getStackInHand(hand);
        boolean isIgniter = held.isOf(Items.FLINT_AND_STEEL) || held.isOf(Items.FIRE_CHARGE);
        
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        // If activated, handle teleportation
        if (state.get(ACTIVATED)) {
            teleportPlayer(player, pos, world);
            return ActionResult.SUCCESS;
        }
        
        // If not activated and player has igniter, activate the pad
        if (isIgniter) {
            // Activate the quantum pad
            world.setBlockState(pos, state.with(ACTIVATED, true));
            world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.BLOCKS,
                1.0F,
                1.0F
            );
            
            // Consume the item if not in creative mode
            if (!player.isCreative()) {
                if (held.isOf(Items.FLINT_AND_STEEL)) {
                    held.damage(1, player, hand);
                } else if (held.isOf(Items.FIRE_CHARGE)) {
                    held.decrement(1);
                }
            }
            
            return ActionResult.CONSUME;
        }
        
        return ActionResult.PASS;
    }

    private void teleportPlayer(PlayerEntity player, BlockPos pos, World world) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        
        MinecraftServer server = serverWorld.getServer();
        RegistryKey<World> currentDimension = world.getRegistryKey();
        RegistryKey<World> targetDimension;
        
        // Determine target dimension
        if (currentDimension.equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            // If in outer world, go to overworld
            targetDimension = World.OVERWORLD;
        } else {
            // If in any other dimension, go to outer world
            targetDimension = ModDimensions.OUTER_WORLD_WORLD_KEY;
        }
        
        ServerWorld targetWorld = server.getWorld(targetDimension);
        if (targetWorld == null) return;
        
        // Calculate teleport position
        BlockPos teleportPos = calculateTeleportPosition(targetWorld, pos);
        
        // Teleport the player
        player.teleport(targetWorld, teleportPos.getX() + 0.5, teleportPos.getY() + 1, teleportPos.getZ() + 0.5, java.util.Set.of(), 0, 0, true);
        
        // Place a quantum pad at the destination
        placeQuantumPadAtDestination(targetWorld, teleportPos);
    }
    
    private BlockPos calculateTeleportPosition(ServerWorld targetWorld, BlockPos originalPos) {
        BlockPos surfacePos = targetWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, originalPos);
        if (surfacePos.getY() <= targetWorld.getBottomY()) {
            surfacePos = new BlockPos(originalPos.getX(), targetWorld.getBottomY() + 1, originalPos.getZ());
        }
        return surfacePos;
    }
    
    private void placeQuantumPadAtDestination(ServerWorld world, BlockPos pos) {
        BlockPos padPos = pos;
        if (!world.getBlockState(padPos).isAir()) {
            padPos = padPos.up();
        }

        ensureSolidBase(world, padPos);
        placePadIfAir(world, padPos);

        BlockPos adjacentPadPos = padPos.east();
        ensureSolidBase(world, adjacentPadPos);
        placePadIfAir(world, adjacentPadPos);
    }

    private void ensureSolidBase(ServerWorld world, BlockPos pos) {
        BlockPos basePos = pos.down();
        if (world.getBlockState(basePos).isAir()) {
            world.setBlockState(basePos, ModBlocks.OXIDIZED_BASALT.getDefaultState());
        }
    }

    private void placePadIfAir(ServerWorld world, BlockPos pos) {
        if (world.getBlockState(pos).isAir()) {
            world.setBlockState(pos, ModBlocks.QUANTUM_PAD.getDefaultState().with(ACTIVATED, true));
        }
    }

    // Public method without Hand parameter (this is the one that gets called)
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return onUse(state, world, pos, player, Hand.MAIN_HAND, hit);
    }

}

