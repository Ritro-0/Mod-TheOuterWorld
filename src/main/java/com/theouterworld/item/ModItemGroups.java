package com.theouterworld.item;

import com.theouterworld.TemplateMod;
import com.theouterworld.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup OUTER_WORLD_GROUP = Registry.register(Registries.ITEM_GROUP,
        Identifier.of(TemplateMod.MOD_ID, "outer_world"),
        FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.QUANTUM_PAD))
            .displayName(Text.translatable("itemgroup.theouterworld"))
            .entries((displayContext, entries) -> {
                entries.add(ModBlocks.QUANTUM_PAD);
            })
            .build());

    public static void registerItemGroups() {
        TemplateMod.LOGGER.info("Registering Item Groups for " + TemplateMod.MOD_ID);
    }
}

