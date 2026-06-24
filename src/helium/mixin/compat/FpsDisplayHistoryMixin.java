/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package com.helium.mixin.compat;

import com.helium.render.PerformanceMetricsOptimizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(value=EnvType.CLIENT)
@Pseudo
@Mixin(targets={"io/grayray75/mods/fpsdisplay/FpsHistory"}, remap=false)
public abstract class FpsDisplayHistoryMixin {
    @ModifyVariable(method={"add"}, at=@At(value="HEAD"), argsOnly=true, require=0)
    private int helium$smoothHistorySample(int sample) {
        return PerformanceMetricsOptimizer.computeOptimizedMetric(sample);
    }
}

