/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL
 */
package com.helium.gpu;

import com.helium.HeliumClient;
import com.helium.gpu.GpuDetector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL;

@Environment(value=EnvType.CLIENT)
public final class AmdOptimizer {
    private static volatile boolean initialized = false;
    private static volatile boolean pinnedMemory = false;
    private static final int BUFFER_ALIGNMENT = 256;

    private AmdOptimizer() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!GpuDetector.isAmd()) {
            HeliumClient.LOGGER.info("amd optimizer skipped - not an amd gpu");
            return;
        }
        try {
            if (GL.getCapabilities().GL_AMD_pinned_memory) {
                pinnedMemory = true;
                HeliumClient.LOGGER.info("amd: pinned memory extension available");
            }
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.warn("amd: pinned memory check failed", t);
        }
        HeliumClient.LOGGER.info("amd optimizer initialized (alignment={})", (Object)256);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static int getBufferAlignment() {
        return 256;
    }

    public static int alignSize(int size) {
        return size + 256 - 1 & 0xFFFFFF00;
    }

    public static boolean hasPinnedMemory() {
        return pinnedMemory;
    }
}

