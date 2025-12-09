package com.theouterworld.block;

import com.mojang.serialization.MapCodec;
import com.theouterworld.registry.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ProcessorBlock extends BlockWithEntity {
    public static final MapCodec<ProcessorBlock> CODEC = createCodec(ProcessorBlock::new);
    
    // Custom shape based on the blockbench model
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(4, 0, 4, 12, 2, 12),   // Base
        Block.createCuboidShape(6, 2, 6, 10, 3, 10),   // Lower support
        Block.createCuboidShape(7, 3, 7, 9, 10, 9),    // Central pillar
        Block.createCuboidShape(4, 10, 4, 12, 11, 12), // Top arms
        Block.createCuboidShape(4, 11, 4, 12, 13, 12)  // Top ring
    );

    public ProcessorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<ProcessorBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ProcessorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) return null;
        return (type == ModBlockEntities.PROCESSOR)
            ? (w, p, s, be) -> ProcessorBlockEntity.tick(w, p, s, (ProcessorBlockEntity) be)
            : null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ProcessorBlockEntity processor) {
            player.openHandledScreen(processor);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ProcessorBlockEntity processorEntity) {
                ItemScatterer.spawn(world, pos, processorEntity);
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, moved);
    }
}

