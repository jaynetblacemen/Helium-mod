/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_4184
 */
package com.helium.util;

import com.helium.HeliumClient;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_4184;

@Environment(value=EnvType.CLIENT)
public final class VersionCompat {
    private static Method identifierOfMethod = null;
    private static Constructor<?> identifierConstructor = null;
    private static boolean identifierResolved = false;
    private static Method cameraPositionMethod = null;
    private static boolean cameraResolved = false;

    private VersionCompat() {
    }

    public static class_2960 createIdentifier(String namespace, String path) {
        if (!identifierResolved) {
            identifierResolved = true;
            try {
                identifierOfMethod = class_2960.class.getMethod("of", String.class, String.class);
            }
            catch (NoSuchMethodException e) {
                try {
                    identifierConstructor = class_2960.class.getConstructor(String.class, String.class);
                }
                catch (NoSuchMethodException e2) {
                    HeliumClient.LOGGER.warn("could not resolve identifier creation method");
                }
            }
        }
        try {
            if (identifierOfMethod != null) {
                return (class_2960)identifierOfMethod.invoke(null, namespace, path);
            }
            if (identifierConstructor != null) {
                return (class_2960)identifierConstructor.newInstance(namespace, path);
            }
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.warn("identifier creation failed", t);
        }
        return class_2960.method_60655((String)namespace, (String)path);
    }

    public static class_243 getCameraPosition(class_4184 camera) {
        if (!cameraResolved) {
            cameraResolved = true;
            try {
                cameraPositionMethod = class_4184.class.getMethod("getCameraPos", new Class[0]);
            }
            catch (NoSuchMethodException e) {
                try {
                    cameraPositionMethod = class_4184.class.getMethod("getPos", new Class[0]);
                }
                catch (NoSuchMethodException e2) {
                    HeliumClient.LOGGER.warn("could not resolve camera position method");
                }
            }
        }
        if (cameraPositionMethod != null) {
            try {
                return (class_243)cameraPositionMethod.invoke((Object)camera, new Object[0]);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return class_243.field_1353;
    }
}

