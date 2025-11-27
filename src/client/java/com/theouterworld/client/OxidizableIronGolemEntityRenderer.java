package com.theouterworld.client;

import com.theouterworld.OuterWorldMod;
import com.theouterworld.entity.OxidizableIronGolemEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * Renderer for the oxidizable iron golem.
 * Selects texture based on oxidation level.
 */
public class OxidizableIronGolemEntityRenderer extends IronGolemEntityRenderer {
    
    private static final Identifier IRON_GOLEM_TEXTURE = Identifier.ofVanilla("textures/entity/iron_golem/iron_golem.png");
    private static final Identifier EXPOSED_IRON_GOLEM_TEXTURE = Identifier.of(OuterWorldMod.MOD_ID, "textures/entity/iron_golem/exposed_iron_golem.png");
    private static final Identifier WEATHERED_IRON_GOLEM_TEXTURE = Identifier.of(OuterWorldMod.MOD_ID, "textures/entity/iron_golem/weathered_iron_golem.png");
    private static final Identifier OXIDIZED_IRON_GOLEM_TEXTURE = Identifier.of(OuterWorldMod.MOD_ID, "textures/entity/iron_golem/oxidized_iron_golem.png");

    public OxidizableIronGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        // Explicitly add crack feature renderer (may not be inherited properly)
        this.addFeature(new IronGolemCrackFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(IronGolemEntityRenderState state) {
        // The render state doesn't have our custom data, so we need to get it differently
        // We'll use a custom render state that includes oxidation level
        if (state instanceof OxidizableIronGolemRenderState oxidizableState) {
            return switch (oxidizableState.oxidationLevel) {
                case 1 -> EXPOSED_IRON_GOLEM_TEXTURE;
                case 2 -> WEATHERED_IRON_GOLEM_TEXTURE;
                case 3 -> OXIDIZED_IRON_GOLEM_TEXTURE;
                default -> IRON_GOLEM_TEXTURE;
            };
        }
        return IRON_GOLEM_TEXTURE;
    }

    @Override
    public IronGolemEntityRenderState createRenderState() {
        return new OxidizableIronGolemRenderState();
    }

    @Override
    public void updateRenderState(net.minecraft.entity.passive.IronGolemEntity entity, IronGolemEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        
        if (state instanceof OxidizableIronGolemRenderState oxidizableState && entity instanceof OxidizableIronGolemEntity oxidizableGolem) {
            oxidizableState.oxidationLevel = oxidizableGolem.getOxidationLevel();
            oxidizableState.waxed = oxidizableGolem.isWaxed();
        }
    }

    /**
     * Custom render state that includes oxidation data
     */
    public static class OxidizableIronGolemRenderState extends IronGolemEntityRenderState {
        public int oxidationLevel = 0;
        public boolean waxed = false;
    }
}

