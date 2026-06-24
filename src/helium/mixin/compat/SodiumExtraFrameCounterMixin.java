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
@Mixin(targets={"me/flashyreese/mods/sodiumextra/client/FrameCounter"}, remap=false)
public abstract class SodiumExtraFrameCounterMixin {
    @Inject(method={"getSmoothFps"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private void helium$optimizeSmoothFps(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((Integer)cir.getReturnValue());
        cir.setReturnValue((Object)optimized);
    }

    @Inject(method={"getAverageFps"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private void helium$optimizeAverageFps(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((Integer)cir.getReturnValue());
        cir.setReturnValue((Object)optimized);
    }

    @Inject(method={"getOnePercentLowFps"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private void helium$optimizeOnePercentLowFps(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((Integer)cir.getReturnValue());
        cir.setReturnValue((Object)optimized);
    }

    @Inject(method={"getPointOnePercentLowFps"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private void helium$optimizePointOnePercentLowFps(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((Integer)cir.getReturnValue());
        cir.setReturnValue((Object)optimized);
    }
}

