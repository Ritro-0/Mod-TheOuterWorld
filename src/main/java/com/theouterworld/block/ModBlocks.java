package com.theouterworld.block;

import com.theouterworld.OuterWorldMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
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
				.dropsNothing()
        )
    );

    public static final Block REGOLITH = registerBlock(
        "regolith",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(0.5f, 0.5f) // Same hardness as sand
                .sounds(BlockSoundGroup.SAND)
        )
    );

    public static final Block OXIDIZED_BASALT = registerBlock(
        "oxidized_basalt",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .mapColor(MapColor.GRAY)
                .strength(1.25f, 4.2f) // Same as basalt
                .sounds(BlockSoundGroup.BASALT)
                .requiresTool() // Vanilla-like timing (~6.25s by hand)
        )
    );

    // Oxidizable Iron blocks - oxidize only in Outerworld dimension
    // Unwaxed blocks (must be registered in order: base -> exposed -> weathered -> oxidized)
    public static final Block EXPOSED_IRON = registerBlock(
        "exposed_iron",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            null, // Will be set after WEATHERED_IRON is registered
            null, // Will be set after IRON_BLOCK is registered
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
            null, // Will be set after OXIDIZED_IRON is registered
            null, // Will be set after EXPOSED_IRON is registered
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
            null, // No next stage
            null, // Will be set after WEATHERED_IRON is registered
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        )
    );

    // Waxed blocks
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

    // Anorthosite - clone of deepslate
    public static final Block ANORTHOSITE = registerBlock(
        "anorthosite",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f, 6.0f) // Same as deepslate
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    // Anorthosite ores - clones of deepslate ores
    public static final Block ANORTHOSITE_COPPER_ORE = registerBlock(
        "anorthosite_copper_ore",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(4.5f, 3.0f) // Same as deepslate copper ore
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    public static final Block ANORTHOSITE_GOLD_ORE = registerBlock(
        "anorthosite_gold_ore",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(4.5f, 3.0f) // Same as deepslate gold ore
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    public static final Block ANORTHOSITE_OXIDIZED_IRON_ORE = registerBlock(
        "anorthosite_oxidized_iron_ore",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(4.5f, 3.0f) // Same as deepslate iron ore
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    public static final Block ANORTHOSITE_REDSTONE_ORE = registerBlock(
        "anorthosite_redstone_ore",
        key -> new Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(4.5f, 3.0f) // Same as deepslate redstone ore
                .sounds(BlockSoundGroup.DEEPSLATE)
                .requiresTool()
        )
    );

    // Iron Bulb blocks - oxidize only in Outerworld dimension
    // Light levels: unoxidized=15, exposed=12, weathered=8, oxidized=4 (like copper bulbs)
    public static final Block IRON_BULB = registerBlock(
        "iron_bulb",
        key -> new OxidizableIronBulbBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            null,
            null,
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
            null,
            null,
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
            null,
            null,
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
            null,
            null,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f, 3.0f)
                .sounds(BlockSoundGroup.COPPER_BULB)
                .requiresTool()
                .luminance(state -> state.get(OxidizableIronBulbBlock.LIT) ? 4 : 0)
        )
    );

    public static final Block WAXED_IRON_BULB = registerBlock(
        "waxed_iron_bulb",
        key -> new WaxedIronBulbBlock(IRON_BULB, AbstractBlock.Settings.create().registryKey(key).strength(3.0f, 3.0f).sounds(BlockSoundGroup.COPPER_BULB).requiresTool().luminance(state -> state.get(WaxedIronBulbBlock.LIT) ? 15 : 0))
    );

    public static final Block WAXED_EXPOSED_IRON_BULB = registerBlock(
        "waxed_exposed_iron_bulb",
        key -> new WaxedIronBulbBlock(EXPOSED_IRON_BULB, AbstractBlock.Settings.create().registryKey(key).strength(3.0f, 3.0f).sounds(BlockSoundGroup.COPPER_BULB).requiresTool().luminance(state -> state.get(WaxedIronBulbBlock.LIT) ? 12 : 0))
    );

    public static final Block WAXED_WEATHERED_IRON_BULB = registerBlock(
        "waxed_weathered_iron_bulb",
        key -> new WaxedIronBulbBlock(WEATHERED_IRON_BULB, AbstractBlock.Settings.create().registryKey(key).strength(3.0f, 3.0f).sounds(BlockSoundGroup.COPPER_BULB).requiresTool().luminance(state -> state.get(WaxedIronBulbBlock.LIT) ? 8 : 0))
    );

    public static final Block WAXED_OXIDIZED_IRON_BULB = registerBlock(
        "waxed_oxidized_iron_bulb",
        key -> new WaxedIronBulbBlock(OXIDIZED_IRON_BULB, AbstractBlock.Settings.create().registryKey(key).strength(3.0f, 3.0f).sounds(BlockSoundGroup.COPPER_BULB).requiresTool().luminance(state -> state.get(WaxedIronBulbBlock.LIT) ? 4 : 0))
    );

    // Iron Chain blocks - only oxidized variants (base exists in vanilla)
    public static final Block EXPOSED_IRON_CHAIN = registerBlock(
        "exposed_iron_chain",
        key -> new OxidizableIronChainBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            null,
            null,
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
            null,
            null,
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
            null,
            null,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.CHAIN)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_EXPOSED_IRON_CHAIN = registerBlock(
        "waxed_exposed_iron_chain",
        key -> new WaxedIronChainBlock(EXPOSED_IRON_CHAIN, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.CHAIN).requiresTool().nonOpaque())
    );

    public static final Block WAXED_WEATHERED_IRON_CHAIN = registerBlock(
        "waxed_weathered_iron_chain",
        key -> new WaxedIronChainBlock(WEATHERED_IRON_CHAIN, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.CHAIN).requiresTool().nonOpaque())
    );

    public static final Block WAXED_OXIDIZED_IRON_CHAIN = registerBlock(
        "waxed_oxidized_iron_chain",
        key -> new WaxedIronChainBlock(OXIDIZED_IRON_CHAIN, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.CHAIN).requiresTool().nonOpaque())
    );

    // Iron Door blocks - only oxidized variants (base exists in vanilla)
    public static final Block EXPOSED_IRON_DOOR = registerBlock(
        "exposed_iron_door",
        key -> new OxidizableIronDoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            null,
            null,
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
            null,
            null,
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
            null,
            null,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_EXPOSED_IRON_DOOR = registerBlock(
        "waxed_exposed_iron_door",
        key -> new WaxedIronDoorBlock(EXPOSED_IRON_DOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    public static final Block WAXED_WEATHERED_IRON_DOOR = registerBlock(
        "waxed_weathered_iron_door",
        key -> new WaxedIronDoorBlock(WEATHERED_IRON_DOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    public static final Block WAXED_OXIDIZED_IRON_DOOR = registerBlock(
        "waxed_oxidized_iron_door",
        key -> new WaxedIronDoorBlock(OXIDIZED_IRON_DOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    // Iron Trapdoor blocks - only oxidized variants (base exists in vanilla)
    public static final Block EXPOSED_IRON_TRAPDOOR = registerBlock(
        "exposed_iron_trapdoor",
        key -> new OxidizableIronTrapdoorBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED,
            null,
            null,
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
            null,
            null,
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
            null,
            null,
            net.minecraft.block.BlockSetType.IRON,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_EXPOSED_IRON_TRAPDOOR = registerBlock(
        "waxed_exposed_iron_trapdoor",
        key -> new WaxedIronTrapdoorBlock(EXPOSED_IRON_TRAPDOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    public static final Block WAXED_WEATHERED_IRON_TRAPDOOR = registerBlock(
        "waxed_weathered_iron_trapdoor",
        key -> new WaxedIronTrapdoorBlock(WEATHERED_IRON_TRAPDOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    public static final Block WAXED_OXIDIZED_IRON_TRAPDOOR = registerBlock(
        "waxed_oxidized_iron_trapdoor",
        key -> new WaxedIronTrapdoorBlock(OXIDIZED_IRON_TRAPDOOR, net.minecraft.block.BlockSetType.IRON, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    // Iron Grate blocks - oxidize only in Outerworld dimension
    public static final Block IRON_GRATE = registerBlock(
        "iron_grate",
        key -> new OxidizableIronBlock(
            net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED,
            null,
            null,
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
            null,
            null,
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
            null,
            null,
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
            null,
            null,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(5.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .nonOpaque()
        )
    );

    public static final Block WAXED_IRON_GRATE = registerBlock(
        "waxed_iron_grate",
        key -> new WaxedIronBlock(IRON_GRATE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    public static final Block WAXED_EXPOSED_IRON_GRATE = registerBlock(
        "waxed_exposed_iron_grate",
        key -> new WaxedIronBlock(EXPOSED_IRON_GRATE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    public static final Block WAXED_WEATHERED_IRON_GRATE = registerBlock(
        "waxed_weathered_iron_grate",
        key -> new WaxedIronBlock(WEATHERED_IRON_GRATE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    public static final Block WAXED_OXIDIZED_IRON_GRATE = registerBlock(
        "waxed_oxidized_iron_grate",
        key -> new WaxedIronBlock(OXIDIZED_IRON_GRATE, AbstractBlock.Settings.create().registryKey(key).strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool().nonOpaque())
    );

    private static Block registerBlock(String name, java.util.function.Function<RegistryKey<Block>, Block> factory) {
        Identifier id = Identifier.of(OuterWorldMod.MOD_ID, name);
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
        OuterWorldMod.LOGGER.info("Registering blocks for " + OuterWorldMod.MOD_ID);
        
        // Link oxidizable iron blocks after all are registered
        if (EXPOSED_IRON instanceof OxidizableIronBlock exposedIron) {
            exposedIron.setNextOxidizedBlock(WEATHERED_IRON);
            exposedIron.setPreviousOxidizedBlock(null); // No base iron block
            exposedIron.setWaxedVersion(WAXED_EXPOSED_IRON);
        }
        if (WEATHERED_IRON instanceof OxidizableIronBlock weatheredIron) {
            weatheredIron.setNextOxidizedBlock(OXIDIZED_IRON);
            weatheredIron.setPreviousOxidizedBlock(EXPOSED_IRON);
            weatheredIron.setWaxedVersion(WAXED_WEATHERED_IRON);
        }
        if (OXIDIZED_IRON instanceof OxidizableIronBlock oxidizedIron) {
            oxidizedIron.setPreviousOxidizedBlock(WEATHERED_IRON);
            oxidizedIron.setWaxedVersion(WAXED_OXIDIZED_IRON);
        }
        
        // Link iron bulb blocks
        if (IRON_BULB instanceof OxidizableIronBulbBlock ironBulb) {
            ironBulb.setNextOxidizedBlock(EXPOSED_IRON_BULB);
            ironBulb.setWaxedVersion(WAXED_IRON_BULB);
        }
        if (EXPOSED_IRON_BULB instanceof OxidizableIronBulbBlock exposedBulb) {
            exposedBulb.setNextOxidizedBlock(WEATHERED_IRON_BULB);
            exposedBulb.setPreviousOxidizedBlock(IRON_BULB);
            exposedBulb.setWaxedVersion(WAXED_EXPOSED_IRON_BULB);
        }
        if (WEATHERED_IRON_BULB instanceof OxidizableIronBulbBlock weatheredBulb) {
            weatheredBulb.setNextOxidizedBlock(OXIDIZED_IRON_BULB);
            weatheredBulb.setPreviousOxidizedBlock(EXPOSED_IRON_BULB);
            weatheredBulb.setWaxedVersion(WAXED_WEATHERED_IRON_BULB);
        }
        if (OXIDIZED_IRON_BULB instanceof OxidizableIronBulbBlock oxidizedBulb) {
            oxidizedBulb.setPreviousOxidizedBlock(WEATHERED_IRON_BULB);
            oxidizedBulb.setWaxedVersion(WAXED_OXIDIZED_IRON_BULB);
        }
        
        // Link iron chain blocks
        if (EXPOSED_IRON_CHAIN instanceof OxidizableIronChainBlock exposedChain) {
            exposedChain.setNextOxidizedBlock(WEATHERED_IRON_CHAIN);
            exposedChain.setWaxedVersion(WAXED_EXPOSED_IRON_CHAIN);
        }
        if (WEATHERED_IRON_CHAIN instanceof OxidizableIronChainBlock weatheredChain) {
            weatheredChain.setNextOxidizedBlock(OXIDIZED_IRON_CHAIN);
            weatheredChain.setPreviousOxidizedBlock(EXPOSED_IRON_CHAIN);
            weatheredChain.setWaxedVersion(WAXED_WEATHERED_IRON_CHAIN);
        }
        if (OXIDIZED_IRON_CHAIN instanceof OxidizableIronChainBlock oxidizedChain) {
            oxidizedChain.setPreviousOxidizedBlock(WEATHERED_IRON_CHAIN);
            oxidizedChain.setWaxedVersion(WAXED_OXIDIZED_IRON_CHAIN);
        }
        
        // Link iron door blocks
        if (EXPOSED_IRON_DOOR instanceof OxidizableIronDoorBlock exposedDoor) {
            exposedDoor.setNextOxidizedBlock(WEATHERED_IRON_DOOR);
            exposedDoor.setWaxedVersion(WAXED_EXPOSED_IRON_DOOR);
        }
        if (WEATHERED_IRON_DOOR instanceof OxidizableIronDoorBlock weatheredDoor) {
            weatheredDoor.setNextOxidizedBlock(OXIDIZED_IRON_DOOR);
            weatheredDoor.setPreviousOxidizedBlock(EXPOSED_IRON_DOOR);
            weatheredDoor.setWaxedVersion(WAXED_WEATHERED_IRON_DOOR);
        }
        if (OXIDIZED_IRON_DOOR instanceof OxidizableIronDoorBlock oxidizedDoor) {
            oxidizedDoor.setPreviousOxidizedBlock(WEATHERED_IRON_DOOR);
            oxidizedDoor.setWaxedVersion(WAXED_OXIDIZED_IRON_DOOR);
        }
        
        // Link iron trapdoor blocks
        if (EXPOSED_IRON_TRAPDOOR instanceof OxidizableIronTrapdoorBlock exposedTrapdoor) {
            exposedTrapdoor.setNextOxidizedBlock(WEATHERED_IRON_TRAPDOOR);
            exposedTrapdoor.setWaxedVersion(WAXED_EXPOSED_IRON_TRAPDOOR);
        }
        if (WEATHERED_IRON_TRAPDOOR instanceof OxidizableIronTrapdoorBlock weatheredTrapdoor) {
            weatheredTrapdoor.setNextOxidizedBlock(OXIDIZED_IRON_TRAPDOOR);
            weatheredTrapdoor.setPreviousOxidizedBlock(EXPOSED_IRON_TRAPDOOR);
            weatheredTrapdoor.setWaxedVersion(WAXED_WEATHERED_IRON_TRAPDOOR);
        }
        if (OXIDIZED_IRON_TRAPDOOR instanceof OxidizableIronTrapdoorBlock oxidizedTrapdoor) {
            oxidizedTrapdoor.setPreviousOxidizedBlock(WEATHERED_IRON_TRAPDOOR);
            oxidizedTrapdoor.setWaxedVersion(WAXED_OXIDIZED_IRON_TRAPDOOR);
        }
        
        // Link iron grate blocks
        if (IRON_GRATE instanceof OxidizableIronBlock ironGrate) {
            ironGrate.setNextOxidizedBlock(EXPOSED_IRON_GRATE);
            ironGrate.setWaxedVersion(WAXED_IRON_GRATE);
        }
        if (EXPOSED_IRON_GRATE instanceof OxidizableIronBlock exposedGrate) {
            exposedGrate.setNextOxidizedBlock(WEATHERED_IRON_GRATE);
            exposedGrate.setPreviousOxidizedBlock(IRON_GRATE);
            exposedGrate.setWaxedVersion(WAXED_EXPOSED_IRON_GRATE);
        }
        if (WEATHERED_IRON_GRATE instanceof OxidizableIronBlock weatheredGrate) {
            weatheredGrate.setNextOxidizedBlock(OXIDIZED_IRON_GRATE);
            weatheredGrate.setPreviousOxidizedBlock(EXPOSED_IRON_GRATE);
            weatheredGrate.setWaxedVersion(WAXED_WEATHERED_IRON_GRATE);
        }
        if (OXIDIZED_IRON_GRATE instanceof OxidizableIronBlock oxidizedGrate) {
            oxidizedGrate.setPreviousOxidizedBlock(WEATHERED_IRON_GRATE);
            oxidizedGrate.setWaxedVersion(WAXED_OXIDIZED_IRON_GRATE);
        }
        
        // Register oxidation and waxing pairs with Fabric API (if available)
        try {
            // Try to use Fabric API's OxidizableBlocksRegistry if it exists
            Class<?> registryClass = Class.forName("net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry");
            java.lang.reflect.Method registerOxidizable = registryClass.getMethod("registerOxidizableBlockPair", Block.class, Block.class);
            java.lang.reflect.Method registerWaxable = registryClass.getMethod("registerWaxableBlockPair", Block.class, Block.class);
            
            // Register oxidation pairs
            registerOxidizable.invoke(null, EXPOSED_IRON, WEATHERED_IRON);
            registerOxidizable.invoke(null, WEATHERED_IRON, OXIDIZED_IRON);
            
            // Register waxing pairs
            registerWaxable.invoke(null, EXPOSED_IRON, WAXED_EXPOSED_IRON);
            registerWaxable.invoke(null, WEATHERED_IRON, WAXED_WEATHERED_IRON);
            registerWaxable.invoke(null, OXIDIZED_IRON, WAXED_OXIDIZED_IRON);
            
            OuterWorldMod.LOGGER.info("Registered oxidizable iron blocks with Fabric API");
        } catch (Exception e) {
            // Fabric API registry not available, blocks will still work via interfaces
            OuterWorldMod.LOGGER.debug("Fabric API OxidizableBlocksRegistry not available, using interface-based system");
        }
    }
}

