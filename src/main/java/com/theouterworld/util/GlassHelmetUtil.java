package com.theouterworld.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public final class GlassHelmetUtil {
	private GlassHelmetUtil() {}

	public static boolean isGlassHelmetItem(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		return stack.isOf(Items.GLASS)
			|| stack.isOf(Items.TINTED_GLASS)
			|| stack.isOf(Items.WHITE_STAINED_GLASS)
			|| stack.isOf(Items.ORANGE_STAINED_GLASS)
			|| stack.isOf(Items.MAGENTA_STAINED_GLASS)
			|| stack.isOf(Items.LIGHT_BLUE_STAINED_GLASS)
			|| stack.isOf(Items.YELLOW_STAINED_GLASS)
			|| stack.isOf(Items.LIME_STAINED_GLASS)
			|| stack.isOf(Items.PINK_STAINED_GLASS)
			|| stack.isOf(Items.GRAY_STAINED_GLASS)
			|| stack.isOf(Items.LIGHT_GRAY_STAINED_GLASS)
			|| stack.isOf(Items.CYAN_STAINED_GLASS)
			|| stack.isOf(Items.PURPLE_STAINED_GLASS)
			|| stack.isOf(Items.BLUE_STAINED_GLASS)
			|| stack.isOf(Items.BROWN_STAINED_GLASS)
			|| stack.isOf(Items.GREEN_STAINED_GLASS)
			|| stack.isOf(Items.RED_STAINED_GLASS)
			|| stack.isOf(Items.BLACK_STAINED_GLASS);
	}

	public static Identifier getGlassTextureId(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return null;
		}
		if (stack.isOf(Items.GLASS)) {
			return Identifier.of("minecraft", "textures/block/glass.png");
		}
		if (stack.isOf(Items.TINTED_GLASS)) {
			return Identifier.of("minecraft", "textures/block/tinted_glass.png");
		}
		if (stack.isOf(Items.WHITE_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/white_stained_glass.png");
		if (stack.isOf(Items.ORANGE_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/orange_stained_glass.png");
		if (stack.isOf(Items.MAGENTA_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/magenta_stained_glass.png");
		if (stack.isOf(Items.LIGHT_BLUE_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/light_blue_stained_glass.png");
		if (stack.isOf(Items.YELLOW_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/yellow_stained_glass.png");
		if (stack.isOf(Items.LIME_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/lime_stained_glass.png");
		if (stack.isOf(Items.PINK_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/pink_stained_glass.png");
		if (stack.isOf(Items.GRAY_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/gray_stained_glass.png");
		if (stack.isOf(Items.LIGHT_GRAY_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/light_gray_stained_glass.png");
		if (stack.isOf(Items.CYAN_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/cyan_stained_glass.png");
		if (stack.isOf(Items.PURPLE_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/purple_stained_glass.png");
		if (stack.isOf(Items.BLUE_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/blue_stained_glass.png");
		if (stack.isOf(Items.BROWN_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/brown_stained_glass.png");
		if (stack.isOf(Items.GREEN_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/green_stained_glass.png");
		if (stack.isOf(Items.RED_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/red_stained_glass.png");
		if (stack.isOf(Items.BLACK_STAINED_GLASS)) return Identifier.of("minecraft", "textures/block/black_stained_glass.png");
		return null;
	}
}


