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
        
        // Get the block entity to check for linked coordinates
        BlockEntity be = world.getBlockEntity(pos);
        QuantumPadBlockEntity padEntity = null;
        BlockPos linkedPos = null;
        
        if (be instanceof QuantumPadBlockEntity) {
            padEntity = (QuantumPadBlockEntity) be;
            if (padEntity.hasLinkedPos()) {
                linkedPos = padEntity.getLinkedPos();
            }
        }
        
        BlockPos targetPadPos = null;
        boolean padExists = false;
        
        // Check if we have linked coordinates and if a pad exists there
        if (linkedPos != null) {
            BlockState linkedState = targetWorld.getBlockState(linkedPos);
            if (linkedState.isOf(ModBlocks.QUANTUM_PAD)) {
                // Pad exists at linked coordinates, use it
                targetPadPos = linkedPos;
                padExists = true;
            }
        }
        
        // If no linked pad exists, we need to create one
        if (!padExists) {
            // Calculate teleport position - match coordinates from the original pad
            BlockPos targetPos = new BlockPos(pos.getX(), 0, pos.getZ());
            
            // Find the surface where the player will be
            BlockPos playerTargetPos = targetPos;
            BlockPos playerSurfacePos = findSurfacePosition(targetWorld, playerTargetPos);
            
            // Place the pad west of the player, at the same Y level as the player's surface
            targetPadPos = new BlockPos(playerTargetPos.getX() - 1, playerSurfacePos.getY() + 1, playerTargetPos.getZ());
            
            // Make sure there's a solid block under the pad
            BlockPos padBasePos = targetPadPos.down();
            BlockState padBaseState = targetWorld.getBlockState(padBasePos);
            if (padBaseState.isAir() || !padBaseState.isSolidBlock(targetWorld, padBasePos)) {
                targetWorld.setBlockState(padBasePos, ModBlocks.OXIDIZED_BASALT.getDefaultState());
            }
            
            // Place the pad if it doesn't exist
            if (targetWorld.getBlockState(targetPadPos).isAir()) {
                targetWorld.setBlockState(targetPadPos, ModBlocks.QUANTUM_PAD.getDefaultState().with(ACTIVATED, true));
            }
            
            // Link both pads to each other
            // Wait a tick for the block entity to be created if needed
            BlockEntity targetBe = targetWorld.getBlockEntity(targetPadPos);
            if (targetBe instanceof QuantumPadBlockEntity) {
                QuantumPadBlockEntity targetPadEntity = (QuantumPadBlockEntity) targetBe;
                targetPadEntity.setLinkedPos(pos); // Link target pad back to source pad
            }
            
            if (padEntity != null) {
                padEntity.setLinkedPos(targetPadPos); // Link source pad to target pad
            }
        }
        
        // Teleport the player to the pad location (east of the pad)
        BlockPos playerTeleportPos = targetPadPos.east(); // Player goes east of the pad
        BlockPos finalPlayerSurfacePos = findSurfacePosition(targetWorld, playerTeleportPos);
        double playerY = finalPlayerSurfacePos.getY() + 1.0; // Player stands on the surface
        player.teleport(targetWorld, playerTeleportPos.getX() + 0.5, playerY, playerTeleportPos.getZ() + 0.5, java.util.Set.of(), 0, 0, true);
    }
    
    private BlockPos findSurfacePosition(ServerWorld targetWorld, BlockPos originalPos) {
        // Scan from top to bottom to find the first solid block with air above it
        // This is more reliable than using heightmaps which can be wrong
        int topY = targetWorld.getBottomY() + 255; // Start from near the top of the world
        int bottomY = targetWorld.getBottomY();
        
        // Scan downward to find the actual surface (topmost solid block with air above)
        for (int y = topY; y >= bottomY; y--) {
            BlockPos checkPos = new BlockPos(originalPos.getX(), y, originalPos.getZ());
            BlockState state = targetWorld.getBlockState(checkPos);
            BlockState stateAbove = targetWorld.getBlockState(checkPos.up());
            
            // Found a solid block with air above it - this is the surface!
            if (!state.isAir() && state.isSolidBlock(targetWorld, checkPos) && stateAbove.isAir()) {
                return checkPos;
            }
        }
        
        // If we didn't find a surface (shouldn't happen in normal worlds), use a safe fallback
        return new BlockPos(originalPos.getX(), targetWorld.getBottomY() + 64, originalPos.getZ());
    }
    
    private BlockPos placeQuantumPadAtDestination(ServerWorld world, BlockPos surfacePos) {
        // surfacePos is the topmost solid block (verified to have air above by findSurfacePosition)
        // Place the pad ON TOP of the surface (exactly 1 block above the solid block)
        BlockPos padPos = surfacePos.up();
        
        // Simply place the pad - findSurfacePosition already verified the surface is solid with air above
        world.setBlockState(padPos, ModBlocks.QUANTUM_PAD.getDefaultState().with(ACTIVATED, true));
        
        return padPos;
    }

    private void ensureSolidBase(ServerWorld world, BlockPos pos) {
        BlockPos basePos = pos.down();
        BlockState baseState = world.getBlockState(basePos);
        
        // If the base is air or a non-solid block, place a solid block
        if (baseState.isAir() || !baseState.isSolidBlock(world, basePos)) {
            world.setBlockState(basePos, ModBlocks.OXIDIZED_BASALT.getDefaultState());
        }
    }

    // Public method without Hand parameter (this is the one that gets called)
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return onUse(state, world, pos, player, Hand.MAIN_HAND, hit);
    }

}

