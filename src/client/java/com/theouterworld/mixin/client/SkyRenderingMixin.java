package com.theouterworld.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;

import com.theouterworld.TemplateMod;
import com.theouterworld.registry.ModDimensions;

import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.state.SkyRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@Mixin(SkyRendering.class)
public abstract class SkyRenderingMixin {

    @Shadow
    @Nullable
    private AbstractTexture sunTexture;

    @Shadow
    @Nullable
    private AbstractTexture moonPhasesTexture;

    @Shadow
    protected abstract AbstractTexture bindTexture(Identifier identifier);

    @Unique
    private static final Identifier THEOUTERWORLD_SUN_TEXTURE = Identifier.of(
        TemplateMod.MOD_ID,
        "textures/environment/sun.png"
    );

    @Unique
    private static final Identifier THEOUTERWORLD_MOON_TEXTURE = Identifier.of(
        TemplateMod.MOD_ID,
        "textures/environment/moon_phases.png"
    );

    @Unique
    @Nullable
    private AbstractTexture theouterworld$vanillaSunTexture;

    @Unique
    @Nullable
    private AbstractTexture theouterworld$vanillaMoonTexture;

    @Unique
    @Nullable
    private AbstractTexture theouterworld$outerworldSunTexture;

    @Unique
    @Nullable
    private AbstractTexture theouterworld$outerworldMoonTexture;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void theouterworld$cacheCelestialTextures(CallbackInfo ci) {
        this.theouterworld$vanillaSunTexture = this.sunTexture;
        this.theouterworld$vanillaMoonTexture = this.moonPhasesTexture;
        this.theouterworld$outerworldSunTexture = this.bindTexture(THEOUTERWORLD_SUN_TEXTURE);
        this.theouterworld$outerworldMoonTexture = this.bindTexture(THEOUTERWORLD_MOON_TEXTURE);
    }

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void theouterworld$swapCelestialTextures(
        ClientWorld world,
        float tickDelta,
        Vec3d cameraPos,
        SkyRenderState state,
        CallbackInfo ci
    ) {
        if (world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
            if (this.theouterworld$outerworldSunTexture != null) {
                this.sunTexture = this.theouterworld$outerworldSunTexture;
            }

            if (this.theouterworld$outerworldMoonTexture != null) {
                this.moonPhasesTexture = this.theouterworld$outerworldMoonTexture;
            }
        } else {
            this.sunTexture = this.theouterworld$vanillaSunTexture;
            this.moonPhasesTexture = this.theouterworld$vanillaMoonTexture;
        }
    }
}

