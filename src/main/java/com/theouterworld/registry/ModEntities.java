package com.theouterworld.registry;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.entity.OxidizableIronGolemEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    
    public static final EntityType<OxidizableIronGolemEntity> OXIDIZABLE_IRON_GOLEM = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(OuterWorldMod.MOD_ID, "oxidizable_iron_golem"),
        EntityType.Builder.create(OxidizableIronGolemEntity::new, SpawnGroup.MISC)
            .dimensions(1.4f, 2.7f) // Same as iron golem
            .maxTrackingRange(10)
            .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(OuterWorldMod.MOD_ID, "oxidizable_iron_golem")))
    );

    public static void registerModEntities() {
        OuterWorldMod.LOGGER.info("Registering entities for " + OuterWorldMod.MOD_ID);
        
        // Register entity attributes
        FabricDefaultAttributeRegistry.register(OXIDIZABLE_IRON_GOLEM, 
            OxidizableIronGolemEntity.createOxidizableIronGolemAttributes());
        
        // TODO: Register copper golem entity
        // - Create OxidizableCopperGolemEntity class similar to OxidizableIronGolemEntity
        // - Should oxidize over time in Outerworld dimension
        // - Can be waxed with honeycomb to prevent oxidation
        // - Can be scraped with axe to de-oxidize
        
        // TODO: Register lunar warden entity
        // - Create LunarWardenEntity class (boss entity)
        // - Should have unique fight mechanics
        // - Should spawn in basalt cathedral structure
        
        // TODO: Register book authors' soul entity
        // - Create BookAuthorsSoulEntity class
        // - Should be released from lunar warden upon defeat
        // - Should join player in combat as an ally
        // - Should have unique AI to assist player
    }
}

