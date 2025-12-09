package com.theouterworld.client;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.screen.KnappingTableScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KnappingTableScreen extends HandledScreen<KnappingTableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(OuterWorldMod.MOD_ID, "textures/gui/container/knapping_table.png");

    public KnappingTableScreen(KnappingTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(
            net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            x, y,
            0.0f, 0.0f,
            this.backgroundWidth, this.backgroundHeight,
            256, 256,
            0xFFFFFFFF
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}

