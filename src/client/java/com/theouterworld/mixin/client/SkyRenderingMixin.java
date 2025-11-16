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

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.state.SkyRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
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
    private static final Vec3d THEOUTERWORLD_DAY_SKY = new Vec3d(0.82D, 0.34D, 0.21D);

    @Unique
    private static final Vec3d THEOUTERWORLD_NIGHT_SKY = new Vec3d(0.05D, 0.05D, 0.10D);

    @Unique
    private static final Vec3d THEOUTERWORLD_SUNRISE = new Vec3d(0.94D, 0.48D, 0.26D);

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

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void theouterworld$updateOuterWorldSky(
        ClientWorld world,
        float tickDelta,
        Vec3d cameraPos,
        SkyRenderState state,
        CallbackInfo ci
    ) {
        boolean inOuterWorld = world.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY);
        if (inOuterWorld) {
            if (this.theouterworld$outerworldSunTexture != null) {
                this.sunTexture = this.theouterworld$outerworldSunTexture;
            }

            if (this.theouterworld$outerworldMoonTexture != null) {
                this.moonPhasesTexture = this.theouterworld$outerworldMoonTexture;
            }

            this.theouterworld$tintMartianSky(state);
        } else {
            this.sunTexture = this.theouterworld$vanillaSunTexture;
            this.moonPhasesTexture = this.theouterworld$vanillaMoonTexture;
        }
    }

    @Unique
    private void theouterworld$tintMartianSky(SkyRenderState state) {
        if (state.skyType == DimensionEffects.SkyType.NONE) {
            return;
        }

        float solarCycle = (MathHelper.cos(state.time * (float) (Math.PI * 2)) + 1.0F) * 0.5F;
        Vec3d martianColor = THEOUTERWORLD_NIGHT_SKY.lerp(THEOUTERWORLD_DAY_SKY, solarCycle);
        state.skyColor = ColorHelper.fromFloats(
            1.0F,
            theouterworld$channel(martianColor.x),
            theouterworld$channel(martianColor.y),
            theouterworld$channel(martianColor.z)
        );

        float cosine = MathHelper.cos(state.time * (float) (Math.PI * 2));
        boolean isSunriseOrSunset = cosine >= -0.4F && cosine <= 0.4F;
        state.isSunTransition = isSunriseOrSunset;

        if (isSunriseOrSunset) {
            state.sunriseAndSunsetColor = ColorHelper.fromFloats(
                1.0F,
                theouterworld$channel(THEOUTERWORLD_SUNRISE.x),
                theouterworld$channel(THEOUTERWORLD_SUNRISE.y),
                theouterworld$channel(THEOUTERWORLD_SUNRISE.z)
            );
        }
    }

    @Unique
    private static float theouterworld$channel(double value) {
        return (float)MathHelper.clamp(value, 0.0D, 1.0D);
    }
}

