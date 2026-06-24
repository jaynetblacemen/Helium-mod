/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_757
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.TemporalReprojection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_757;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_757.class})
public abstract class GameRendererTemporalMixin {
    @Unique
    private static boolean helium$failed = false;
    @Unique
    private static long helium$frameCounter = 0L;

    @Inject(method={"method_3192"}, at={@At(value="TAIL")}, require=0)
    private void helium$captureMatrices(CallbackInfo ci) {
        if (helium$failed) {
            return;
        }
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.temporalReprojection) {
                return;
            }
            if (!TemporalReprojection.isInitialized()) {
                return;
            }
            class_310 client = class_310.method_1551();
            if (client.field_1724 == null || client.field_1773 == null) {
                return;
            }
            Matrix4f proj = client.field_1773.method_22973(((Integer)client.field_1690.method_41808().method_41753()).floatValue());
            Matrix4f view = new Matrix4f();
            float yaw = client.field_1773.method_19418().method_19330();
            float pitch = client.field_1773.method_19418().method_19329();
            view.identity();
            view.rotateX((float)Math.toRadians(-pitch));
            view.rotateY((float)Math.toRadians(yaw + 180.0f));
            Matrix4f combined = new Matrix4f();
            proj.mul((Matrix4fc)view, combined);
            TemporalReprojection.updateMatrices(combined, helium$frameCounter++);
        }
        catch (Throwable t) {
            helium$failed = true;
            HeliumClient.LOGGER.warn("temporal reprojection matrix capture disabled ({})", (Object)t.getClass().getSimpleName());
        }
    }
}

