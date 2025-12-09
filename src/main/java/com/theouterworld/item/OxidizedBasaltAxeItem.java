package com.theouterworld.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;

public class OxidizedBasaltAxeItem extends Item {
    private static final net.minecraft.registry.tag.TagKey<net.minecraft.block.Block> AXE_MINEABLE = BlockTags.AXE_MINEABLE;
    private static final Map<Block, Block> STRIPPED_BLOCKS = Axe.getStrippables();
    
    public OxidizedBasaltAxeItem(RegistryKey<Item> registryKey, Item.Settings settings) {
        super(computeSettings(settings));
    }
    
    private static Item.Settings computeSettings(Item.Settings settings) {
        // Use .axe() method like Paxels does
        return settings
            .maxDamage(ModToolMaterials.OXIDIZED_BASALT_TOOLS_DURABILITY)
            .axe(
                ModToolMaterials.OXIDIZED_BASALT_TOOLS,
                6.0f, // attack damage (wood axe base)
                -3.2f // attack speed (standard for axes)
            );
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        // Return the mining speed from ToolMaterial when block is mineable, like Paxels does
        return state.isIn(AXE_MINEABLE) ? ModToolMaterials.OXIDIZED_BASALT_TOOLS_MINING_SPEED : 1.0f;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        
        // Cancel if player is holding shield in offhand (like vanilla axes)
        if (player != null && context.getHand() == Hand.MAIN_HAND && player.getOffHandStack().isOf(net.minecraft.item.Items.SHIELD) && !player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        
        // Handle wood stripping (like vanilla axes)
        if (STRIPPED_BLOCKS.containsKey(state.getBlock())) {
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient()) {
                BlockState strippedState = STRIPPED_BLOCKS.get(state.getBlock()).getDefaultState();
                // Preserve axis property if present
                if (state.contains(PillarBlock.AXIS)) {
                    strippedState = strippedState.with(PillarBlock.AXIS, state.get(PillarBlock.AXIS));
                }
                world.setBlockState(pos, strippedState, 11);
                if (player != null) {
                    context.getStack().damage(1, player, Hand.MAIN_HAND);
                }
            }
            return ActionResult.SUCCESS;
        }
        
        // Handle campfire extinguishing (like vanilla axes)
        if (state.getBlock() instanceof net.minecraft.block.CampfireBlock && state.get(net.minecraft.block.CampfireBlock.LIT)) {
            world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(net.minecraft.block.CampfireBlock.LIT, false), 11);
                if (player != null) {
                    context.getStack().damage(1, player, Hand.MAIN_HAND);
                }
            }
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }
    
    // Inner class to access protected STRIPPED_BLOCKS field, like Paxels does
    private static final class Axe extends AxeItem {
        public static Map<Block, Block> getStrippables() {
            return AxeItem.STRIPPED_BLOCKS;
        }
        
        private Axe(net.minecraft.item.ToolMaterial tier, float attackDamage, float attackSpeed, Settings settings) {
            super(tier, attackDamage, attackSpeed, settings);
        }
    }
}

