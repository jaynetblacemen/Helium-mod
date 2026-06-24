/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_703
 */
package com.helium.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_703;

@Environment(value=EnvType.CLIENT)
public final class ParticlePriority {
    public static final int PRIORITY_CRITICAL = 100;
    public static final int PRIORITY_HIGH = 75;
    public static final int PRIORITY_MEDIUM = 50;
    public static final int PRIORITY_LOW = 25;
    public static final int PRIORITY_AMBIENT = 10;

    private ParticlePriority() {
    }

    public static int getPriority(class_703 particle) {
        if (particle == null) {
            return 25;
        }
        String className = particle.getClass().getSimpleName().toLowerCase();
        if (className.contains("explosion") || className.contains("damage") || className.contains("crit")) {
            return 100;
        }
        if (className.contains("enchant") || className.contains("portal") || className.contains("flame")) {
            return 75;
        }
        if (className.contains("block") || className.contains("item") || className.contains("dust")) {
            return 50;
        }
        if (className.contains("bubble") || className.contains("splash") || className.contains("drip")) {
            return 25;
        }
        if (className.contains("ambient") || className.contains("ash") || className.contains("spore")) {
            return 10;
        }
        return 50;
    }

    public static boolean shouldKeep(int priority, int currentCount, int maxCount) {
        if ((double)currentCount < (double)maxCount * 0.5) {
            return true;
        }
        if (priority >= 100) {
            return true;
        }
        if ((double)currentCount < (double)maxCount * 0.75) {
            return priority >= 75;
        }
        if ((double)currentCount < (double)maxCount * 0.9) {
            return priority >= 50;
        }
        return priority >= 100;
    }
}

