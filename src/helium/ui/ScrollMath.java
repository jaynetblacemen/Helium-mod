/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.ui;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ScrollMath {
    private static final double DEFAULT_SCROLL_SPEED = 0.5;
    private static final double DEFAULT_SCROLLBAR_DRAG = 0.025;
    private static final double DEFAULT_ANIMATION_DURATION = 1.0;
    private static final double DEFAULT_PUSHBACK_STRENGTH = 1.0;

    private ScrollMath() {
    }

    public static double getScrollSpeed() {
        return 0.5;
    }

    public static double getScrollbarDrag() {
        return 0.025;
    }

    public static double getAnimationDuration() {
        return 1.0;
    }

    public static double getPushBackStrength() {
        return 1.0;
    }

    public static double scrollbarVelocity(double timer, double factor) {
        return Math.pow(1.0 - ScrollMath.getScrollbarDrag(), timer) * factor;
    }

    public static int dampenSquish(double squish, int height) {
        double proportion = Math.min(1.0, squish / 100.0);
        return (int)(Math.min(0.85, proportion) * (double)height);
    }

    public static double pushBackStrength(double distance, float delta) {
        return (distance + 4.0) * (double)delta / 0.3 / (3.2 / ScrollMath.getPushBackStrength());
    }

    public static boolean isEnabled() {
        HeliumConfig config = HeliumClient.getConfig();
        return config != null && config.modEnabled && config.smoothScrolling;
    }
}

