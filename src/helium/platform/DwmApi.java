/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFWNativeWin32
 *  org.lwjgl.system.JNI
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.windows.WinBase
 */
package com.helium.platform;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.platform.DwmEnums;
import com.helium.platform.WindowsVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.WinBase;

@Environment(value=EnvType.CLIENT)
public final class DwmApi {
    private static final int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;
    private static final int DWMWA_WINDOW_CORNER_PREFERENCE = 33;
    private static final int DWMWA_SYSTEMBACKDROP_TYPE = 38;
    private static final int WCA_ACCENT_POLICY = 19;
    private static final int ACCENT_DISABLED = 0;
    private static final int ACCENT_ENABLE_ACRYLICBLURBEHIND = 4;
    private static volatile long setAttrFunc = -1L;
    private static volatile long setWcaFunc = -1L;

    private DwmApi() {
    }

    private static long getSetAttrFunc() {
        if (setAttrFunc == -1L) {
            try {
                long module = WinBase.LoadLibrary((CharSequence)"dwmapi");
                setAttrFunc = module != 0L ? WinBase.GetProcAddress((long)module, (CharSequence)"DwmSetWindowAttribute") : 0L;
            }
            catch (Throwable t) {
                HeliumClient.LOGGER.debug("dwm: failed to load dwmapi - {}", (Object)t.getMessage());
                setAttrFunc = 0L;
            }
        }
        return setAttrFunc;
    }

    private static long getSetWcaFunc() {
        if (setWcaFunc == -1L) {
            try {
                long module = WinBase.LoadLibrary((CharSequence)"user32");
                setWcaFunc = module != 0L ? WinBase.GetProcAddress((long)module, (CharSequence)"SetWindowCompositionAttribute") : 0L;
            }
            catch (Throwable t) {
                HeliumClient.LOGGER.debug("dwm: failed to load SetWindowCompositionAttribute - {}", (Object)t.getMessage());
                setWcaFunc = 0L;
            }
        }
        return setWcaFunc;
    }

    private static void setAttribute(long hwnd, int attribute, int value) {
        long func = DwmApi.getSetAttrFunc();
        if (func == 0L) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            long pvAttr = stack.nmalloc(4, 4);
            MemoryUtil.memPutInt((long)pvAttr, (int)value);
            JNI.callPPI((long)hwnd, (int)attribute, (long)pvAttr, (int)4, (long)func);
        }
    }

    private static void setAccentPolicy(long hwnd, int accentState, int gradientColor) {
        long func = DwmApi.getSetWcaFunc();
        if (func == 0L) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            long policy = stack.nmalloc(4, 16);
            MemoryUtil.memPutInt((long)(policy + 0L), (int)accentState);
            MemoryUtil.memPutInt((long)(policy + 4L), (int)0);
            MemoryUtil.memPutInt((long)(policy + 8L), (int)gradientColor);
            MemoryUtil.memPutInt((long)(policy + 12L), (int)0);
            long data = stack.nmalloc(8, 24);
            MemoryUtil.memPutInt((long)(data + 0L), (int)19);
            MemoryUtil.memPutLong((long)(data + 8L), (long)policy);
            MemoryUtil.memPutInt((long)(data + 16L), (int)16);
            JNI.callPPI((long)hwnd, (long)data, (long)func);
        }
    }

    public static void applyWindowStyle(boolean fullscreen, long windowHandle) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.windowStyle) {
            return;
        }
        if (!WindowsVersion.isCompatible()) {
            return;
        }
        try {
            long hwnd = GLFWNativeWin32.glfwGetWin32Window((long)windowHandle);
            if (hwnd == 0L) {
                return;
            }
            if (fullscreen) {
                DwmApi.resetWindowStyle(hwnd);
                return;
            }
            DwmApi.setAttribute(hwnd, 20, 1);
            DwmEnums.WindowMaterial material = DwmEnums.WindowMaterial.fromString(config.windowMaterial);
            if (material == DwmEnums.WindowMaterial.ACRYLIC) {
                if (WindowsVersion.supportsBackdrop()) {
                    DwmApi.setAttribute(hwnd, 38, DwmEnums.WindowMaterial.NONE.ordinal());
                }
                DwmApi.setAccentPolicy(hwnd, 4, 0xFFFFFF);
            } else {
                DwmApi.setAccentPolicy(hwnd, 0, 0);
                if (WindowsVersion.supportsBackdrop()) {
                    DwmApi.setAttribute(hwnd, 38, material.ordinal());
                }
            }
            DwmEnums.WindowCorner corner = DwmEnums.WindowCorner.fromString(config.windowCorner);
            DwmApi.setAttribute(hwnd, 33, corner.ordinal());
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.debug("dwm api failed: {}", (Object)t.getMessage());
        }
    }

    private static void resetWindowStyle(long hwnd) {
        try {
            DwmApi.setAccentPolicy(hwnd, 0, 0);
            if (WindowsVersion.supportsBackdrop()) {
                DwmApi.setAttribute(hwnd, 38, DwmEnums.WindowMaterial.AUTO.ordinal());
            }
            DwmApi.setAttribute(hwnd, 33, DwmEnums.WindowCorner.DEFAULT.ordinal());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

