/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_156
 *  net.minecraft.class_156$class_158
 */
package com.helium.platform;

import java.io.File;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_156;

@Environment(value=EnvType.CLIENT)
public final class DeviceDetector {
    private static final class_156.class_158 OS = class_156.method_668();
    private static final boolean ANDROID = DeviceDetector.detectAndroid();

    private DeviceDetector() {
    }

    private static boolean detectAndroid() {
        if (new File("/system/build.prop").exists()) {
            return true;
        }
        if (new File("/system/app").isDirectory()) {
            return true;
        }
        String arch = System.getProperty("os.arch", "").toLowerCase();
        String name = System.getProperty("os.name", "").toLowerCase();
        return name.contains("linux") && arch.contains("aarch64") && new File("/data/data").isDirectory();
    }

    public static boolean isWindows() {
        return OS == class_156.class_158.field_1133;
    }

    public static boolean isLinux() {
        return OS == class_156.class_158.field_1135 && !ANDROID;
    }

    public static boolean isMacOS() {
        return OS == class_156.class_158.field_1137;
    }

    public static boolean isAndroid() {
        return ANDROID;
    }
}

