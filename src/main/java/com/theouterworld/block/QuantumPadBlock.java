package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class QuantumPadBlock extends Block {
    // Custom shape for the block (smaller than full block since it's a pad)
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(0, 0, 0, 16, 1, 16), // Base pad
        Block.createCuboidShape(7.36, 0, 7.64, 8.36, 24, 8.64) // Center pillar
    );

    public QuantumPadBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}

