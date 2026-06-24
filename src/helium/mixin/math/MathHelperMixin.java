/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.helium.mixin.math;

import com.helium.HeliumClient;
import com.helium.math.FastMath;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_3532.class})
public abstract class MathHelperMixin {
    @Inject(method={"sin(D)D"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void helium$fastSinDouble(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((Object)FastMath.sin(value));
        }
    }

    @Inject(method={"sin(F)F"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void helium$fastSinFloat(float value, CallbackInfoReturnable<Float> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((Object)Float.valueOf(FastMath.sin(value)));
        }
    }

    @Inject(method={"cos(D)D"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void helium$fastCosDouble(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((Object)FastMath.cos(value));
        }
    }

    @Inject(method={"cos(F)F"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void helium$fastCosFloat(float value, CallbackInfoReturnable<Float> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((Object)Float.valueOf(FastMath.cos(value)));
        }
    }

    @Inject(method={"method_15349"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void helium$fastAtan2(double y, double x, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((Object)FastMath.atan2(y, x));
        }
    }

    @Inject(method={"method_15345"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void helium$fastInvSqrt(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((Object)FastMath.inverseSqrt(value));
        }
    }
}

