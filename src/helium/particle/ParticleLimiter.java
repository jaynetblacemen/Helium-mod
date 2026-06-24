/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_703
 */
package com.helium.particle;

import com.helium.HeliumClient;
import com.helium.particle.ParticlePriority;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_703;

@Environment(value=EnvType.CLIENT)
public final class ParticleLimiter {
    private static final AtomicInteger currentParticleCount = new AtomicInteger(0);
    private static volatile int maxParticles = 1000;
    private static volatile boolean initialized = false;

    private ParticleLimiter() {
    }

    public static void init(int max) {
        maxParticles = max;
        initialized = true;
        HeliumClient.LOGGER.info("particle limiter initialized with max {}", (Object)max);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean canAddParticle(class_703 particle) {
        if (!initialized) {
            return true;
        }
        int count = currentParticleCount.get();
        int priority = ParticlePriority.getPriority(particle);
        return ParticlePriority.shouldKeep(priority, count, maxParticles);
    }

    public static void onParticleAdded() {
        currentParticleCount.incrementAndGet();
    }

    public static void onParticleRemoved() {
        currentParticleCount.updateAndGet(c -> Math.max(0, c - 1));
    }

    public static void setParticleCount(int count) {
        currentParticleCount.set(count);
    }

    public static int getCurrentCount() {
        return currentParticleCount.get();
    }

    public static int getMaxParticles() {
        return maxParticles;
    }

    public static void setMaxParticles(int max) {
        maxParticles = max;
    }
}

