/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWVidMode
 */
package com.helium.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.gpu.GpuDetector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

@Environment(value=EnvType.CLIENT)
public final class DisplaySyncOptimizer {
    private static volatile long lastDisplayUpdateTime = 0L;
    private static volatile long updateIntervalNs = 0L;
    private static volatile int cachedRefreshRate = 0;
    private static volatile int appliedConfigRate = 0;

    private DisplaySyncOptimizer() {
    }

    public static boolean shouldPerformDisplayUpdate() {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || config.displaySyncRefreshRate == 0) {
            return true;
        }
        int configRate = config.displaySyncRefreshRate;
        long interval = updateIntervalNs;
        int applied = appliedConfigRate;
        if (interval == 0L || applied != configRate) {
            if (configRate == -1) {
                DisplaySyncOptimizer.detectRefreshRate();
            } else {
                cachedRefreshRate = configRate;
                updateIntervalNs = 1000000000L / (long)(configRate + 30);
            }
            appliedConfigRate = configRate;
            interval = updateIntervalNs;
        }
        long now = System.nanoTime();
        long lastUpdate = lastDisplayUpdateTime;
        long elapsed = now - lastUpdate;
        if (GpuDetector.isIntegratedOnly()) {
            return DisplaySyncOptimizer.handleIntegratedGpu(now, elapsed, interval);
        }
        if (elapsed >= interval) {
            lastDisplayUpdateTime = now;
            return true;
        }
        return false;
    }

    private static boolean handleIntegratedGpu(long now, long elapsed, long interval) {
        if (elapsed >= interval) {
            lastDisplayUpdateTime = now;
            return true;
        }
        long remaining = interval - elapsed;
        if (remaining > 1000000L && remaining < 8000000L) {
            try {
                Thread.sleep(0L, (int)Math.min(remaining, 999999L));
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            lastDisplayUpdateTime = System.nanoTime();
            return true;
        }
        return true;
    }

    private static void detectRefreshRate() {
        try {
            GLFWVidMode vidMode;
            long monitor = 0L;
            class_310 client = class_310.method_1551();
            if (client != null && client.method_22683() != null) {
                monitor = GLFW.glfwGetWindowMonitor((long)client.method_22683().method_4490());
            }
            if (monitor == 0L) {
                monitor = GLFW.glfwGetPrimaryMonitor();
            }
            if (monitor != 0L && (vidMode = GLFW.glfwGetVideoMode((long)monitor)) != null && vidMode.refreshRate() > 0) {
                cachedRefreshRate = vidMode.refreshRate();
                updateIntervalNs = 1000000000L / (long)(cachedRefreshRate + 30);
                return;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        cachedRefreshRate = 60;
        updateIntervalNs = 11111111L;
    }

    public static void reset() {
        lastDisplayUpdateTime = 0L;
        updateIntervalNs = 0L;
        cachedRefreshRate = 0;
        appliedConfigRate = 0;
    }

    public static int getDetectedRefreshRate() {
        return cachedRefreshRate;
    }
}

