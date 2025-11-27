package com.theouterworld.event;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.block.ModBlocks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.PaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles deferred vanilla iron block replacements.
 * 
 * The actual detection happens in VanillaIronOnPlacedMixin which hooks into Block.onPlaced.
 * When a vanilla iron block/door/trapdoor/chain is placed in the Outerworld, it queues
 * a replacement request here. The tick handler processes those requests on the next tick,
 * allowing block placement to finalize first (important for chains to get their AXIS property).
 */
public class VanillaIronReplacementListener {
    
    /**
     * Queue of pending block replacements. Processed each server tick.
     * Thread-safe for cross-thread access.
     */
    public static final ConcurrentLinkedQueue<PendingReplacement> PENDING_REPLACEMENTS = new ConcurrentLinkedQueue<>();

    /**
     * Gets the replacement block for a vanilla iron block, or null if not applicable.
     * 
     * FIXED: Uses instanceof ChainBlock instead of registry lookup.
     * This is robust, avoids any registry timing/loading issues in 1.21.10+,
     * works identically to how other blocks are detected, and skips custom oxidizable chains.
     */
    public static Block getReplacement(Block placedBlock) {
        if (placedBlock == Blocks.IRON_BLOCK) {
            return ModBlocks.UNAFFECTED_IRON;
        } else if (placedBlock == Blocks.IRON_TRAPDOOR) {
            return ModBlocks.UNAFFECTED_IRON_TRAPDOOR;
        } else if (placedBlock == Blocks.IRON_BARS) {
            return ModBlocks.UNAFFECTED_IRON_BARS;
        } else if (placedBlock instanceof ChainBlock && !(placedBlock instanceof Oxidizable)) {
            // Direct type check on ChainBlock - fast, reliable, no registry lookups
            // The !(instanceof Oxidizable) check is explicit safety to prevent loops
            return ModBlocks.UNAFFECTED_IRON_CHAIN;
        }
        return null;
    }

    /**
     * Registers the server tick handler that processes pending iron block replacements.
     */
    public static void register() {
        // Process pending replacements at the end of each server tick
        // This runs AFTER block placement has fully finalized (AXIS, etc.)
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            processPendingReplacements();
        });
    }

    /**
     * Process all pending replacements.
     */
    private static void processPendingReplacements() {
        PendingReplacement pending;
        while ((pending = PENDING_REPLACEMENTS.poll()) != null) {
            ServerWorld world = pending.world();
            BlockPos pos = pending.pos();
            Block replacement = pending.replacement();

            // Re-check the block is still vanilla (might have been broken/changed)
            BlockState currentState = world.getBlockState(pos);
            Block currentBlock = currentState.getBlock();

            // Skip if already replaced or block was broken
            if (currentBlock instanceof Oxidizable) continue;
            if (currentBlock == Blocks.AIR) continue;

            // Verify it's still a target block
            Block expectedReplacement = getReplacement(currentBlock);
            if (expectedReplacement != replacement) continue;

            // DEBUG: Log chain replacements (remove after verification)
            if (replacement == ModBlocks.UNAFFECTED_IRON_CHAIN) {
                OuterWorldMod.LOGGER.info("[ChainDebug] Replacing chain at {} with {}", pos, replacement);
            }

            // Do the replacement, preserving all properties (AXIS, FACING, etc.)
            BlockState newState = replacement.getStateWithProperties(currentState);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        }
    }

    /**
     * Record for pending replacements.
     */
    public record PendingReplacement(ServerWorld world, BlockPos pos, Block replacement) {}
}
