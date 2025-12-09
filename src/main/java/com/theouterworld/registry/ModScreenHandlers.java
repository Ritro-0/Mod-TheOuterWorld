package com.theouterworld.registry;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.screen.KnappingTableScreenHandler;
import com.theouterworld.screen.ProcessorScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ProcessorScreenHandler> PROCESSOR_SCREEN_HANDLER = Registry.register(
        Registries.SCREEN_HANDLER,
        Identifier.of(OuterWorldMod.MOD_ID, "processor"),
        new ScreenHandlerType<>(ProcessorScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );

    public static final ScreenHandlerType<KnappingTableScreenHandler> KNAPPING_TABLE_SCREEN_HANDLER = Registry.register(
        Registries.SCREEN_HANDLER,
        Identifier.of(OuterWorldMod.MOD_ID, "knapping_table"),
        new ScreenHandlerType<>(KnappingTableScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );

    public static void registerModScreenHandlers() {
        OuterWorldMod.LOGGER.info("Registering screen handlers for " + OuterWorldMod.MOD_ID);
    }
}

