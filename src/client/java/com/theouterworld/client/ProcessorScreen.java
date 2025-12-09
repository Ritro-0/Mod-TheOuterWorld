package com.theouterworld.client;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.network.ProcessorModeTogglePacket;
import com.theouterworld.screen.ProcessorScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ProcessorScreen extends HandledScreen<ProcessorScreenHandler> implements ScreenHandlerProvider<ProcessorScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(OuterWorldMod.MOD_ID, "textures/gui/container/processor.png");
    
    // Progress bar dimensions (similar to brewing stand bubbles/arrow)
    private static final int PROGRESS_BAR_X = 97;
    private static final int PROGRESS_BAR_Y = 16;
    private static final int PROGRESS_BAR_WIDTH = 9;
    private static final int PROGRESS_BAR_HEIGHT = 28;
    
    // Heat bar dimensions (similar to blaze powder meter)
    private static final int HEAT_BAR_X = 17;
    private static final int HEAT_BAR_Y = 34;
    private static final int HEAT_BAR_WIDTH = 16;
    private static final int HEAT_BAR_HEIGHT = 4;
    
    // Mode toggle button
    private ButtonWidget modeButton;

    public ProcessorScreen(ProcessorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 166;
        this.backgroundWidth = 176;
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        
        // Add mode toggle button (below secondary input slot)
        int buttonX = this.x + 7;
        int buttonY = this.y + 59;
        modeButton = ButtonWidget.builder(getModeButtonText(), button -> {
            // Get BlockPos from handler (works on server) or from player's target block
            net.minecraft.util.math.BlockPos pos = handler.getBlockPos();
            OuterWorldMod.LOGGER.info("[Processor Client] Button clicked, handler BlockPos: {}", pos);
            // If handler doesn't have the pos (client side), try to get it from player's target
            if (pos.equals(net.minecraft.util.math.BlockPos.ORIGIN) && this.client != null && this.client.player != null) {
                // Try crosshair target first
                var hitResult = this.client.crosshairTarget;
                if (hitResult instanceof net.minecraft.util.hit.BlockHitResult blockHit) {
                    pos = blockHit.getBlockPos();
                    OuterWorldMod.LOGGER.info("[Processor Client] Got BlockPos from crosshair: {}", pos);
                } else {
                    // Fallback: look for processor block near player (within 5 blocks)
                    var playerPos = this.client.player.getBlockPos();
                    var world = this.client.player.getEntityWorld();
                    for (int x = -5; x <= 5; x++) {
                        for (int y = -5; y <= 5; y++) {
                            for (int z = -5; z <= 5; z++) {
                                var checkPos = playerPos.add(x, y, z);
                                if (world.getBlockEntity(checkPos) instanceof com.theouterworld.block.ProcessorBlockEntity) {
                                    pos = checkPos;
                                    OuterWorldMod.LOGGER.info("[Processor Client] Found BlockPos near player: {}", pos);
                                    break;
                                }
                            }
                            if (!pos.equals(net.minecraft.util.math.BlockPos.ORIGIN)) break;
                        }
                        if (!pos.equals(net.minecraft.util.math.BlockPos.ORIGIN)) break;
                    }
                }
            }
            // Send packet to toggle mode on server
            if (!pos.equals(net.minecraft.util.math.BlockPos.ORIGIN)) {
                OuterWorldMod.LOGGER.info("[Processor Client] Sending toggle packet with BlockPos: {}", pos);
                ClientPlayNetworking.send(new ProcessorModeTogglePacket(pos));
            } else {
                OuterWorldMod.LOGGER.warn("[Processor Client] Could not determine BlockPos, not sending packet");
            }
        }).dimensions(buttonX, buttonY, 50, 16).build();
        this.addDrawableChild(modeButton);
    }

    private Text getModeButtonText() {
        return handler.isHeatMode() ? Text.literal("Heat") : Text.literal("Process");
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        
        // Draw main background (176x166 texture)
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            x, y,
            0.0f, 0.0f,
            backgroundWidth, backgroundHeight,
            256, 256,
            0xFFFFFFFF
        );
        
        // Draw progress bar as a filled rectangle (green, fills downward)
        float progress = handler.getProgressScaled();
        if (progress > 0) {
            int progressHeight = (int) (PROGRESS_BAR_HEIGHT * progress);
            int barX = x + PROGRESS_BAR_X;
            int barY = y + PROGRESS_BAR_Y + (PROGRESS_BAR_HEIGHT - progressHeight);
            // Green color: 0xFF00AA00
            context.fill(barX, barY, barX + PROGRESS_BAR_WIDTH, barY + progressHeight, 0xFF00AA00);
        }
        
        // Draw heat bar as a filled rectangle (orange, fills upward) - only in heat mode
        if (handler.isHeatMode()) {
            float heat = handler.getHeatScaled();
            if (heat > 0) {
                int heatHeight = (int) (20 * heat); // 20 pixel tall heat bar
                int barX = x + HEAT_BAR_X;
                int barY = y + HEAT_BAR_Y + (20 - heatHeight);
                // Orange color: 0xFFFF6600
                context.fill(barX, barY, barX + HEAT_BAR_WIDTH, barY + heatHeight, 0xFFFF6600);
            }
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
        
        // Draw mode indicator
        String modeText = handler.isHeatMode() ? "Heat Mode" : "Process Mode";
        context.drawText(this.textRenderer, modeText, 7, 64, 4210752, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        
        // Update button text
        if (modeButton != null) {
            modeButton.setMessage(getModeButtonText());
        }
    }
}

