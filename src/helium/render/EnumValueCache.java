/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2351
 */
package com.helium.render;

import com.helium.HeliumClient;
import java.lang.reflect.Method;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2350;

@Environment(value=EnvType.CLIENT)
public final class EnumValueCache {
    private static volatile boolean initialized = false;
    private static class_2350[] cachedDirections;
    private static class_2350.class_2351[] cachedAxes;
    private static class_2350[] cachedHorizontal;
    private static Object[] cachedDyeColors;
    private static Object[] cachedFormattings;

    private EnumValueCache() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        try {
            cachedDirections = class_2350.values();
            cachedAxes = class_2350.class_2351.values();
            int hCount = 0;
            for (class_2350 d : cachedDirections) {
                if (!d.method_10166().method_10179()) continue;
                ++hCount;
            }
            cachedHorizontal = new class_2350[hCount];
            int idx = 0;
            for (class_2350 d : cachedDirections) {
                if (!d.method_10166().method_10179()) continue;
                EnumValueCache.cachedHorizontal[idx++] = d;
            }
            EnumValueCache.cacheGenericEnums();
            initialized = true;
            HeliumClient.LOGGER.info("[helium] enum value cache initialized ({} directions, {} axes)", (Object)cachedDirections.length, (Object)cachedAxes.length);
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.warn("[helium] enum value cache init failed: {}", (Object)t.getMessage());
        }
    }

    private static void cacheGenericEnums() {
        Method valuesMethod;
        try {
            Class<?> dyeColorClass = Class.forName("net.minecraft.util.DyeColor");
            valuesMethod = dyeColorClass.getMethod("values", new Class[0]);
            cachedDyeColors = (Object[])valuesMethod.invoke(null, new Object[0]);
        }
        catch (Throwable dyeColorClass) {
            // empty catch block
        }
        try {
            Class<?> formattingClass = Class.forName("net.minecraft.util.Formatting");
            valuesMethod = formattingClass.getMethod("values", new Class[0]);
            cachedFormattings = (Object[])valuesMethod.invoke(null, new Object[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static class_2350[] getDirections() {
        return cachedDirections;
    }

    public static class_2350.class_2351[] getAxes() {
        return cachedAxes;
    }

    public static class_2350[] getHorizontalDirections() {
        return cachedHorizontal;
    }

    public static Object[] getDyeColors() {
        return cachedDyeColors;
    }

    public static Object[] getFormattings() {
        return cachedFormattings;
    }
}

