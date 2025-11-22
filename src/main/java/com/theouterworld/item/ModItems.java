package com.theouterworld.item;

import com.theouterworld.OuterWorldMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {
	public static final Item EMERGENCY_RETURN_PAD = registerItem(
		"emergency_return_pad",
		key -> new EmergencyReturnPadItem(
			new Item.Settings()
				.registryKey(key)
				.maxCount(1) // one-time use item
		)
	);

	private static Item registerItem(String name, java.util.function.Function<RegistryKey<Item>, Item> factory) {
		Identifier id = Identifier.of(OuterWorldMod.MOD_ID, name);
		RegistryKey<Item> key = RegistryKey.of(Registries.ITEM.getKey(), id);
		Item item = factory.apply(key);
		return Registry.register(Registries.ITEM, id, item);
	}

	public static void registerModItems() {
		OuterWorldMod.LOGGER.info("Registering items for " + OuterWorldMod.MOD_ID);
	}
}


