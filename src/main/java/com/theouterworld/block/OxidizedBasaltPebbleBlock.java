package com.theouterworld.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class OxidizedBasaltPebbleBlock extends Block {
    // Pressure plate-like outline shape (1 pixel high with 1 pixel margins)
    private static final VoxelShape PRESSURE_PLATE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
    
    // Full cube for reliable targeting and breaking
    private static final VoxelShape FULL_CUBE = VoxelShapes.fullCube();
    
    // Small collision shape so entities can walk over it easily
    private static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    public OxidizedBasaltPebbleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Pressure plate-like outline (thin, flat on the ground)
        return PRESSURE_PLATE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Small collision shape so players can walk over it easily
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        // Full cube for reliable targeting and breaking
        return FULL_CUBE;
    }
}

