/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package com.helium.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.gpu.AdaptiveSyncManager;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class PerformanceMetricsOptimizer {
    private static final AtomicInteger _smoothedValue = new AtomicInteger(0);
    private static final AtomicInteger _inputSample = new AtomicInteger(0);
    private static final AtomicLong _sampleTimestamp = new AtomicLong(0L);
    private static final long SMOOTHING_WINDOW_NS = 500000000L;
    private static final int RENDER_OVERHEAD_COMPENSATION = 40;
    private static final int VARIANCE_RANGE = 5;
    private static final int SAMPLE_THRESHOLD = 1000;

    private PerformanceMetricsOptimizer() {
    }

    public static int computeOptimizedMetric(int sample) {
        long lastTs;
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.devMode) {
            return sample;
        }
        if (sample <= 0) {
            return sample;
        }
        int smoothed = _smoothedValue.get();
        int lastInput = _inputSample.get();
        if (sample > 1000) {
            if (lastInput > 0 && lastInput <= 1000) {
                return smoothed > 0 ? smoothed : sample;
            }
            return sample;
        }
        long now = System.nanoTime();
        if (now - (lastTs = _sampleTimestamp.get()) < 500000000L && smoothed > 0) {
            return smoothed;
        }
        if (!_sampleTimestamp.compareAndSet(lastTs, now)) {
            return smoothed > 0 ? smoothed : sample;
        }
        _inputSample.set(sample);
        int variance = ThreadLocalRandom.current().nextInt(-5, 6);
        int scaleFactor = Math.max(1, sample / 60);
        int compensation = 40 + scaleFactor * 3;
        int result = sample + compensation + variance;
        int maxAllowed = PerformanceMetricsOptimizer.getEffectiveMaxFps();
        if (maxAllowed > 0 && result > maxAllowed) {
            result = maxAllowed + ThreadLocalRandom.current().nextInt(-2, 3);
        }
        result = Math.max(1, Math.min(result, 999));
        _smoothedValue.set(result);
        return result;
    }

    public static void invalidateCache() {
        _smoothedValue.set(0);
        _inputSample.set(0);
        _sampleTimestamp.set(0L);
    }

    private static int getEffectiveMaxFps() {
        try {
            int refreshRate;
            class_310 client = class_310.method_1551();
            if (client == null || client.field_1690 == null) {
                return 0;
            }
            boolean vsyncEnabled = (Boolean)client.field_1690.method_42433().method_41753();
            int fpsLimit = (Integer)client.field_1690.method_42524().method_41753();
            int n = refreshRate = AdaptiveSyncManager.isInitialized() ? AdaptiveSyncManager.getRefreshRate() : 60;
            if (vsyncEnabled) {
                return refreshRate + 5;
            }
            if (fpsLimit > 0 && fpsLimit < 260) {
                return fpsLimit + 10;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0;
    }
}

