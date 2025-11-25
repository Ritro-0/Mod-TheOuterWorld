package com.theouterworld.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;

public class OxidizedBasaltPickItem extends Item {
    public OxidizedBasaltPickItem(RegistryKey<Item> registryKey, Item.Settings settings) {
        super(settings.maxDamage(ModToolMaterials.OXIDIZED_BASALT_DURABILITY));
    }
    
    // The item is tagged as a pickaxe in pickaxes.json
    // Mining speed is set to 1.0f (same as hands) via OxidizedBasaltPickMixin
    // This makes it a proper pickaxe that can harvest the block but adds no speed boost
}
