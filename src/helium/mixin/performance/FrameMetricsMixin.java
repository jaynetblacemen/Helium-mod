/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.helium.mixin.performance;

import com.helium.render.PerformanceMetricsOptimizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_310.class})
public abstract class FrameMetricsMixin {
    @Inject(method={"method_47599"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private static void helium$smoothMetricSample(CallbackInfoReturnable<Integer> cir) {
        int smoothed = PerformanceMetricsOptimizer.computeOptimizedMetric((Integer)cir.getReturnValue());
        cir.setReturnValue((Object)smoothed);
    }
}

