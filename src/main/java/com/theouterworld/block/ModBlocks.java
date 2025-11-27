package com.theouterworld.block;

import com.theouterworld.OuterWorldMod;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
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
                .luminance(state -> 7)
                .requiresTool()
                .dropsNothing()
        )
    );

    public static final Block REGOLITH = registerBlock(
        "regolith",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(0.5f, 0.5f)
                .sounds(BlockSoundGroup.SAND)
        )
    );

    public static final Block OXIDIZED_BASALT = registerBlock(
        "oxidized_basalt",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .mapColor(MapColor.GRAY)
                .strength(1.25f, 4.2f)
                .sounds(BlockSoundGroup.BASALT)
                .requiresTool()
        )
    );

    // Oxidizable Iron blocks - oxidize only in Outerworld dimension
    
    // UNAFFECTED stage clones of vanilla blocks (hidden - no BlockItems)
    // These replace vanilla blocks when placed in Outerworld
    // They drop vanilla items via loot tables
    public static final Block UNAFFECTED_IRON = registerBlockOnly(
        "unaffected_iron",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );
    
    public static final Block EXPOSED_IRON = registerBlock(
        "exposed_iron",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_IRON = registerBlock(
        "weathered_iron",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_IRON = registerBlock(
        "oxidized_iron",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    // Waxed iron blocks
    public static final Block WAXED_EXPOSED_IRON = registerBlock(
        "waxed_exposed_iron",
        key -> new WaxedIronBlock(
            EXPOSED_IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    public static final Block WAXED_WEATHERED_IRON = registerBlock(
        "waxed_weathered_iron",
        key -> new WaxedIronBlock(
            WEATHERED_IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    public static final Block WAXED_OXIDIZED_IRON = registerBlock(
        "waxed_oxidized_iron",
        key -> new WaxedIronBlock(
            OXIDIZED_IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    // Waxed vanilla iron - looks like vanilla but won't oxidize in Outerworld
    public static final Block WAXED_IRON = registerBlock(
        "waxed_iron",
        key -> new WaxedIronBlock(
            UNAFFECTED_IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    // Anorthosite - clone of deepslate
    public static final Block ANORTHOSITE = registerBlock(
        "anorthosite",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f, 6.0f)
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    // Anorthosite ores
    public static final Block ANORTHOSITE_COPPER_ORE = registerBlock(
        "anorthosite_copper_ore",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(4.5f, 3.0f)
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    public static final Block ANORTHOSITE_GOLD_ORE = registerBlock(
        "anorthosite_gold_ore",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(4.5f, 3.0f)
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    public static final Block ANORTHOSITE_OXIDIZED_IRON_ORE = registerBlock(
        "anorthosite_oxidized_iron_ore",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(4.5f, 3.0f)
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    public static final Block ANORTHOSITE_REDSTONE_ORE = registerBlock(
        "anorthosite_redstone_ore",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(4.5f, 3.0f)
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    // Iron Bulb blocks
    public static final Block IRON_BULB = registerBlock(
        "iron_bulb",
        key -> new OxidizableIronBulbBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f, 3.0f)
                .sounds(BlockSoundGroup.COPPER_BULB)
                .requiresTool()
                .ticksRandomly()
                .luminance(state -> state.get(OxidizableIronBulbBlock.LIT) ? 15 : 0)
        )
    );

    public static final Block EXPOSED_IRON_BULB = registerBlock(
        "exposed_iron_bulb",
        key -> new OxidizableIronBulbBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f, 3.0f)
                .sounds(BlockSoundGroup.COPPER_BULB)
                .requiresTool()
                .ticksRandomly()
                .luminance(state -> state.get(OxidizableIronBulbBlock.LIT) ? 12 : 0)
        )
    );

    public static final Block WEATHERED_IRON_BULB = registerBlock(
        "weathered_iron_bulb",
        key -> new OxidizableIronBulbBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f, 3.0f)
                .sounds(BlockSoundGroup.COPPER_BULB)
                .requiresTool()
                .ticksRandomly()
                .luminance(state -> state.get(OxidizableIronBulbBlock.LIT) ? 8 : 0)
        )
    );

    public static final Block OXIDIZED_IRON_BULB = registerBlock(
        "oxidized_iron_bulb",
        key -> new OxidizableIronBulbBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f, 3.0f)
                .sounds(BlockSoundGroup.COPPER_BULB)
                .requiresTool()
                .luminance(state -> state.get(OxidizableIronBulbBlock.LIT) ? 4 : 0)
        )
    );

    public static final Block WAXED_IRON_BULB = registerBlock("waxed_iron_bulb",
        key -> new WaxedIronBulbBlock(IRON_BULB, AbstractBlock.Settings.create().registryKey(key).strength(3.0f, 3.0f).sounds(BlockSoundGroup.COPPER_BULB).requiresTool().luminance(state -> state.get(WaxedIronBulbBlock.LIT) ? 15 : 0)));

    public static final Block WAXED_EXPOSED_IRON_BULB = registerBlock("waxed_exposed_iron_bulb",
        key -> new WaxedIronBulbBlock(EXPOSED_IRON_BULB, AbstractBlock.Settings.create().registryKey(key).strength(3.0f, 3.0f).sounds(BlockSoundGroup.COPPER_BULB).requiresTool().luminance(state -> state.get(WaxedIronBulbBlock.LIT) ? 12 : 0)));

    public static final Block WAXED_WEATHERED_IRON_BULB = registerBlock("waxed_weathered_iron_bulb",
        key -> new WaxedIronBulbBlock(WEATHERED_IRON_BULB, AbstractBlock.Settings.create().registryKey(key).strength(3.0f, 3.0f).sounds(BlockSoundGroup.COPPER_BULB).requiresTool().luminance(state -> state.get(WaxedIronBulbBlock.LIT) ? 8 : 0)));

    public static final Block WAXED_OXIDIZED_IRON_BULB = registerBlock("waxed_oxidized_iron_bulb",
        key -> new WaxedIronBulbBlock(OXIDIZED_IRON_BULB, AbstractBlock.Settings.create().registryKey(key).strength(3.0f, 3.0f).sounds(BlockSoundGroup.COPPER_BULB).requiresTool().luminance(state -> state.get(WaxedIronBulbBlock.LIT) ? 4 : 0)));

    // Iron Chain blocks
    public static final Block UNAFFECTED_IRON_CHAIN = registerBlockOnly(
        "unaffected_iron_chain",
        key -> new OxidizableIronChainBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.CHAIN)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );
    
    public static final Block EXPOSED_IRON_CHAIN = registerBlock(
        "exposed_iron_chain",
        key -> new OxidizableIronChainBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.CHAIN)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_IRON_CHAIN = registerBlock(
        "weathered_iron_chain",
        key -> new OxidizableIronChainBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.CHAIN)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_IRON_CHAIN = registerBlock(
        "oxidized_iron_chain",
        key -> new OxidizableIronChainBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.CHAIN)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_EXPOSED_IRON_CHAIN = registerBlock("waxed_exposed_iron_chain",
        key -> new WaxedIronChainBlock(EXPOSED_IRON_CHAIN, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.CHAIN).requiresTool().nonOpaque()));

    public static final Block WAXED_WEATHERED_IRON_CHAIN = registerBlock("waxed_weathered_iron_chain",
        key -> new WaxedIronChainBlock(WEATHERED_IRON_CHAIN, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.CHAIN).requiresTool().nonOpaque()));

    public static final Block WAXED_OXIDIZED_IRON_CHAIN = registerBlock("waxed_oxidized_iron_chain",
        key -> new WaxedIronChainBlock(OXIDIZED_IRON_CHAIN, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.CHAIN).requiresTool().nonOpaque()));

    // Waxed vanilla iron chain - looks like vanilla but won't oxidize in Outerworld
    public static final Block WAXED_IRON_CHAIN = registerBlock("waxed_iron_chain",
        key -> new WaxedIronChainBlock(UNAFFECTED_IRON_CHAIN, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.CHAIN).requiresTool().nonOpaque()));

    // Iron Door blocks
    public static final Block UNAFFECTED_IRON_DOOR = registerBlockOnly(
        "unaffected_iron_door",
        key -> new OxidizableIronDoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );
    
    public static final Block EXPOSED_IRON_DOOR = registerBlock(
        "exposed_iron_door",
        key -> new OxidizableIronDoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_IRON_DOOR = registerBlock(
        "weathered_iron_door",
        key -> new OxidizableIronDoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_IRON_DOOR = registerBlock(
        "oxidized_iron_door",
        key -> new OxidizableIronDoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_EXPOSED_IRON_DOOR = registerBlock("waxed_exposed_iron_door",
        key -> new WaxedIronDoorBlock(EXPOSED_IRON_DOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_WEATHERED_IRON_DOOR = registerBlock("waxed_weathered_iron_door",
        key -> new WaxedIronDoorBlock(WEATHERED_IRON_DOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_OXIDIZED_IRON_DOOR = registerBlock("waxed_oxidized_iron_door",
        key -> new WaxedIronDoorBlock(OXIDIZED_IRON_DOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    // Waxed vanilla iron door - looks like vanilla but won't oxidize in Outerworld
    public static final Block WAXED_IRON_DOOR = registerBlock("waxed_iron_door",
        key -> new WaxedIronDoorBlock(UNAFFECTED_IRON_DOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    // Iron Trapdoor blocks
    public static final Block UNAFFECTED_IRON_TRAPDOOR = registerBlockOnly(
        "unaffected_iron_trapdoor",
        key -> new OxidizableIronTrapdoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );
    
    public static final Block EXPOSED_IRON_TRAPDOOR = registerBlock(
        "exposed_iron_trapdoor",
        key -> new OxidizableIronTrapdoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_IRON_TRAPDOOR = registerBlock(
        "weathered_iron_trapdoor",
        key -> new OxidizableIronTrapdoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_IRON_TRAPDOOR = registerBlock(
        "oxidized_iron_trapdoor",
        key -> new OxidizableIronTrapdoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_EXPOSED_IRON_TRAPDOOR = registerBlock("waxed_exposed_iron_trapdoor",
        key -> new WaxedIronTrapdoorBlock(EXPOSED_IRON_TRAPDOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_WEATHERED_IRON_TRAPDOOR = registerBlock("waxed_weathered_iron_trapdoor",
        key -> new WaxedIronTrapdoorBlock(WEATHERED_IRON_TRAPDOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_OXIDIZED_IRON_TRAPDOOR = registerBlock("waxed_oxidized_iron_trapdoor",
        key -> new WaxedIronTrapdoorBlock(OXIDIZED_IRON_TRAPDOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    // Waxed vanilla iron trapdoor - looks like vanilla but won't oxidize in Outerworld
    public static final Block WAXED_IRON_TRAPDOOR = registerBlock("waxed_iron_trapdoor",
        key -> new WaxedIronTrapdoorBlock(UNAFFECTED_IRON_TRAPDOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    // Iron Grate blocks
    public static final Block IRON_GRATE = registerBlock(
        "iron_grate",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block EXPOSED_IRON_GRATE = registerBlock(
        "exposed_iron_grate",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_IRON_GRATE = registerBlock(
        "weathered_iron_grate",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_IRON_GRATE = registerBlock(
        "oxidized_iron_grate",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_IRON_GRATE = registerBlock("waxed_iron_grate",
        key -> new WaxedIronBlock(IRON_GRATE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_EXPOSED_IRON_GRATE = registerBlock("waxed_exposed_iron_grate",
        key -> new WaxedIronBlock(EXPOSED_IRON_GRATE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_WEATHERED_IRON_GRATE = registerBlock("waxed_weathered_iron_grate",
        key -> new WaxedIronBlock(WEATHERED_IRON_GRATE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_OXIDIZED_IRON_GRATE = registerBlock("waxed_oxidized_iron_grate",
        key -> new WaxedIronBlock(OXIDIZED_IRON_GRATE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    // Iron Bars blocks
    public static final Block UNAFFECTED_IRON_BARS = registerBlockOnly(
        "unaffected_iron_bars",
        key -> new OxidizableIronBarsBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block EXPOSED_IRON_BARS = registerBlock(
        "exposed_iron_bars",
        key -> new OxidizableIronBarsBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_IRON_BARS = registerBlock(
        "weathered_iron_bars",
        key -> new OxidizableIronBarsBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_IRON_BARS = registerBlock(
        "oxidized_iron_bars",
        key -> new OxidizableIronBarsBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_EXPOSED_IRON_BARS = registerBlock("waxed_exposed_iron_bars",
        key -> new WaxedIronBarsBlock(EXPOSED_IRON_BARS, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_WEATHERED_IRON_BARS = registerBlock("waxed_weathered_iron_bars",
        key -> new WaxedIronBarsBlock(WEATHERED_IRON_BARS, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_OXIDIZED_IRON_BARS = registerBlock("waxed_oxidized_iron_bars",
        key -> new WaxedIronBarsBlock(OXIDIZED_IRON_BARS, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    // Waxed vanilla iron bars - looks like vanilla but won't oxidize in Outerworld
    public static final Block WAXED_IRON_BARS = registerBlock("waxed_iron_bars",
        key -> new WaxedIronBarsBlock(UNAFFECTED_IRON_BARS, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    // ============= IRON GOLEM STATUES =============
    // Petrified iron golems that can be scraped to de-oxidize and eventually reanimate
    
    public static final Block IRON_GOLEM_STATUE = registerBlock(
        "iron_golem_statue",
        key -> new IronGolemStatueBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block EXPOSED_IRON_GOLEM_STATUE = registerBlock(
        "exposed_iron_golem_statue",
        key -> new IronGolemStatueBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WEATHERED_IRON_GOLEM_STATUE = registerBlock(
        "weathered_iron_golem_statue",
        key -> new IronGolemStatueBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block OXIDIZED_IRON_GOLEM_STATUE = registerBlock(
        "oxidized_iron_golem_statue",
        key -> new IronGolemStatueBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    // Waxed iron golem statues
    public static final Block WAXED_IRON_GOLEM_STATUE = registerBlock("waxed_iron_golem_statue",
        key -> new WaxedIronGolemStatueBlock(IRON_GOLEM_STATUE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_EXPOSED_IRON_GOLEM_STATUE = registerBlock("waxed_exposed_iron_golem_statue",
        key -> new WaxedIronGolemStatueBlock(EXPOSED_IRON_GOLEM_STATUE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_WEATHERED_IRON_GOLEM_STATUE = registerBlock("waxed_weathered_iron_golem_statue",
        key -> new WaxedIronGolemStatueBlock(WEATHERED_IRON_GOLEM_STATUE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    public static final Block WAXED_OXIDIZED_IRON_GOLEM_STATUE = registerBlock("waxed_oxidized_iron_golem_statue",
        key -> new WaxedIronGolemStatueBlock(OXIDIZED_IRON_GOLEM_STATUE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque()));

    // ============= CUT IRON BLOCKS =============
    // Oxidizable decorative cut iron blocks with slab and stair variants
    
    public static final Block CUT_IRON = registerBlock(
        "cut_iron",
        key -> new OxidizableCutIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block EXPOSED_CUT_IRON = registerBlock(
        "exposed_cut_iron",
        key -> new OxidizableCutIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_CUT_IRON = registerBlock(
        "weathered_cut_iron",
        key -> new OxidizableCutIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_CUT_IRON = registerBlock(
        "oxidized_cut_iron",
        key -> new OxidizableCutIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    // Waxed Cut Iron Blocks
    public static final Block WAXED_CUT_IRON = registerBlock("waxed_cut_iron",
        key -> new WaxedCutIronBlock(CUT_IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_EXPOSED_CUT_IRON = registerBlock("waxed_exposed_cut_iron",
        key -> new WaxedCutIronBlock(EXPOSED_CUT_IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_WEATHERED_CUT_IRON = registerBlock("waxed_weathered_cut_iron",
        key -> new WaxedCutIronBlock(WEATHERED_CUT_IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_OXIDIZED_CUT_IRON = registerBlock("waxed_oxidized_cut_iron",
        key -> new WaxedCutIronBlock(OXIDIZED_CUT_IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    // Cut Iron Slabs
    public static final Block CUT_IRON_SLAB = registerBlock(
        "cut_iron_slab",
        key -> new OxidizableCutIronSlabBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block EXPOSED_CUT_IRON_SLAB = registerBlock(
        "exposed_cut_iron_slab",
        key -> new OxidizableCutIronSlabBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_CUT_IRON_SLAB = registerBlock(
        "weathered_cut_iron_slab",
        key -> new OxidizableCutIronSlabBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_CUT_IRON_SLAB = registerBlock(
        "oxidized_cut_iron_slab",
        key -> new OxidizableCutIronSlabBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    // Waxed Cut Iron Slabs
    public static final Block WAXED_CUT_IRON_SLAB = registerBlock("waxed_cut_iron_slab",
        key -> new WaxedCutIronSlabBlock(CUT_IRON_SLAB, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_EXPOSED_CUT_IRON_SLAB = registerBlock("waxed_exposed_cut_iron_slab",
        key -> new WaxedCutIronSlabBlock(EXPOSED_CUT_IRON_SLAB, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_WEATHERED_CUT_IRON_SLAB = registerBlock("waxed_weathered_cut_iron_slab",
        key -> new WaxedCutIronSlabBlock(WEATHERED_CUT_IRON_SLAB, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_OXIDIZED_CUT_IRON_SLAB = registerBlock("waxed_oxidized_cut_iron_slab",
        key -> new WaxedCutIronSlabBlock(OXIDIZED_CUT_IRON_SLAB, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    // Cut Iron Stairs
    public static final Block CUT_IRON_STAIRS = registerBlock(
        "cut_iron_stairs",
        key -> new OxidizableCutIronStairsBlock(
            CUT_IRON.getDefaultState(),
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block EXPOSED_CUT_IRON_STAIRS = registerBlock(
        "exposed_cut_iron_stairs",
        key -> new OxidizableCutIronStairsBlock(
            EXPOSED_CUT_IRON.getDefaultState(),
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block WEATHERED_CUT_IRON_STAIRS = registerBlock(
        "weathered_cut_iron_stairs",
        key -> new OxidizableCutIronStairsBlock(
            WEATHERED_CUT_IRON.getDefaultState(),
            net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .ticksRandomly()
        )
    );

    public static final Block OXIDIZED_CUT_IRON_STAIRS = registerBlock(
        "oxidized_cut_iron_stairs",
        key -> new OxidizableCutIronStairsBlock(
            OXIDIZED_CUT_IRON.getDefaultState(),
            net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    // Waxed Cut Iron Stairs
    public static final Block WAXED_CUT_IRON_STAIRS = registerBlock("waxed_cut_iron_stairs",
        key -> new WaxedCutIronStairsBlock(CUT_IRON_STAIRS, CUT_IRON.getDefaultState(), AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_EXPOSED_CUT_IRON_STAIRS = registerBlock("waxed_exposed_cut_iron_stairs",
        key -> new WaxedCutIronStairsBlock(EXPOSED_CUT_IRON_STAIRS, EXPOSED_CUT_IRON.getDefaultState(), AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_WEATHERED_CUT_IRON_STAIRS = registerBlock("waxed_weathered_cut_iron_stairs",
        key -> new WaxedCutIronStairsBlock(WEATHERED_CUT_IRON_STAIRS, WEATHERED_CUT_IRON.getDefaultState(), AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    public static final Block WAXED_OXIDIZED_CUT_IRON_STAIRS = registerBlock("waxed_oxidized_cut_iron_stairs",
        key -> new WaxedCutIronStairsBlock(OXIDIZED_CUT_IRON_STAIRS, OXIDIZED_CUT_IRON.getDefaultState(), AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool()));

    private static Block registerBlock(String name, java.util.function.Function<RegistryKey<Block>, Block> factory) {
        Identifier id = Identifier.of(OuterWorldMod.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(Registries.BLOCK.getKey(), id);
        Block block = factory.apply(key);
        Registry.register(Registries.BLOCK, id, block);
        RegistryKey<Item> itemKey = RegistryKey.of(Registries.ITEM.getKey(), id);
        Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);
        BlockItem item = new BlockItem(block, itemSettings);
        Registry.register(Registries.ITEM, id, item);
        return block;
    }

    /**
     * Register a block WITHOUT a BlockItem.
     * Used for hidden blocks that players don't interact with directly
     * (e.g., oxidizable clones of vanilla blocks that only appear via replacement).
     */
    private static Block registerBlockOnly(String name, java.util.function.Function<RegistryKey<Block>, Block> factory) {
        Identifier id = Identifier.of(OuterWorldMod.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(Registries.BLOCK.getKey(), id);
        Block block = factory.apply(key);
        Registry.register(Registries.BLOCK, id, block);
        return block;
    }

    public static void registerModBlocks() {
        OuterWorldMod.LOGGER.info("Registering blocks for " + OuterWorldMod.MOD_ID);
        
        // Set waxed versions for onUse honeycomb interaction
        // UNAFFECTED blocks wax to our custom waxed versions (identical look but won't oxidize)
        if (UNAFFECTED_IRON instanceof OxidizableIronBlock b) b.setWaxedVersion(WAXED_IRON);
        if (EXPOSED_IRON instanceof OxidizableIronBlock b) b.setWaxedVersion(WAXED_EXPOSED_IRON);
        if (WEATHERED_IRON instanceof OxidizableIronBlock b) b.setWaxedVersion(WAXED_WEATHERED_IRON);
        if (OXIDIZED_IRON instanceof OxidizableIronBlock b) b.setWaxedVersion(WAXED_OXIDIZED_IRON);
        
        if (IRON_BULB instanceof OxidizableIronBulbBlock b) b.setWaxedVersion(WAXED_IRON_BULB);
        if (EXPOSED_IRON_BULB instanceof OxidizableIronBulbBlock b) b.setWaxedVersion(WAXED_EXPOSED_IRON_BULB);
        if (WEATHERED_IRON_BULB instanceof OxidizableIronBulbBlock b) b.setWaxedVersion(WAXED_WEATHERED_IRON_BULB);
        if (OXIDIZED_IRON_BULB instanceof OxidizableIronBulbBlock b) b.setWaxedVersion(WAXED_OXIDIZED_IRON_BULB);
        
        // UNAFFECTED chain waxes to our custom waxed version
        if (UNAFFECTED_IRON_CHAIN instanceof OxidizableIronChainBlock b) b.setWaxedVersion(WAXED_IRON_CHAIN);
        if (EXPOSED_IRON_CHAIN instanceof OxidizableIronChainBlock b) b.setWaxedVersion(WAXED_EXPOSED_IRON_CHAIN);
        if (WEATHERED_IRON_CHAIN instanceof OxidizableIronChainBlock b) b.setWaxedVersion(WAXED_WEATHERED_IRON_CHAIN);
        if (OXIDIZED_IRON_CHAIN instanceof OxidizableIronChainBlock b) b.setWaxedVersion(WAXED_OXIDIZED_IRON_CHAIN);
        
        // UNAFFECTED door waxes to our custom waxed version
        if (UNAFFECTED_IRON_DOOR instanceof OxidizableIronDoorBlock b) b.setWaxedVersion(WAXED_IRON_DOOR);
        if (EXPOSED_IRON_DOOR instanceof OxidizableIronDoorBlock b) b.setWaxedVersion(WAXED_EXPOSED_IRON_DOOR);
        if (WEATHERED_IRON_DOOR instanceof OxidizableIronDoorBlock b) b.setWaxedVersion(WAXED_WEATHERED_IRON_DOOR);
        if (OXIDIZED_IRON_DOOR instanceof OxidizableIronDoorBlock b) b.setWaxedVersion(WAXED_OXIDIZED_IRON_DOOR);
        
        // UNAFFECTED trapdoor waxes to our custom waxed version
        if (UNAFFECTED_IRON_TRAPDOOR instanceof OxidizableIronTrapdoorBlock b) b.setWaxedVersion(WAXED_IRON_TRAPDOOR);
        if (EXPOSED_IRON_TRAPDOOR instanceof OxidizableIronTrapdoorBlock b) b.setWaxedVersion(WAXED_EXPOSED_IRON_TRAPDOOR);
        if (WEATHERED_IRON_TRAPDOOR instanceof OxidizableIronTrapdoorBlock b) b.setWaxedVersion(WAXED_WEATHERED_IRON_TRAPDOOR);
        if (OXIDIZED_IRON_TRAPDOOR instanceof OxidizableIronTrapdoorBlock b) b.setWaxedVersion(WAXED_OXIDIZED_IRON_TRAPDOOR);
        
        if (IRON_GRATE instanceof OxidizableIronBlock b) b.setWaxedVersion(WAXED_IRON_GRATE);
        if (EXPOSED_IRON_GRATE instanceof OxidizableIronBlock b) b.setWaxedVersion(WAXED_EXPOSED_IRON_GRATE);
        if (WEATHERED_IRON_GRATE instanceof OxidizableIronBlock b) b.setWaxedVersion(WAXED_WEATHERED_IRON_GRATE);
        if (OXIDIZED_IRON_GRATE instanceof OxidizableIronBlock b) b.setWaxedVersion(WAXED_OXIDIZED_IRON_GRATE);
        
        // UNAFFECTED iron bars waxes to our custom waxed version
        if (UNAFFECTED_IRON_BARS instanceof OxidizableIronBarsBlock b) b.setWaxedVersion(WAXED_IRON_BARS);
        if (EXPOSED_IRON_BARS instanceof OxidizableIronBarsBlock b) b.setWaxedVersion(WAXED_EXPOSED_IRON_BARS);
        if (WEATHERED_IRON_BARS instanceof OxidizableIronBarsBlock b) b.setWaxedVersion(WAXED_WEATHERED_IRON_BARS);
        if (OXIDIZED_IRON_BARS instanceof OxidizableIronBarsBlock b) b.setWaxedVersion(WAXED_OXIDIZED_IRON_BARS);
        
        // Iron golem statues waxed versions
        if (IRON_GOLEM_STATUE instanceof IronGolemStatueBlock b) b.setWaxedVersion(WAXED_IRON_GOLEM_STATUE);
        if (EXPOSED_IRON_GOLEM_STATUE instanceof IronGolemStatueBlock b) b.setWaxedVersion(WAXED_EXPOSED_IRON_GOLEM_STATUE);
        if (WEATHERED_IRON_GOLEM_STATUE instanceof IronGolemStatueBlock b) b.setWaxedVersion(WAXED_WEATHERED_IRON_GOLEM_STATUE);
        if (OXIDIZED_IRON_GOLEM_STATUE instanceof IronGolemStatueBlock b) b.setWaxedVersion(WAXED_OXIDIZED_IRON_GOLEM_STATUE);
        
        // Cut iron blocks waxed versions
        if (CUT_IRON instanceof OxidizableCutIronBlock b) b.setWaxedVersion(WAXED_CUT_IRON);
        if (EXPOSED_CUT_IRON instanceof OxidizableCutIronBlock b) b.setWaxedVersion(WAXED_EXPOSED_CUT_IRON);
        if (WEATHERED_CUT_IRON instanceof OxidizableCutIronBlock b) b.setWaxedVersion(WAXED_WEATHERED_CUT_IRON);
        if (OXIDIZED_CUT_IRON instanceof OxidizableCutIronBlock b) b.setWaxedVersion(WAXED_OXIDIZED_CUT_IRON);
        
        // Cut iron slabs waxed versions
        if (CUT_IRON_SLAB instanceof OxidizableCutIronSlabBlock b) b.setWaxedVersion(WAXED_CUT_IRON_SLAB);
        if (EXPOSED_CUT_IRON_SLAB instanceof OxidizableCutIronSlabBlock b) b.setWaxedVersion(WAXED_EXPOSED_CUT_IRON_SLAB);
        if (WEATHERED_CUT_IRON_SLAB instanceof OxidizableCutIronSlabBlock b) b.setWaxedVersion(WAXED_WEATHERED_CUT_IRON_SLAB);
        if (OXIDIZED_CUT_IRON_SLAB instanceof OxidizableCutIronSlabBlock b) b.setWaxedVersion(WAXED_OXIDIZED_CUT_IRON_SLAB);
        
        // Cut iron stairs waxed versions
        if (CUT_IRON_STAIRS instanceof OxidizableCutIronStairsBlock b) b.setWaxedVersion(WAXED_CUT_IRON_STAIRS);
        if (EXPOSED_CUT_IRON_STAIRS instanceof OxidizableCutIronStairsBlock b) b.setWaxedVersion(WAXED_EXPOSED_CUT_IRON_STAIRS);
        if (WEATHERED_CUT_IRON_STAIRS instanceof OxidizableCutIronStairsBlock b) b.setWaxedVersion(WAXED_WEATHERED_CUT_IRON_STAIRS);
        if (OXIDIZED_CUT_IRON_STAIRS instanceof OxidizableCutIronStairsBlock b) b.setWaxedVersion(WAXED_OXIDIZED_CUT_IRON_STAIRS);
        
        // Register oxidation chains with Fabric API
        // This enables Oxidizable.getIncreasedOxidationBlock() and getDecreasedOxidationBlock()
        
        // Iron blocks: unaffected -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(UNAFFECTED_IRON, EXPOSED_IRON);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_IRON, WEATHERED_IRON);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_IRON, OXIDIZED_IRON);
        
        // Iron bulbs: iron_bulb -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(IRON_BULB, EXPOSED_IRON_BULB);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_IRON_BULB, WEATHERED_IRON_BULB);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_IRON_BULB, OXIDIZED_IRON_BULB);
        
        // Iron chains: unaffected -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(UNAFFECTED_IRON_CHAIN, EXPOSED_IRON_CHAIN);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_IRON_CHAIN, WEATHERED_IRON_CHAIN);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_IRON_CHAIN, OXIDIZED_IRON_CHAIN);
        
        // DEBUG: Verify chain oxidation registration (remove after verification)
        OuterWorldMod.LOGGER.info("[ChainDebug] Next for UNAFFECTED_IRON_CHAIN: {}", 
            net.minecraft.block.Oxidizable.getIncreasedOxidationBlock(UNAFFECTED_IRON_CHAIN).orElse(null));
        OuterWorldMod.LOGGER.info("[ChainDebug] Next for EXPOSED_IRON_CHAIN: {}", 
            net.minecraft.block.Oxidizable.getIncreasedOxidationBlock(EXPOSED_IRON_CHAIN).orElse(null));
        OuterWorldMod.LOGGER.info("[ChainDebug] Next for WEATHERED_IRON_CHAIN: {}", 
            net.minecraft.block.Oxidizable.getIncreasedOxidationBlock(WEATHERED_IRON_CHAIN).orElse(null));
        
        // Iron doors: unaffected -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(UNAFFECTED_IRON_DOOR, EXPOSED_IRON_DOOR);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_IRON_DOOR, WEATHERED_IRON_DOOR);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_IRON_DOOR, OXIDIZED_IRON_DOOR);
        
        // Iron trapdoors: unaffected -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(UNAFFECTED_IRON_TRAPDOOR, EXPOSED_IRON_TRAPDOOR);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_IRON_TRAPDOOR, WEATHERED_IRON_TRAPDOOR);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_IRON_TRAPDOOR, OXIDIZED_IRON_TRAPDOOR);
        
        // Iron grates: iron_grate -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(IRON_GRATE, EXPOSED_IRON_GRATE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_IRON_GRATE, WEATHERED_IRON_GRATE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_IRON_GRATE, OXIDIZED_IRON_GRATE);
        
        // Iron bars: unaffected -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(UNAFFECTED_IRON_BARS, EXPOSED_IRON_BARS);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_IRON_BARS, WEATHERED_IRON_BARS);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_IRON_BARS, OXIDIZED_IRON_BARS);
        
        // Iron golem statues: iron -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(IRON_GOLEM_STATUE, EXPOSED_IRON_GOLEM_STATUE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_IRON_GOLEM_STATUE, WEATHERED_IRON_GOLEM_STATUE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_IRON_GOLEM_STATUE, OXIDIZED_IRON_GOLEM_STATUE);
        
        // Cut iron blocks: cut_iron -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(CUT_IRON, EXPOSED_CUT_IRON);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_CUT_IRON, WEATHERED_CUT_IRON);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_CUT_IRON, OXIDIZED_CUT_IRON);
        
        // Cut iron slabs: cut_iron_slab -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(CUT_IRON_SLAB, EXPOSED_CUT_IRON_SLAB);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_CUT_IRON_SLAB, WEATHERED_CUT_IRON_SLAB);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_CUT_IRON_SLAB, OXIDIZED_CUT_IRON_SLAB);
        
        // Cut iron stairs: cut_iron_stairs -> exposed -> weathered -> oxidized
        OxidizableBlocksRegistry.registerOxidizableBlockPair(CUT_IRON_STAIRS, EXPOSED_CUT_IRON_STAIRS);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_CUT_IRON_STAIRS, WEATHERED_CUT_IRON_STAIRS);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_CUT_IRON_STAIRS, OXIDIZED_CUT_IRON_STAIRS);
        
        // Register waxing pairs
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_IRON, WAXED_EXPOSED_IRON);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_IRON, WAXED_WEATHERED_IRON);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_IRON, WAXED_OXIDIZED_IRON);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(IRON_BULB, WAXED_IRON_BULB);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_IRON_BULB, WAXED_EXPOSED_IRON_BULB);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_IRON_BULB, WAXED_WEATHERED_IRON_BULB);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_IRON_BULB, WAXED_OXIDIZED_IRON_BULB);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_IRON_CHAIN, WAXED_EXPOSED_IRON_CHAIN);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_IRON_CHAIN, WAXED_WEATHERED_IRON_CHAIN);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_IRON_CHAIN, WAXED_OXIDIZED_IRON_CHAIN);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_IRON_DOOR, WAXED_EXPOSED_IRON_DOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_IRON_DOOR, WAXED_WEATHERED_IRON_DOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_IRON_DOOR, WAXED_OXIDIZED_IRON_DOOR);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_IRON_TRAPDOOR, WAXED_EXPOSED_IRON_TRAPDOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_IRON_TRAPDOOR, WAXED_WEATHERED_IRON_TRAPDOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_IRON_TRAPDOOR, WAXED_OXIDIZED_IRON_TRAPDOOR);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(IRON_GRATE, WAXED_IRON_GRATE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_IRON_GRATE, WAXED_EXPOSED_IRON_GRATE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_IRON_GRATE, WAXED_WEATHERED_IRON_GRATE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_IRON_GRATE, WAXED_OXIDIZED_IRON_GRATE);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_IRON_BARS, WAXED_EXPOSED_IRON_BARS);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_IRON_BARS, WAXED_WEATHERED_IRON_BARS);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_IRON_BARS, WAXED_OXIDIZED_IRON_BARS);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(IRON_GOLEM_STATUE, WAXED_IRON_GOLEM_STATUE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_IRON_GOLEM_STATUE, WAXED_EXPOSED_IRON_GOLEM_STATUE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_IRON_GOLEM_STATUE, WAXED_WEATHERED_IRON_GOLEM_STATUE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_IRON_GOLEM_STATUE, WAXED_OXIDIZED_IRON_GOLEM_STATUE);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(CUT_IRON, WAXED_CUT_IRON);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_CUT_IRON, WAXED_EXPOSED_CUT_IRON);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_CUT_IRON, WAXED_WEATHERED_CUT_IRON);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_CUT_IRON, WAXED_OXIDIZED_CUT_IRON);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(CUT_IRON_SLAB, WAXED_CUT_IRON_SLAB);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_CUT_IRON_SLAB, WAXED_EXPOSED_CUT_IRON_SLAB);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_CUT_IRON_SLAB, WAXED_WEATHERED_CUT_IRON_SLAB);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_CUT_IRON_SLAB, WAXED_OXIDIZED_CUT_IRON_SLAB);
        
        OxidizableBlocksRegistry.registerWaxableBlockPair(CUT_IRON_STAIRS, WAXED_CUT_IRON_STAIRS);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_CUT_IRON_STAIRS, WAXED_EXPOSED_CUT_IRON_STAIRS);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_CUT_IRON_STAIRS, WAXED_WEATHERED_CUT_IRON_STAIRS);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_CUT_IRON_STAIRS, WAXED_OXIDIZED_CUT_IRON_STAIRS);
        
        // Register waxable pairs for vanilla blocks -> waxed versions
        // This allows applying honeycomb to vanilla blocks to get waxed versions
        OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.IRON_BLOCK, WAXED_IRON);
        OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.IRON_DOOR, WAXED_IRON_DOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.IRON_TRAPDOOR, WAXED_IRON_TRAPDOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.IRON_BARS, WAXED_IRON_BARS);
        // Register waxable pair for vanilla chain using registry lookup
        Block vanillaChain = Registries.BLOCK.get(Identifier.ofVanilla("chain"));
        OxidizableBlocksRegistry.registerWaxableBlockPair(vanillaChain, WAXED_IRON_CHAIN);
        
        OuterWorldMod.LOGGER.info("Registered oxidizable iron blocks with Fabric API");
    }
}
