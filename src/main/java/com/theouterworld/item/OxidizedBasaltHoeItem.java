package com.theouterworld.item;

import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;

public class OxidizedBasaltHoeItem extends HoeItem {
    private static final net.minecraft.registry.tag.TagKey<net.minecraft.block.Block> HOE_MINEABLE = BlockTags.HOE_MINEABLE;
    
    public OxidizedBasaltHoeItem(RegistryKey<net.minecraft.item.Item> registryKey, Settings settings) {
        super(
            ModToolMaterials.OXIDIZED_BASALT_TOOLS,
            0.0f, // attack damage (wood hoe base - 0, shown as 1 in game)
            -3.0f, // attack speed (standard for hoes)
            computeSettings(settings)
        );
    }
    
    private static Settings computeSettings(Settings settings) {
        // Set durability - HoeItem already handles the tool component via .hoe() in constructor
        return settings.maxDamage(ModToolMaterials.OXIDIZED_BASALT_TOOLS_DURABILITY);
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, net.minecraft.block.BlockState state) {
        // Return the mining speed from ToolMaterial when block is mineable, like Paxels does
        return state.isIn(HOE_MINEABLE) ? ModToolMaterials.OXIDIZED_BASALT_TOOLS_MINING_SPEED : 1.0f;
    }
}
