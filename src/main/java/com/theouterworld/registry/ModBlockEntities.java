package com.theouterworld.registry;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.block.QuantumPadBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<QuantumPadBlockEntity> QUANTUM_PAD = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(OuterWorldMod.MOD_ID, "quantum_pad"),
        net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.<QuantumPadBlockEntity>create(QuantumPadBlockEntity::new, com.theouterworld.block.ModBlocks.QUANTUM_PAD).build()
    );

    public static void registerModBlockEntities() {
        OuterWorldMod.LOGGER.info("Registering block entities for " + OuterWorldMod.MOD_ID);
    }
}
