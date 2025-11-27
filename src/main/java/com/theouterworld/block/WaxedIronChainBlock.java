package com.theouterworld.block;

import com.theouterworld.registry.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WaxedIronChainBlock extends ChainBlock {
    private final Block unwaxedVersion;

    public WaxedIronChainBlock(Block unwaxedVersion, Settings settings) {
        super(settings);
        this.unwaxedVersion = unwaxedVersion;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, net.minecraft.entity.player.PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        if (stack.getItem() instanceof AxeItem) {
            if (world instanceof ServerWorld serverWorld) {
                // In Outerworld: return to unaffected iron chain (which will oxidize)
                // Outside Outerworld: return to vanilla chain
                Block targetBlock;
                if (serverWorld.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
                    targetBlock = unwaxedVersion; // UNAFFECTED_IRON_CHAIN in Outerworld
                } else {
                    // Return to vanilla chain outside Outerworld
                    targetBlock = Registries.BLOCK.get(Identifier.ofVanilla("chain"));
                }
                
                BlockState newState = targetBlock.getDefaultState();
                if (newState.contains(AXIS)) {
                    newState = newState.with(AXIS, state.get(AXIS));
                }
                world.setBlockState(pos, newState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.playSound(null, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                if (!player.isCreative()) {
                    stack.damage(1, player, player.getActiveHand());
                }
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }

    public Block getUnwaxedVersion() {
        return unwaxedVersion;
    }
}

