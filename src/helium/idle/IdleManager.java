/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_156
 *  net.minecraft.class_310
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWCursorPosCallback
 *  org.lwjgl.glfw.GLFWCursorPosCallbackI
 *  org.lwjgl.glfw.GLFWKeyCallback
 *  org.lwjgl.glfw.GLFWKeyCallbackI
 *  org.lwjgl.glfw.GLFWMouseButtonCallback
 *  org.lwjgl.glfw.GLFWMouseButtonCallbackI
 *  org.lwjgl.glfw.GLFWScrollCallback
 *  org.lwjgl.glfw.GLFWScrollCallbackI
 *  org.lwjgl.glfw.GLFWWindowFocusCallback
 *  org.lwjgl.glfw.GLFWWindowFocusCallbackI
 */
package com.helium.idle;

import com.helium.HeliumClient;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_156;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;

@Environment(value=EnvType.CLIENT)
public final class IdleManager {
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicLong lastActivityTime = new AtomicLong(0L);
    private static final AtomicBoolean idle = new AtomicBoolean(false);
    private static final AtomicLong lastTickTime = new AtomicLong(0L);
    private static volatile int timeoutSeconds = 60;
    private static volatile int idleFpsLimit = 5;
    private static volatile boolean windowFocused = true;
    private static volatile double prevCursorX = Double.NaN;
    private static volatile double prevCursorY = Double.NaN;
    private static volatile double prevPlayerX = Double.NaN;
    private static volatile double prevPlayerY = Double.NaN;
    private static volatile double prevPlayerZ = Double.NaN;
    private static volatile float prevYaw = Float.NaN;
    private static volatile float prevPitch = Float.NaN;
    private static final double CURSOR_THRESHOLD_SQ = 4.0;
    private static GLFWCursorPosCallback prevCursorCallback;
    private static GLFWKeyCallback prevKeyCallback;
    private static GLFWMouseButtonCallback prevMouseBtnCallback;
    private static GLFWWindowFocusCallback prevFocusCallback;
    private static GLFWScrollCallback prevScrollCallback;
    private static volatile long registeredWindowHandle;

    private IdleManager() {
    }

