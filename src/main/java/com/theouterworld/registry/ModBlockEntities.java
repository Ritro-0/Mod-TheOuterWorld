package com.theouterworld.registry;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.block.IronGolemStatueBlockEntity;
import com.theouterworld.block.ModBlocks;
import com.theouterworld.block.ProcessorBlockEntity;
import com.theouterworld.block.QuantumPadBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<QuantumPadBlockEntity> QUANTUM_PAD = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(OuterWorldMod.MOD_ID, "quantum_pad"),
        net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.<QuantumPadBlockEntity>create(QuantumPadBlockEntity::new, ModBlocks.QUANTUM_PAD).build()
    );

    public static final BlockEntityType<IronGolemStatueBlockEntity> IRON_GOLEM_STATUE = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(OuterWorldMod.MOD_ID, "iron_golem_statue"),
        net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.<IronGolemStatueBlockEntity>create(
            IronGolemStatueBlockEntity::new,
            ModBlocks.IRON_GOLEM_STATUE,
            ModBlocks.EXPOSED_IRON_GOLEM_STATUE,
            ModBlocks.WEATHERED_IRON_GOLEM_STATUE,
            ModBlocks.OXIDIZED_IRON_GOLEM_STATUE,
            ModBlocks.WAXED_IRON_GOLEM_STATUE,
            ModBlocks.WAXED_EXPOSED_IRON_GOLEM_STATUE,
            ModBlocks.WAXED_WEATHERED_IRON_GOLEM_STATUE,
            ModBlocks.WAXED_OXIDIZED_IRON_GOLEM_STATUE
        ).build()
    );

    public static final BlockEntityType<ProcessorBlockEntity> PROCESSOR = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(OuterWorldMod.MOD_ID, "processor"),
        net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.<ProcessorBlockEntity>create(
            ProcessorBlockEntity::new,
            ModBlocks.PROCESSOR
        ).build()
    );

    public static void registerModBlockEntities() {
        OuterWorldMod.LOGGER.info("Registering block entities for " + OuterWorldMod.MOD_ID);
    }
}
