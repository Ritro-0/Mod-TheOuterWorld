package com.theouterworld;

import com.theouterworld.block.ModBlocks;
import com.theouterworld.item.ModItemGroups;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateMod implements ModInitializer {
	public static final String MOD_ID = "theouterworld";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world from TheOuterWorld!");
		
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
	}
}

