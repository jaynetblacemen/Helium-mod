/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package com.helium.feature;

import com.helium.HeliumClient;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class FullbrightManager {
    private static final AtomicBoolean enabled = new AtomicBoolean(false);
    private static volatile double originalGamma = 1.0;
    private static final double FULLBRIGHT_GAMMA = 16.0;

    private FullbrightManager() {
    }

    public static void toggle() {
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1690 == null) {
            return;
        }
        if (enabled.get()) {
            FullbrightManager.disable();
        } else {
            FullbrightManager.enable();
        }
    }

    public static void enable() {
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1690 == null) {
            return;
        }
        try {
            originalGamma = (Double)client.field_1690.method_42473().method_41753();
        }
        catch (Throwable ignored) {
            originalGamma = 1.0;
        }
        enabled.set(true);
        FullbrightManager.applyGamma(16.0);
        HeliumClient.LOGGER.info("fullbright enabled");
    }

    public static void disable() {
        enabled.set(false);
        FullbrightManager.applyGamma(originalGamma);
        HeliumClient.LOGGER.info("fullbright disabled");
    }

    public static void setEnabled(boolean state) {
        if (state && !enabled.get()) {
            FullbrightManager.enable();
        } else if (!state && enabled.get()) {
            FullbrightManager.disable();
        }
    }

    public static boolean isEnabled() {
        return enabled.get();
    }

    private static void applyGamma(double value) {
        try {
            class_310 client = class_310.method_1551();
            if (client == null || client.field_1690 == null) {
                return;
            }
            client.field_1690.method_42473().method_41748((Object)value);
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.warn("failed to set gamma", t);
        }
    }
}

