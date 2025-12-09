package com.theouterworld.event;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.block.OxidizableIronBlock;
import com.theouterworld.block.OxidizableIronBarsBlock;
import com.theouterworld.block.OxidizableIronBulbBlock;
import com.theouterworld.block.OxidizableIronChainBlock;
import com.theouterworld.block.OxidizableIronDoorBlock;
import com.theouterworld.block.OxidizableIronTrapdoorBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.BulbBlock;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.GrateBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles deferred vanilla copper block replacements.
 * 
 * The actual detection happens in VanillaCopperOnPlacedMixin which hooks into Block.onPlaced.
 * When a vanilla copper block is placed in the Outerworld, it queues
 * a replacement request here. The tick handler processes those requests on the next tick,
 * allowing block placement to finalize first (important for blocks to get their properties).
 */
public class VanillaCopperReplacementListener {
    
    /**
     * Queue of pending block replacements. Processed each server tick.
     * Thread-safe for cross-thread access.
     */
    public static final ConcurrentLinkedQueue<PendingReplacement> PENDING_REPLACEMENTS = new ConcurrentLinkedQueue<>();

    /**
     * Gets the replacement block for a vanilla copper block, or null if not applicable.
     * 
     * Replaces all oxidizable copper blocks with non-oxidizable versions.
     */
    public static Block getReplacement(Block placedBlock) {
        // Check if it's an oxidizable copper block
        if (!(placedBlock instanceof Oxidizable)) {
            return null;
        }

        // Chiseled copper blocks (check first since they're regular blocks)
        if (placedBlock == Blocks.CHISELED_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_CHISELED_COPPER;
        } else if (placedBlock == Blocks.EXPOSED_CHISELED_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_EXPOSED_CHISELED_COPPER;
        } else if (placedBlock == Blocks.WEATHERED_CHISELED_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_WEATHERED_CHISELED_COPPER;
        } else if (placedBlock == Blocks.OXIDIZED_CHISELED_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_OXIDIZED_CHISELED_COPPER;
        }
        
        // Map vanilla copper blocks to non-oxidizable versions
        if (placedBlock == Blocks.COPPER_BLOCK) {
            return ModBlocks.NON_OXIDIZABLE_COPPER_BLOCK;
        } else if (placedBlock == Blocks.EXPOSED_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_EXPOSED_COPPER;
        } else if (placedBlock == Blocks.WEATHERED_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_WEATHERED_COPPER;
        } else if (placedBlock == Blocks.OXIDIZED_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_OXIDIZED_COPPER;
        } else if (placedBlock == Blocks.CUT_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_CUT_COPPER;
        } else if (placedBlock == Blocks.EXPOSED_CUT_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_EXPOSED_CUT_COPPER;
        } else if (placedBlock == Blocks.WEATHERED_CUT_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_WEATHERED_CUT_COPPER;
        } else if (placedBlock == Blocks.OXIDIZED_CUT_COPPER) {
            return ModBlocks.NON_OXIDIZABLE_OXIDIZED_CUT_COPPER;
        } else if (placedBlock == Blocks.CUT_COPPER_SLAB) {
            return ModBlocks.NON_OXIDIZABLE_CUT_COPPER_SLAB;
        } else if (placedBlock == Blocks.EXPOSED_CUT_COPPER_SLAB) {
            return ModBlocks.NON_OXIDIZABLE_EXPOSED_CUT_COPPER_SLAB;
        } else if (placedBlock == Blocks.WEATHERED_CUT_COPPER_SLAB) {
            return ModBlocks.NON_OXIDIZABLE_WEATHERED_CUT_COPPER_SLAB;
        } else if (placedBlock == Blocks.OXIDIZED_CUT_COPPER_SLAB) {
            return ModBlocks.NON_OXIDIZABLE_OXIDIZED_CUT_COPPER_SLAB;
        } else if (placedBlock == Blocks.CUT_COPPER_STAIRS) {
            return ModBlocks.NON_OXIDIZABLE_CUT_COPPER_STAIRS;
        } else if (placedBlock == Blocks.EXPOSED_CUT_COPPER_STAIRS) {
            return ModBlocks.NON_OXIDIZABLE_EXPOSED_CUT_COPPER_STAIRS;
        } else if (placedBlock == Blocks.WEATHERED_CUT_COPPER_STAIRS) {
            return ModBlocks.NON_OXIDIZABLE_WEATHERED_CUT_COPPER_STAIRS;
        } else if (placedBlock == Blocks.OXIDIZED_CUT_COPPER_STAIRS) {
            return ModBlocks.NON_OXIDIZABLE_OXIDIZED_CUT_COPPER_STAIRS;
        } else if (placedBlock instanceof BulbBlock && !(placedBlock instanceof OxidizableIronBulbBlock)) {
            // Copper bulbs - check by type since they're all BulbBlocks
            if (placedBlock == Blocks.COPPER_BULB) {
                return ModBlocks.NON_OXIDIZABLE_COPPER_BULB;
            } else if (placedBlock == Blocks.EXPOSED_COPPER_BULB) {
                return ModBlocks.NON_OXIDIZABLE_EXPOSED_COPPER_BULB;
            } else if (placedBlock == Blocks.WEATHERED_COPPER_BULB) {
                return ModBlocks.NON_OXIDIZABLE_WEATHERED_COPPER_BULB;
            } else if (placedBlock == Blocks.OXIDIZED_COPPER_BULB) {
                return ModBlocks.NON_OXIDIZABLE_OXIDIZED_COPPER_BULB;
            }
        } else if (placedBlock instanceof GrateBlock && !(placedBlock instanceof OxidizableIronBlock)) {
            // Copper grates
            if (placedBlock == Blocks.COPPER_GRATE) {
                return ModBlocks.NON_OXIDIZABLE_COPPER_GRATE;
            } else if (placedBlock == Blocks.EXPOSED_COPPER_GRATE) {
                return ModBlocks.NON_OXIDIZABLE_EXPOSED_COPPER_GRATE;
            } else if (placedBlock == Blocks.WEATHERED_COPPER_GRATE) {
                return ModBlocks.NON_OXIDIZABLE_WEATHERED_COPPER_GRATE;
            } else if (placedBlock == Blocks.OXIDIZED_COPPER_GRATE) {
                return ModBlocks.NON_OXIDIZABLE_OXIDIZED_COPPER_GRATE;
            }
        } else if (placedBlock instanceof DoorBlock && !(placedBlock instanceof OxidizableIronDoorBlock)) {
            // Copper doors - handled by VanillaCopperDoorOnPlacedMixin directly
            // Return null here to avoid double replacement
            return null;
        } else if (placedBlock instanceof TrapdoorBlock && !(placedBlock instanceof OxidizableIronTrapdoorBlock)) {
            // Copper trapdoors
            if (placedBlock == Blocks.COPPER_TRAPDOOR) {
                return ModBlocks.NON_OXIDIZABLE_COPPER_TRAPDOOR;
            } else if (placedBlock == Blocks.EXPOSED_COPPER_TRAPDOOR) {
                return ModBlocks.NON_OXIDIZABLE_EXPOSED_COPPER_TRAPDOOR;
            } else if (placedBlock == Blocks.WEATHERED_COPPER_TRAPDOOR) {
                return ModBlocks.NON_OXIDIZABLE_WEATHERED_COPPER_TRAPDOOR;
            } else if (placedBlock == Blocks.OXIDIZED_COPPER_TRAPDOOR) {
                return ModBlocks.NON_OXIDIZABLE_OXIDIZED_COPPER_TRAPDOOR;
            }
        } else if (placedBlock instanceof PaneBlock && !(placedBlock instanceof OxidizableIronBarsBlock)) {
            // Copper bars - check by registry name
            net.minecraft.util.Identifier blockId = net.minecraft.registry.Registries.BLOCK.getId(placedBlock);
            if (blockId != null && blockId.getNamespace().equals("minecraft")) {
                String path = blockId.getPath();
                if (path.equals("copper_bars")) {
                    return ModBlocks.NON_OXIDIZABLE_COPPER_BARS;
                } else if (path.equals("exposed_copper_bars")) {
                    return ModBlocks.NON_OXIDIZABLE_EXPOSED_COPPER_BARS;
                } else if (path.equals("weathered_copper_bars")) {
                    return ModBlocks.NON_OXIDIZABLE_WEATHERED_COPPER_BARS;
                } else if (path.equals("oxidized_copper_bars")) {
                    return ModBlocks.NON_OXIDIZABLE_OXIDIZED_COPPER_BARS;
                }
            }
        } else if (placedBlock instanceof ChainBlock && !(placedBlock instanceof OxidizableIronChainBlock)) {
            // Copper chains - check by registry name
            net.minecraft.util.Identifier blockId = net.minecraft.registry.Registries.BLOCK.getId(placedBlock);
            if (blockId != null && blockId.getNamespace().equals("minecraft")) {
                String path = blockId.getPath();
                if (path.equals("copper_chain")) {
                    return ModBlocks.NON_OXIDIZABLE_COPPER_CHAIN;
                } else if (path.equals("exposed_copper_chain")) {
                    return ModBlocks.NON_OXIDIZABLE_EXPOSED_COPPER_CHAIN;
                } else if (path.equals("weathered_copper_chain")) {
                    return ModBlocks.NON_OXIDIZABLE_WEATHERED_COPPER_CHAIN;
                } else if (path.equals("oxidized_copper_chain")) {
                    return ModBlocks.NON_OXIDIZABLE_OXIDIZED_COPPER_CHAIN;
                }
            }
        }
        
        return null;
    }

    /**
     * Registers the server tick handler that processes pending copper block replacements.
     */
    public static void register() {
        // Process pending replacements at the end of each server tick
        // This runs AFTER block placement has fully finalized
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

            // Re-check the block is still oxidizable (might have been broken/changed)
            BlockState currentState = world.getBlockState(pos);
            Block currentBlock = currentState.getBlock();

            // Skip if already replaced or block was broken
            if (currentBlock instanceof Oxidizable) {
                // Check if it's still a copper block that needs replacement
                Block expectedReplacement = getReplacement(currentBlock);
                if (expectedReplacement != replacement) continue;
            } else {
                continue; // Already replaced or not a copper block
            }
            if (currentBlock == Blocks.AIR) continue;

            // Do the replacement, preserving all properties
            BlockState newState = replacement.getStateWithProperties(currentState);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        }
    }

    /**
     * Record for pending replacements.
     */
    public record PendingReplacement(ServerWorld world, BlockPos pos, Block replacement) {}
}

