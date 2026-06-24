/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 */
package com.helium.gpu;

import com.helium.HeliumClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public final class GpuDetector {
    private static volatile GpuVendor vendor = GpuVendor.UNKNOWN;
    private static volatile String rendererString = "";
    private static volatile boolean initialized = false;
    private static volatile boolean isIntegratedOnly = false;

    private GpuDetector() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        try {
            String lower;
            rendererString = GL11.glGetString((int)7937);
            if (rendererString == null) {
                rendererString = "";
            }
            if ((lower = rendererString.toLowerCase()).contains("nvidia") || lower.contains("geforce") || lower.contains("quadro") || lower.contains("rtx") || lower.contains("gtx")) {
                vendor = GpuVendor.NVIDIA;
            } else if (lower.contains("amd") || lower.contains("radeon") || lower.contains("rx ")) {
                vendor = GpuVendor.AMD;
            } else if (lower.contains("intel") || lower.contains("iris") || lower.contains("uhd") || lower.contains("hd graphics")) {
                vendor = GpuVendor.INTEL;
            }
            isIntegratedOnly = vendor == GpuVendor.INTEL && GpuDetector.isLikelyIntegratedGpu(lower);
            initialized = true;
            HeliumClient.LOGGER.info("gpu detected: {} ({}){}", new Object[]{vendor, rendererString, isIntegratedOnly ? " [integrated]" : ""});
        }
        catch (Throwable t) {
            initialized = true;
            HeliumClient.LOGGER.warn("gpu detection failed", t);
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static GpuVendor getVendor() {
        return vendor;
    }

    public static boolean isNvidia() {
        return vendor == GpuVendor.NVIDIA;
    }

    public static boolean isAmd() {
        return vendor == GpuVendor.AMD;
    }

    public static boolean isIntel() {
        return vendor == GpuVendor.INTEL;
    }

    public static String getRendererString() {
        return rendererString;
    }

    public static boolean isIntegratedOnly() {
        return isIntegratedOnly;
    }

    private static boolean isLikelyIntegratedGpu(String renderer) {
        return renderer.contains("uhd") || renderer.contains("hd graphics") || renderer.contains("iris xe") || renderer.contains("iris plus") || renderer.contains("intel") && !renderer.contains("arc");
    }

    @Environment(value=EnvType.CLIENT)
    public static enum GpuVendor {
        NVIDIA,
        AMD,
        INTEL,
        UNKNOWN;

    }
}

