/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package com.helium.render;

import com.helium.HeliumClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public final class TemporalReprojection {
    private static volatile boolean initialized = false;
    private static final Matrix4f previousViewProjection = new Matrix4f();
    private static final Matrix4f currentViewProjection = new Matrix4f();
    private static final Matrix4f reprojectionMatrix = new Matrix4f();
    private static volatile long previousFrameId = -1L;
    private static volatile long currentFrameId = 0L;
    private static volatile int reusedPixels = 0;
    private static volatile int totalPixels = 0;
    private static volatile float reprojectionConfidence = 0.0f;
    private static final float GHOSTING_THRESHOLD = 0.02f;
    private static final float MIN_CONFIDENCE = 0.3f;

    private TemporalReprojection() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        previousViewProjection.identity();
        currentViewProjection.identity();
        reprojectionMatrix.identity();
        HeliumClient.LOGGER.info("temporal reprojection initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void updateMatrices(Matrix4f viewProjection, long frameId) {
        if (!initialized) {
            return;
        }
        previousViewProjection.set((Matrix4fc)currentViewProjection);
        currentViewProjection.set((Matrix4fc)viewProjection);
        previousFrameId = currentFrameId;
        currentFrameId = frameId;
        Matrix4f invCurrent = new Matrix4f();
        currentViewProjection.invert(invCurrent);
        previousViewProjection.mul((Matrix4fc)invCurrent, reprojectionMatrix);
    }

    public static boolean canReproject(float screenX, float screenY) {
        if (!initialized || previousFrameId < 0L) {
            return false;
        }
        Vector4f clipPos = new Vector4f(screenX * 2.0f - 1.0f, screenY * 2.0f - 1.0f, 0.0f, 1.0f);
        reprojectionMatrix.transform(clipPos);
        if (clipPos.w == 0.0f) {
            return false;
        }
        float reprojX = (clipPos.x / clipPos.w + 1.0f) * 0.5f;
        float dx = reprojX - screenX;
        float reprojY = (clipPos.y / clipPos.w + 1.0f) * 0.5f;
        float dy = reprojY - screenY;
        float motionSq = dx * dx + dy * dy;
        return motionSq < 0.02f;
    }

    public static void recordFrameStats(int reused, int total) {
        reusedPixels = reused;
        totalPixels = total;
        reprojectionConfidence = total > 0 ? (float)reused / (float)total : 0.0f;
    }

    public static float getReprojectionConfidence() {
        return reprojectionConfidence;
    }

    public static boolean isConfident() {
        return reprojectionConfidence >= 0.3f;
    }

    public static boolean shouldSkipEntity(double distSq) {
        if (!initialized || previousFrameId < 0L) {
            return false;
        }
        if (reprojectionConfidence < 0.3f) {
            return false;
        }
        if (distSq < 2304.0) {
            return false;
        }
        return (currentFrameId & 1L) == 0L;
    }

    public static Matrix4f getReprojectionMatrix() {
        return reprojectionMatrix;
    }

    public static int getReusedPixels() {
        return reusedPixels;
    }

    public static int getTotalPixels() {
        return totalPixels;
    }

    public static void shutdown() {
        initialized = false;
        previousFrameId = -1L;
        currentFrameId = 0L;
        previousViewProjection.identity();
        currentViewProjection.identity();
        reprojectionMatrix.identity();
    }
}

