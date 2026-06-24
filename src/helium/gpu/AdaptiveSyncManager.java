/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWVidMode
 */
package com.helium.gpu;

import com.helium.HeliumClient;
import com.helium.gpu.GpuDetector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

@Environment(value=EnvType.CLIENT)
public final class AdaptiveSyncManager {
    private static volatile boolean initialized = false;
    private static volatile boolean adaptiveSyncDetected = false;
    private static volatile int refreshRate = 60;

    private AdaptiveSyncManager() {
    }

    public static void init(long windowHandle) {
        if (initialized) {
            return;
        }
        initialized = true;
        try {
            GLFWVidMode vidMode;
            long monitor = GLFW.glfwGetPrimaryMonitor();
            if (monitor != 0L && (vidMode = GLFW.glfwGetVideoMode((long)monitor)) != null) {
                refreshRate = vidMode.refreshRate();
            }
            if (adaptiveSyncDetected = AdaptiveSyncManager.detectAdaptiveSync()) {
                HeliumClient.LOGGER.info("adaptive sync detected ({}hz refresh)", (Object)refreshRate);
            } else {
                HeliumClient.LOGGER.info("no adaptive sync detected ({}hz refresh)", (Object)refreshRate);
            }
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.warn("adaptive sync detection failed", t);
        }
    }

    private static boolean detectAdaptiveSync() {
        if (GpuDetector.isNvidia()) {
            String renderer = GpuDetector.getRendererString().toLowerCase();
            return renderer.contains("gtx 10") || renderer.contains("gtx 16") || renderer.contains("rtx") || renderer.contains("gtx 9");
        }
        return GpuDetector.isAmd();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isAdaptiveSyncDetected() {
        return adaptiveSyncDetected;
    }

    public static int getRefreshRate() {
        return refreshRate;
    }

    public static int getTargetFps() {
        if (adaptiveSyncDetected) {
            return refreshRate - 3;
        }
        return refreshRate;
    }
}

