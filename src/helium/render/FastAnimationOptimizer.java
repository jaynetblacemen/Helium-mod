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
public final class FastAnimationOptimizer {
    private static volatile boolean initialized = false;

    private FastAnimationOptimizer() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        HeliumClient.LOGGER.info("[helium] fast animation optimizer initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }
}

