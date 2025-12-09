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

	public static final Item BROKEN_EMERGENCY_RETURN_PAD = registerItem(
		"broken_emergency_return_pad",
		key -> new BrokenEmergencyReturnPadItem(
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item OXIDIZED_BASALT_ROCK = registerItem(
		"oxidized_basalt_rock",
		key -> new Item(
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item OXIDIZED_BASALT_PICK = registerItem(
		"oxidized_basalt_pick",
		key -> new OxidizedBasaltPickItem(
			key,
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item OXIDIZED_BASALT_SWORD = registerItem(
		"oxidized_basalt_sword",
		key -> new OxidizedBasaltSwordItem(
			key,
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item OXIDIZED_BASALT_AXE = registerItem(
		"oxidized_basalt_axe",
		key -> new OxidizedBasaltAxeItem(
			key,
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item OXIDIZED_BASALT_PICKAXE = registerItem(
		"oxidized_basalt_pickaxe",
		key -> new OxidizedBasaltPickaxeItem(
			key,
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item OXIDIZED_BASALT_SHOVEL = registerItem(
		"oxidized_basalt_shovel",
		key -> new OxidizedBasaltShovelItem(
			key,
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item OXIDIZED_BASALT_HOE = registerItem(
		"oxidized_basalt_hoe",
		key -> new OxidizedBasaltHoeItem(
			key,
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item RAW_OXIDIZED_IRON = registerItem(
		"raw_oxidized_iron",
		key -> new Item(
			new Item.Settings()
				.registryKey(key)
		)
	);

	public static final Item RUST_SPLINT = registerItem(
		"rust_splint",
		key -> new Item(
			new Item.Settings()
				.registryKey(key)
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


