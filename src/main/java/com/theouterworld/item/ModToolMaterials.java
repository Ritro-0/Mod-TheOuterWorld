package com.theouterworld.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

public class ModToolMaterials {
    // Oxidized Basalt Pick (original, working configuration - DO NOT CHANGE)
    public static final int OXIDIZED_BASALT_DURABILITY = 44; // ~75% of wood (59)
    public static final float OXIDIZED_BASALT_MINING_SPEED = 0.3f; // Matches hand speed on requiresTool blocks (~6.25s)
    public static final int OXIDIZED_BASALT_MINING_LEVEL = 0;
    public static final float OXIDIZED_BASALT_ATTACK_DAMAGE = 1.0f;
    public static final int OXIDIZED_BASALT_ENCHANTABILITY = 5;
    
    // Create a ToolMaterial for the oxidized basalt pick (original configuration)
    public static final ToolMaterial OXIDIZED_BASALT = new ToolMaterial(
        BlockTags.INCORRECT_FOR_WOODEN_TOOL, // Standard tag like Paxels uses
        OXIDIZED_BASALT_DURABILITY,
        OXIDIZED_BASALT_MINING_SPEED,
        OXIDIZED_BASALT_ATTACK_DAMAGE,
        OXIDIZED_BASALT_ENCHANTABILITY,
        ItemTags.STONE_TOOL_MATERIALS // repair items (using stone as placeholder)
    );
    
    // Oxidized Basalt Tools (new full toolset - wood-tier)
    public static final int OXIDIZED_BASALT_TOOLS_DURABILITY = 30; // Half of wood (59)
    public static final float OXIDIZED_BASALT_TOOLS_MINING_SPEED = 2.0f; // Same as wood
    public static final float OXIDIZED_BASALT_TOOLS_ATTACK_DAMAGE = 1.0f;
    
    // Create a ToolMaterial for the new oxidized basalt tools (normal wood-tier)
    public static final ToolMaterial OXIDIZED_BASALT_TOOLS = new ToolMaterial(
        BlockTags.INCORRECT_FOR_WOODEN_TOOL, // Standard tag like Paxels uses
        OXIDIZED_BASALT_TOOLS_DURABILITY,
        OXIDIZED_BASALT_TOOLS_MINING_SPEED,
        OXIDIZED_BASALT_TOOLS_ATTACK_DAMAGE,
        OXIDIZED_BASALT_ENCHANTABILITY,
        ItemTags.STONE_TOOL_MATERIALS // repair items (using stone as placeholder)
    );
}
