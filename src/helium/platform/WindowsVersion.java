/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.JNI
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.windows.WinBase
 */
package com.helium.platform;

import com.helium.HeliumClient;
import com.helium.platform.DeviceDetector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.WinBase;

@Environment(value=EnvType.CLIENT)
public final class WindowsVersion {
    private static final int MINIMUM_BUILD = 22000;
    private static final int BACKDROP_BUILD = 22621;
    private static int majorVersion = -1;
    private static int buildNumber = -1;
    private static boolean initialized = false;

    private WindowsVersion() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!DeviceDetector.isWindows()) {
            return;
        }
        try {
            long ntdll = WinBase.LoadLibrary((CharSequence)"ntdll");
            if (ntdll == 0L) {
                return;
            }
            long func = WinBase.GetProcAddress((long)ntdll, (CharSequence)"RtlGetNtVersionNumbers");
            if (func == 0L) {
                return;
            }
            try (MemoryStack stack = MemoryStack.stackPush();){
                long majorPtr = stack.nmalloc(4, 4);
                long minorPtr = stack.nmalloc(4, 4);
                long buildPtr = stack.nmalloc(4, 4);
                MemoryUtil.memPutInt((long)majorPtr, (int)0);
                MemoryUtil.memPutInt((long)minorPtr, (int)0);
                MemoryUtil.memPutInt((long)buildPtr, (int)0);
                JNI.callPPPV((long)majorPtr, (long)minorPtr, (long)buildPtr, (long)func);
                majorVersion = MemoryUtil.memGetInt((long)majorPtr);
                buildNumber = MemoryUtil.memGetInt((long)buildPtr) & 0xFFFFFFF;
            }
            HeliumClient.LOGGER.debug("windows version: {} build {}", (Object)majorVersion, (Object)buildNumber);
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.debug("failed to detect windows version: {}", (Object)t.getMessage());
            majorVersion = -1;
            buildNumber = -1;
        }
    }

    public static boolean isCompatible() {
        if (!initialized) {
            WindowsVersion.init();
        }
        return majorVersion >= 10 && buildNumber >= 22000;
    }

    public static boolean supportsBackdrop() {
        if (!initialized) {
            WindowsVersion.init();
        }
        return buildNumber >= 22621;
    }

    public static int getBuildNumber() {
        if (!initialized) {
            WindowsVersion.init();
        }
        return buildNumber;
    }
}

