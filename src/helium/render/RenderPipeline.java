/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.render;

import com.helium.HeliumClient;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class RenderPipeline {
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicLong frameCount = new AtomicLong(0L);
    private static final AtomicLong lastFrameTime = new AtomicLong(0L);
    private static final AtomicLong frameBudgetNs = new AtomicLong(16666667L);
    private static volatile long[] frameTimes = new long[60];
    private static volatile int frameIndex = 0;
    private static volatile double smoothedFrameTime = 16.67;
    private static volatile boolean adaptivePacing = true;

    private RenderPipeline() {
    }

    public static void init() {
        if (initialized.getAndSet(true)) {
            return;
        }
        lastFrameTime.set(System.nanoTime());
        HeliumClient.LOGGER.info("render pipeline initialized (frame pacing mode)");
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static void onFrameStart() {
        long last;
        if (!initialized.get()) {
            return;
        }
        long now = System.nanoTime();
        long delta = now - (last = lastFrameTime.getAndSet(now));
        if (delta > 0L && delta < 1000000000L) {
            RenderPipeline.frameTimes[RenderPipeline.frameIndex] = delta;
            frameIndex = (frameIndex + 1) % frameTimes.length;
            long sum = 0L;
            int count = 0;
            for (long t : frameTimes) {
                if (t <= 0L) continue;
                sum += t;
                ++count;
            }
            if (count > 0) {
                smoothedFrameTime = (double)sum / (double)count / 1000000.0;
            }
        }
        frameCount.incrementAndGet();
    }

    public static void onFrameEnd() {
        long elapsed;
        if (!initialized.get() || !adaptivePacing) {
            return;
        }
        long budget = frameBudgetNs.get();
        long remaining = budget - (elapsed = System.nanoTime() - lastFrameTime.get());
        if (remaining > 500000L && remaining < 8000000L) {
            try {
                Thread.sleep(0L, (int)Math.min(remaining / 2L, 999999L));
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void setTargetFps(int fps) {
        if (fps > 0 && fps <= 1000) {
            frameBudgetNs.set(1000000000L / (long)fps);
        }
    }

    public static void setAdaptivePacing(boolean enabled) {
        adaptivePacing = enabled;
    }

    public static double getSmoothedFrameTimeMs() {
        return smoothedFrameTime;
    }

    public static double getSmoothedFps() {
        return smoothedFrameTime > 0.0 ? 1000.0 / smoothedFrameTime : 0.0;
    }

    public static long getFrameCount() {
        return frameCount.get();
    }

    public static void shutdown() {
        initialized.set(false);
        frameCount.set(0L);
        lastFrameTime.set(0L);
        frameTimes = new long[60];
        frameIndex = 0;
        smoothedFrameTime = 16.67;
        HeliumClient.LOGGER.info("render pipeline shutdown");
    }
}

