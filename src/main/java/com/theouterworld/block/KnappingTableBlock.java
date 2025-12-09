package com.theouterworld.block;

import com.mojang.serialization.MapCodec;
import com.theouterworld.screen.KnappingTableScreenHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class KnappingTableBlock extends Block {
    public static final MapCodec<KnappingTableBlock> CODEC = createCodec(KnappingTableBlock::new);
    private static final Text TITLE = Text.translatable("container.knapping_table");
    
    // Half-slab shape (8 pixels tall)
    private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public KnappingTableBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<KnappingTableBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BOTTOM_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        
        // Open the crafting screen directly
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        return ActionResult.CONSUME;
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(
            (syncId, playerInventory, player) -> 
                new KnappingTableScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos)),
            TITLE
        );
    }
}

