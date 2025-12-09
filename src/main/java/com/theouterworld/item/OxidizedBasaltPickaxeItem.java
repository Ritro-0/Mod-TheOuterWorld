package com.theouterworld.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;

public class OxidizedBasaltPickaxeItem extends Item {
    private static final net.minecraft.registry.tag.TagKey<net.minecraft.block.Block> PICKAXE_MINEABLE = BlockTags.PICKAXE_MINEABLE;
    
    public OxidizedBasaltPickaxeItem(RegistryKey<Item> registryKey, Item.Settings settings) {
        super(computeSettings(settings));
    }
    
    private static Item.Settings computeSettings(Item.Settings settings) {
        // Use the settings.pickaxe() method like Paxels does
        return settings
            .maxDamage(ModToolMaterials.OXIDIZED_BASALT_TOOLS_DURABILITY)
            .pickaxe(
                ModToolMaterials.OXIDIZED_BASALT_TOOLS,
                1.0f, // attack damage (wood pickaxe base)
                -2.8f // attack speed (standard for pickaxes)
            );
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        // Return the mining speed from ToolMaterial when block is mineable, like Paxels does
        return state.isIn(PICKAXE_MINEABLE) ? ModToolMaterials.OXIDIZED_BASALT_TOOLS_MINING_SPEED : 1.0f;
    }
}

