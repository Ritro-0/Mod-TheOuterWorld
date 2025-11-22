package com.theouterworld.config;

/**
 * Simple configuration class for The Outerworld mod.
 * Hardcoded gravity multiplier for testing - Mars gravity ratio: 0.377
 * (Mars gravity: 3.71 m/s² / Earth 9.81 m/s²)
 */
public class OuterworldConfig {
    
    /**
     * Gravity multiplier applied in Outerworld dimension.
     * Default: 0.377 (Mars gravity ratio)
     */
    public static final double GRAVITY_MULTIPLIER = 0.377;
    
    // Martian moon orbital periods (in Minecraft ticks, 1 day = 24000 ticks)
    /** Phobos: orbits every ~7.65 minutes = 7650 ticks (realistic) */
    public static final double PHOBOS_ORBITAL_PERIOD_TICKS = 7650.0;
    
    /** Deimos: orbits every ~30.25 hours = 725000 ticks (realistic) */
    public static final double DEIMOS_ORBITAL_PERIOD_TICKS = 725000.0;
    
    // Moon size multipliers (relative to vanilla moon)
    /** Phobos: appears 2.5-3x larger than vanilla moon */
    public static final double PHOBOS_SIZE_MULTIPLIER = 2.75;
    
    /** Deimos: appears 1/4 to 1/5 the size of vanilla moon */
    public static final double DEIMOS_SIZE_MULTIPLIER = 0.225;
    
    // Maximum elevation angles (in degrees)
    /** Phobos: reaches up to 60-70° elevation */
    public static final float PHOBOS_MAX_ELEVATION = 65.0f;
    
    /** Deimos: never gets very high, max ~15-20° */
    public static final float DEIMOS_MAX_ELEVATION = 17.5f;
    
    private static final OuterworldConfig INSTANCE = new OuterworldConfig();
    
    public double gravityMultiplier = GRAVITY_MULTIPLIER;
    public double phobosOrbitalPeriod = PHOBOS_ORBITAL_PERIOD_TICKS;
    public double deimosOrbitalPeriod = DEIMOS_ORBITAL_PERIOD_TICKS;
    public double phobosSizeMultiplier = PHOBOS_SIZE_MULTIPLIER;
    public double deimosSizeMultiplier = DEIMOS_SIZE_MULTIPLIER;
    public float phobosMaxElevation = PHOBOS_MAX_ELEVATION;
    public float deimosMaxElevation = DEIMOS_MAX_ELEVATION;
    
    private OuterworldConfig() {
        // Private constructor for singleton
    }
    
    /**
     * Register and initialize the config.
     * No-op for simplified config.
     */
    public static void register() {
        // No registration needed for hardcoded config
    }
    
    /**
     * Get the config instance.
     * @return The OuterworldConfig instance
     */
    public static OuterworldConfig get() {
        return INSTANCE;
    }
}
