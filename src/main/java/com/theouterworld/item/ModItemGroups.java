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
                entries.add(ModBlocks.QUANTUM_PAD);
                entries.add(ModBlocks.REGOLITH);
                entries.add(ModBlocks.OXIDIZED_BASALT);
            })
            .build());

    public static void registerItemGroups() {
        OuterWorldMod.LOGGER.info("Registering Item Groups for " + OuterWorldMod.MOD_ID);
    }
}