    public static void init(int timeout, int fpsLimit) {
        if (initialized.getAndSet(true)) {
            return;
        }
        timeoutSeconds = timeout;
        idleFpsLimit = fpsLimit;
        lastActivityTime.set(class_156.method_658());
        HeliumClient.LOGGER.info("idle manager initialized (timeout={}s, idle fps={})", (Object)timeout, (Object)fpsLimit);
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static void setWindow(long handle) {
        if (!initialized.get()) {
            return;
        }
        registeredWindowHandle = handle;
        prevCursorCallback = GLFW.glfwSetCursorPosCallback((long)handle, IdleManager::onCursorPos);
        prevKeyCallback = GLFW.glfwSetKeyCallback((long)handle, IdleManager::onKey);
        prevMouseBtnCallback = GLFW.glfwSetMouseButtonCallback((long)handle, IdleManager::onMouseButton);
        prevFocusCallback = GLFW.glfwSetWindowFocusCallback((long)handle, IdleManager::onWindowFocus);
        prevScrollCallback = GLFW.glfwSetScrollCallback((long)handle, IdleManager::onScroll);
        HeliumClient.LOGGER.info("idle manager: glfw callbacks registered");
    }

    private static void onCursorPos(long window, double x, double y) {
        double dy;
        double dx;
        if (!Double.isNaN(prevCursorX) && (dx = x - prevCursorX) * dx + (dy = y - prevCursorY) * dy >= 4.0) {
            IdleManager.onActivity();
        }
        prevCursorX = x;
        prevCursorY = y;
        if (prevCursorCallback != null) {
            prevCursorCallback.invoke(window, x, y);
        }
    }

    private static void onKey(long window, int key, int scancode, int action, int mods) {
        if (action == 1 || action == 2) {
            IdleManager.onActivity();
        }
        if (prevKeyCallback != null) {
            prevKeyCallback.invoke(window, key, scancode, action, mods);
        }
    }

    private static void onMouseButton(long window, int button, int action, int mods) {
        if (action == 1) {
            IdleManager.onActivity();
        }
        if (prevMouseBtnCallback != null) {
            prevMouseBtnCallback.invoke(window, button, action, mods);
        }
    }

    private static void onWindowFocus(long window, boolean focused) {
        windowFocused = focused;
        if (focused) {
            IdleManager.onActivity();
        }
        if (prevFocusCallback != null) {
            prevFocusCallback.invoke(window, focused);
        }
    }

    private static void onScroll(long window, double xoffset, double yoffset) {
        IdleManager.onActivity();
        if (prevScrollCallback != null) {
            prevScrollCallback.invoke(window, xoffset, yoffset);
        }
    }

    public static void onActivity() {
        if (!initialized.get()) {
            return;
        }
        lastActivityTime.set(class_156.method_658());
        if (idle.getAndSet(false)) {
            HeliumClient.LOGGER.info("activity detected, resuming normal rendering");
        }
    }

    public static void tick() {
        long prev;
        if (!initialized.get()) {
            return;
        }
        long now = class_156.method_658();
        if (now - (prev = lastTickTime.get()) < 50L) {
            return;
        }
        lastTickTime.set(now);
        IdleManager.checkPlayerMovement();
        long lastActivity = lastActivityTime.get();
        int timeout = timeoutSeconds;
        long elapsed = now - lastActivity;
        long timeoutMs = (long)timeout * 1000L;
        if (elapsed >= timeoutMs && !idle.getAndSet(true)) {
            HeliumClient.LOGGER.info("no activity for {}s, entering idle mode (fps={})", (Object)timeout, (Object)idleFpsLimit);
        }
    }

    private static void checkPlayerMovement() {
        try {
            class_310 client = class_310.method_1551();
            if (client == null || client.field_1724 == null) {
                return;
            }
            double px = client.field_1724.method_23317();
            double py = client.field_1724.method_23318();
            double pz = client.field_1724.method_23321();
            float yaw = client.field_1724.method_36454();
            float pitch = client.field_1724.method_36455();
            if (!Double.isNaN(prevPlayerX)) {
                double dx = px - prevPlayerX;
                double dy = py - prevPlayerY;
                double dz = pz - prevPlayerZ;
                float dyaw = yaw - prevYaw;
                float dpitch = pitch - prevPitch;
                if (dx * dx + dy * dy + dz * dz > 0.001 || Math.abs(dyaw) > 0.1f || Math.abs(dpitch) > 0.1f) {
                    IdleManager.onActivity();
                }
            }
            prevPlayerX = px;
            prevPlayerY = py;
            prevPlayerZ = pz;
            prevYaw = yaw;
            prevPitch = pitch;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static boolean isIdle() {
        return idle.get();
    }

    public static boolean isWindowFocused() {
        return windowFocused;
    }

    public static int getIdleFpsLimit() {
        return idleFpsLimit;
    }

    public static void setTimeoutSeconds(int timeout) {
        timeoutSeconds = Math.max(10, timeout);
    }

    public static void setIdleFpsLimit(int limit) {
        idleFpsLimit = Math.max(1, Math.min(30, limit));
    }

    public static int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public static long getTimeSinceLastActivity() {
        return class_156.method_658() - lastActivityTime.get();
    }

    public static void shutdown() {
        if (registeredWindowHandle != 0L) {
            try {
                GLFW.glfwSetCursorPosCallback((long)registeredWindowHandle, (GLFWCursorPosCallbackI)prevCursorCallback);
                GLFW.glfwSetKeyCallback((long)registeredWindowHandle, (GLFWKeyCallbackI)prevKeyCallback);
                GLFW.glfwSetMouseButtonCallback((long)registeredWindowHandle, (GLFWMouseButtonCallbackI)prevMouseBtnCallback);
                GLFW.glfwSetWindowFocusCallback((long)registeredWindowHandle, (GLFWWindowFocusCallbackI)prevFocusCallback);
                GLFW.glfwSetScrollCallback((long)registeredWindowHandle, (GLFWScrollCallbackI)prevScrollCallback);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            registeredWindowHandle = 0L;
        }
        prevCursorCallback = null;
        prevKeyCallback = null;
        prevMouseBtnCallback = null;
        prevFocusCallback = null;
        prevScrollCallback = null;
        idle.set(false);
        initialized.set(false);
    }

    static {
        registeredWindowHandle = 0L;
    }
}

