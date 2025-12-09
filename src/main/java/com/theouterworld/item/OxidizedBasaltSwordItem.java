package com.theouterworld.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;

public class OxidizedBasaltSwordItem extends Item {
    private static final net.minecraft.registry.tag.TagKey<net.minecraft.block.Block> SWORD_MINEABLE = BlockTags.SWORD_EFFICIENT;
    private final net.minecraft.item.ToolMaterial tier;

    public OxidizedBasaltSwordItem(RegistryKey<Item> registryKey, Item.Settings settings) {
        super(computeSettings(settings));
        this.tier = ModToolMaterials.OXIDIZED_BASALT_TOOLS;
    }
    
    private static Item.Settings computeSettings(Item.Settings settings) {
        return settings
            .maxDamage(ModToolMaterials.OXIDIZED_BASALT_TOOLS_DURABILITY)
            .tool(
                ModToolMaterials.OXIDIZED_BASALT_TOOLS,
                BlockTags.SWORD_EFFICIENT,
                3.0f, // attack damage (wood sword base)
                -2.4f, // attack speed (standard for swords)
                0.0f   // default damage blocked
            );
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        return state.isIn(SWORD_MINEABLE) ? ModToolMaterials.OXIDIZED_BASALT_TOOLS_MINING_SPEED : 1.0f;
    }
    
    @Override
    public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        return !state.isToolRequired() || state.isIn(SWORD_MINEABLE);
    }
}

