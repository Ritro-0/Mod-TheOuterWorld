package com.theouterworld;

import net.fabricmc.api.ClientModInitializer;

public class TemplateModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TemplateMod.LOGGER.info("Hello Fabric world from TheOuterWorld Client!");
	}
}

