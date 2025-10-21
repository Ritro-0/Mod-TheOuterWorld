package com.theouterworld.block;

import com.theouterworld.TemplateMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block QUANTUM_PAD = registerBlock(
        "quantum_pad",
        key -> new QuantumPadBlock(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .luminance(state -> 7) // Emits light level 7
                .requiresTool()
        )
    );

    private static Block registerBlock(String name, java.util.function.Function<RegistryKey<Block>, Block> factory) {
        Identifier id = Identifier.of(TemplateMod.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(Registries.BLOCK.getKey(), id);
        // Construct with registry key to satisfy settings that need an id during construction
        Block block = factory.apply(key);
        // Register the block
        Registry.register(Registries.BLOCK, id, block);
        // Register the block item so it appears in inventory and can be placed
        RegistryKey<Item> itemKey = RegistryKey.of(Registries.ITEM.getKey(), id);
        Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);
        BlockItem item = new BlockItem(block, itemSettings);
        Registry.register(Registries.ITEM, id, item);
        return block;
    }

    public static void registerModBlocks() {
        TemplateMod.LOGGER.info("Registering blocks for " + TemplateMod.MOD_ID);
    }
}

