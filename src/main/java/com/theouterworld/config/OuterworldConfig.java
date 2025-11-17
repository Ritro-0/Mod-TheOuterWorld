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
    
    private static final OuterworldConfig INSTANCE = new OuterworldConfig();
    
    public double gravityMultiplier = GRAVITY_MULTIPLIER;
    
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
