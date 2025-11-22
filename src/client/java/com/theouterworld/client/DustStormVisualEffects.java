package com.theouterworld.client;

import com.theouterworld.OuterWorldClient;
import com.theouterworld.registry.ModDimensions;
import com.theouterworld.util.GlassHelmetUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Client-side visual effects for dust storms:
 * - FOV zoom effect (like powdered snow)
 * - Overlay rendering (like powdered snow overlay)
 */
public class DustStormVisualEffects {
    private static final Identifier DUSTSTORM_OVERLAY = 
        Identifier.of("theouterworld", "textures/misc/duststorm_overlay.png");
    
    // Toggle: Set to true to use texture overlay, false to use just color fill
    private static final boolean USE_TEXTURE_OVERLAY = true;
    
    // Client-side exposure tracking (simulated, server has authoritative version)
    private static int clientExposureTicks = 0;
    private static boolean wasInStorm = false;
    
    public static void register() {
        // FOV modification - exact same formula as powdered snow
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                clientExposureTicks = 0;
                wasInStorm = false;
                return;
            }
            
            // Mirror the same check as DustStormHandler - only in Outer World and when storm is active
            if (!client.world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY) || 
                !OuterWorldClient.isDustStormActive) {
                clientExposureTicks = 0;
                wasInStorm = false;
                if (client.options.getFovEffectScale().getValue() != 1.0) {
                    client.options.getFovEffectScale().setValue(1.0);
                }
                return;
            }
            
            boolean isInStorm = true; // We already checked isDustStormActive above
            
            // Track exposure client-side (simplified version, server is authoritative for effects)
            if (isInStorm) {
                if (!wasInStorm) {
                    // Just entered storm
                    clientExposureTicks = 0;
                }
                if (clientExposureTicks < 120) {
                    clientExposureTicks++;
                }
            } else {
                // Exiting storm - decay exposure
                if (wasInStorm) {
                    clientExposureTicks = -30; // Grace period
                }
                if (clientExposureTicks > -30) {
                    clientExposureTicks--;
                } else if (clientExposureTicks < 0) {
                    clientExposureTicks = 0;
                }
            }
            wasInStorm = isInStorm;
            
            // Calculate intensity - use render tick counter for smooth interpolation
            RenderTickCounter tickCounter = client.getRenderTickCounter();
            float tickDelta = tickCounter.getTickProgress(false);
            float intensity = MathHelper.clamp((clientExposureTicks + tickDelta) / 120f, 0f, 1f);
            
            if (intensity > 0f) {
                // Exact same formula Minecraft uses for powdered snow
                // intensity^2 * 0.4 means at full intensity (1.0), FOV multiplier is 0.6 (40% reduction)
                float fovMultiplier = 1.0f - (intensity * intensity * 0.4f);
                client.options.getFovEffectScale().setValue((double) fovMultiplier);
            } else {
                // Reset FOV when intensity is 0
                client.options.getFovEffectScale().setValue(1.0);
            }
        });
        
        // Simple overlay - just show a colored tint when dust storm particles are active
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;
            
            // Same check as DustStormHandler - if particles are spawning, show overlay
            if (!client.world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY) || 
                !OuterWorldClient.isDustStormActive) return;
            
            // Calculate intensity for fade in/out
            float tickDeltaFloat = tickDelta.getTickProgress(false);
            float intensity = MathHelper.clamp((clientExposureTicks + tickDeltaFloat) / 120f, 0f, 1f);
            if (intensity <= 0f) return;
            
			// If player is wearing a glass helmet, skip the orange tint + texture overlay
			if (GlassHelmetUtil.isGlassHelmetItem(client.player.getEquippedStack(EquipmentSlot.HEAD))) {
				return;
			}
			
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();
            
            // Layer 1: Color fill (brown/orange tint) - blends underneath
            // Reduced max alpha for more transparency (120/255 = ~47% max opacity)
            int tintAlpha = (int) (intensity * 120f); // 0-120 for reduced opacity (adjust 120 to change transparency)
            int tintColor = (tintAlpha << 24) | 0xD2691E; // Brown/orange tint (RGB: 210, 105, 30)
            drawContext.fill(0, 0, width, height, tintColor);
            
            // Layer 2: Texture overlay on top (dust scratches/edges effect)
            // Use RenderPipelines.GUI_TEXTURED for GUI textures/overlays
            // Reduced max alpha for more transparency (150/255 = ~59% max opacity)
            int textureAlpha = (int) (intensity * 150f); // 0-150 for reduced opacity (adjust 150 to change transparency)
            int overlayColor = (textureAlpha << 24) | 0xFFFFFF; // White tint + alpha (ARGB format)
            
            drawContext.drawTexture(
                RenderPipelines.GUI_TEXTURED,  // RenderPipeline for GUI textures
                DUSTSTORM_OVERLAY,            // Texture identifier
                0, 0,                         // Screen position (top-left)
                0.0f, 0.0f,                   // Texture UV start (top-left of texture)
                width, height,                // Draw size (full screen - stretches + tiles)
                256, 256,                    // Texture size (your PNG dimensions)
                overlayColor                 // ARGB color: alpha + white tint
            );
        });
    }
}

