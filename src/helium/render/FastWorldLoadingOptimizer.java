/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.render;

import com.helium.HeliumClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class FastWorldLoadingOptimizer {
    private static volatile boolean initialized = false;
    private static volatile boolean enabled = false;
    private static volatile long loadStartTime = 0L;
    private static volatile long loadEndTime = 0L;

    private FastWorldLoadingOptimizer() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        HeliumClient.LOGGER.info("[helium] fast world loading optimizer initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled && initialized;
    }

    public static void onWorldLoadStart() {
        if (!enabled) {
            return;
        }
        loadStartTime = System.currentTimeMillis();
        HeliumClient.LOGGER.info("[helium] world load started");
    }

    public static void onWorldLoadEnd() {
        if (!enabled || loadStartTime == 0L) {
            return;
        }
        loadEndTime = System.currentTimeMillis();
        long elapsed = loadEndTime - loadStartTime;
        HeliumClient.LOGGER.info("[helium] world load completed in {}ms", (Object)elapsed);
        loadStartTime = 0L;
    }

    public static int getReducedSpawnRadius() {
        return 2;
    }

    public static boolean shouldSkipExtraSpawnChunks(int chunkX, int chunkZ, int spawnX, int spawnZ) {
        if (!enabled) {
            return false;
        }
        int dx = Math.abs(chunkX - spawnX);
        int dz = Math.abs(chunkZ - spawnZ);
        int radius = FastWorldLoadingOptimizer.getReducedSpawnRadius();
        return dx > radius || dz > radius;
    }

    public static long getLastLoadTimeMs() {
        return loadEndTime - loadStartTime;
    }
}

