/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.helium.mixin.compat;

import com.helium.render.PerformanceMetricsOptimizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Pseudo
@Mixin(targets={"com/fpshud/FPShudClient"}, remap=false)
public abstract class FPShudMetricsMixin {
    @Inject(method={"calcAvrFps"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private static void helium$optimizeAvrMetric(CallbackInfoReturnable<Double> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((int)Math.round((Double)cir.getReturnValue()));
        cir.setReturnValue((Object)optimized);
    }

    @Inject(method={"calcMaxFps"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private static void helium$optimizeMaxMetric(CallbackInfoReturnable<Double> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((int)Math.round((Double)cir.getReturnValue()));
        cir.setReturnValue((Object)optimized);
    }

    @Inject(method={"calcMinFps"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private static void helium$optimizeMinMetric(CallbackInfoReturnable<Double> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((int)Math.round((Double)cir.getReturnValue()));
        cir.setReturnValue((Object)optimized);
    }
}

