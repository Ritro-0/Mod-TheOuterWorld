package com.theouterworld.registry;

import com.theouterworld.OuterWorldMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ModDimensions {
    public static final RegistryKey<World> OUTER_WORLD_WORLD_KEY = RegistryKey.of(
        RegistryKeys.WORLD,
        Identifier.of(OuterWorldMod.MOD_ID, "outer_world")
    );

    public static final RegistryKey<DimensionType> OUTER_WORLD_DIMENSION_TYPE = RegistryKey.of(
        RegistryKeys.DIMENSION_TYPE,
        OUTER_WORLD_WORLD_KEY.getValue()
    );

    public static void registerModDimensions() {
        OuterWorldMod.LOGGER.info("Registering dimensions for " + OuterWorldMod.MOD_ID);
        // Dimensions are registered via JSON files in 1.21.10
    }
}
