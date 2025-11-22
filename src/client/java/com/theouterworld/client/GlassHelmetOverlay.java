package com.theouterworld.client;

import com.theouterworld.util.GlassHelmetUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class GlassHelmetOverlay {
	private GlassHelmetOverlay() {}

	public static void register() {
		HudRenderCallback.EVENT.register(GlassHelmetOverlay::renderOverlay);
	}

	private static void renderOverlay(DrawContext drawContext, net.minecraft.client.render.RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) return;

		ItemStack head = client.player.getEquippedStack(EquipmentSlot.HEAD);
		if (!GlassHelmetUtil.isGlassHelmetItem(head)) return;

		Identifier overlayTexture = GlassHelmetUtil.getGlassTextureId(head);
		if (overlayTexture == null) return;

		int width = client.getWindow().getScaledWidth();
		int height = client.getWindow().getScaledHeight();

		// Mild opacity to avoid being too intrusive
		int alpha = 90; // 0-255
		int color = (alpha << 24) | 0xFFFFFF;

		// Draw the glass block texture stretched over the screen.
		drawContext.drawTexture(
			net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED,
			overlayTexture,
			0, 0,
			0.0f, 0.0f,
			width, height,
			width, height,
			color
		);
	}
}


