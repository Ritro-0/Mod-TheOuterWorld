package com.theouterworld.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;

import java.util.Map;

public class OxidizedBasaltShovelItem extends Item {
    private static final net.minecraft.registry.tag.TagKey<net.minecraft.block.Block> SHOVEL_MINEABLE = BlockTags.SHOVEL_MINEABLE;
    private static final Map<Block, BlockState> FLATTENABLES = Shovel.getFlattenables();
    
    public OxidizedBasaltShovelItem(RegistryKey<Item> registryKey, Item.Settings settings) {
        super(computeSettings(settings));
    }
    
    private static Item.Settings computeSettings(Item.Settings settings) {
        // Use .shovel() method like Paxels does
        return settings
            .maxDamage(ModToolMaterials.OXIDIZED_BASALT_TOOLS_DURABILITY)
            .shovel(
                ModToolMaterials.OXIDIZED_BASALT_TOOLS,
                1.5f, // attack damage (wood shovel base)
                -3.0f // attack speed (standard for shovels)
            );
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        // Return the mining speed from ToolMaterial when block is mineable, like Paxels does
        return state.isIn(SHOVEL_MINEABLE) ? ModToolMaterials.OXIDIZED_BASALT_TOOLS_MINING_SPEED : 1.0f;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        
        // Handle path creation (like vanilla shovels)
        if (context.getSide() != Direction.DOWN && FLATTENABLES.containsKey(state.getBlock()) && world.getBlockState(pos.up()).isAir()) {
            world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient()) {
                world.setBlockState(pos, FLATTENABLES.get(state.getBlock()), 11);
                if (context.getPlayer() != null) {
                    context.getStack().damage(1, context.getPlayer(), net.minecraft.util.Hand.MAIN_HAND);
                }
            }
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }
    
    // Inner class to access protected PATH_STATES field, like Paxels does
    private static final class Shovel extends ShovelItem {
        public static Map<Block, BlockState> getFlattenables() {
            return ShovelItem.PATH_STATES;
        }
        
        private Shovel(net.minecraft.item.ToolMaterial tier, float attackDamage, float attackSpeed, Settings settings) {
            super(tier, attackDamage, attackSpeed, settings);
        }
    }
}

