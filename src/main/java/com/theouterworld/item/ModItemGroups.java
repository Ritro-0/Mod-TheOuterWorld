package com.theouterworld.item;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.block.ModBlocks;
import com.theouterworld.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup OUTER_WORLD_GROUP = Registry.register(Registries.ITEM_GROUP,
        Identifier.of(OuterWorldMod.MOD_ID, "outer_world"),
        FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.QUANTUM_PAD))
            .displayName(Text.translatable("itemgroup.theouterworld"))
            .entries((displayContext, entries) -> {
                entries.add(ModItems.EMERGENCY_RETURN_PAD);
                entries.add(ModItems.BROKEN_EMERGENCY_RETURN_PAD);
                entries.add(ModBlocks.QUANTUM_PAD);
                entries.add(ModBlocks.REGOLITH);
                entries.add(ModBlocks.OXIDIZED_BASALT);
                entries.add(ModBlocks.EXPOSED_IRON);
                entries.add(ModBlocks.WEATHERED_IRON);
                entries.add(ModBlocks.OXIDIZED_IRON);
                entries.add(ModBlocks.WAXED_EXPOSED_IRON);
                entries.add(ModBlocks.WAXED_WEATHERED_IRON);
                entries.add(ModBlocks.WAXED_OXIDIZED_IRON);
                entries.add(ModBlocks.IRON_BULB);
                entries.add(ModBlocks.EXPOSED_IRON_BULB);
                entries.add(ModBlocks.WEATHERED_IRON_BULB);
                entries.add(ModBlocks.OXIDIZED_IRON_BULB);
                entries.add(ModBlocks.WAXED_IRON_BULB);
                entries.add(ModBlocks.WAXED_EXPOSED_IRON_BULB);
                entries.add(ModBlocks.WAXED_WEATHERED_IRON_BULB);
                entries.add(ModBlocks.WAXED_OXIDIZED_IRON_BULB);
                entries.add(ModBlocks.EXPOSED_IRON_CHAIN);
                entries.add(ModBlocks.WEATHERED_IRON_CHAIN);
                entries.add(ModBlocks.OXIDIZED_IRON_CHAIN);
                entries.add(ModBlocks.WAXED_EXPOSED_IRON_CHAIN);
                entries.add(ModBlocks.WAXED_WEATHERED_IRON_CHAIN);
                entries.add(ModBlocks.WAXED_OXIDIZED_IRON_CHAIN);
                entries.add(ModBlocks.EXPOSED_IRON_DOOR);
                entries.add(ModBlocks.WEATHERED_IRON_DOOR);
                entries.add(ModBlocks.OXIDIZED_IRON_DOOR);
                entries.add(ModBlocks.WAXED_EXPOSED_IRON_DOOR);
                entries.add(ModBlocks.WAXED_WEATHERED_IRON_DOOR);
                entries.add(ModBlocks.WAXED_OXIDIZED_IRON_DOOR);
                entries.add(ModBlocks.EXPOSED_IRON_TRAPDOOR);
                entries.add(ModBlocks.WEATHERED_IRON_TRAPDOOR);
                entries.add(ModBlocks.OXIDIZED_IRON_TRAPDOOR);
                entries.add(ModBlocks.WAXED_EXPOSED_IRON_TRAPDOOR);
                entries.add(ModBlocks.WAXED_WEATHERED_IRON_TRAPDOOR);
                entries.add(ModBlocks.WAXED_OXIDIZED_IRON_TRAPDOOR);
                entries.add(ModBlocks.IRON_GRATE);
                entries.add(ModBlocks.EXPOSED_IRON_GRATE);
                entries.add(ModBlocks.WEATHERED_IRON_GRATE);
                entries.add(ModBlocks.OXIDIZED_IRON_GRATE);
                entries.add(ModBlocks.WAXED_IRON_GRATE);
                entries.add(ModBlocks.WAXED_EXPOSED_IRON_GRATE);
                entries.add(ModBlocks.WAXED_WEATHERED_IRON_GRATE);
                entries.add(ModBlocks.WAXED_OXIDIZED_IRON_GRATE);
                entries.add(ModBlocks.ANORTHOSITE);
                entries.add(ModBlocks.ANORTHOSITE_COPPER_ORE);
                entries.add(ModBlocks.ANORTHOSITE_GOLD_ORE);
                entries.add(ModBlocks.ANORTHOSITE_OXIDIZED_IRON_ORE);
                entries.add(ModBlocks.ANORTHOSITE_REDSTONE_ORE);
                entries.add(ModItems.OXIDIZED_BASALT_PEBBLE);
                entries.add(ModItems.OXIDIZED_BASALT_ROCK);
                entries.add(ModItems.OXIDIZED_BASALT_PICK);
            })
            .build());

    public static void registerItemGroups() {
        OuterWorldMod.LOGGER.info("Registering Item Groups for " + OuterWorldMod.MOD_ID);
    }
}

