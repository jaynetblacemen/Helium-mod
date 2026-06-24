/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.overlay;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class FpsStats {
    private volatile int currentFps = 0;
    private volatile int displayFps = 0;
    private volatile int minFps = Integer.MAX_VALUE;
    private volatile int maxFps = 0;
    private volatile long fpsSum = 0L;
    private volatile int fpsCount = 0;
    private volatile long lastResetTime = System.currentTimeMillis();
    private volatile long lastDisplayUpdateTime = System.currentTimeMillis();
    private static final long RESET_INTERVAL_MS = 500L;
    private static final long DISPLAY_UPDATE_INTERVAL_MS = 500L;

    public void updateFps(int fps) {
        this.currentFps = fps;
        long now = System.currentTimeMillis();
        if (now - this.lastDisplayUpdateTime >= 500L) {
            this.displayFps = fps;
            this.lastDisplayUpdateTime = now;
        }
        if (fps > 0) {
            if (fps < this.minFps) {
                this.minFps = fps;
            }
            if (fps > this.maxFps) {
                this.maxFps = fps;
            }
            this.fpsSum += (long)fps;
            ++this.fpsCount;
        }
        if (now - this.lastResetTime >= 500L) {
            this.minFps = fps;
            this.maxFps = fps;
            this.fpsSum = fps;
            this.fpsCount = 1;
            this.lastResetTime = now;
        }
    }

    public int getCurrentFps() {
        return this.displayFps;
    }

    public int getMinFps() {
        return this.minFps == Integer.MAX_VALUE ? 0 : this.minFps;
    }

    public int getMaxFps() {
        return this.maxFps;
    }

    public int getAvgFps() {
        return this.fpsCount > 0 ? (int)(this.fpsSum / (long)this.fpsCount) : 0;
    }

    public void reset() {
        this.currentFps = 0;
        this.minFps = Integer.MAX_VALUE;
        this.maxFps = 0;
        this.fpsSum = 0L;
        this.fpsCount = 0;
        this.lastResetTime = System.currentTimeMillis();
    }
}

