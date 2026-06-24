/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.performance;

import com.helium.render.DisplaySyncOptimizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1041.class})
public abstract class WindowDisplayMixin {
    @Inject(method={"method_15998"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void helium$optimizeDisplayRate(CallbackInfo ci) {
        if (!DisplaySyncOptimizer.shouldPerformDisplayUpdate()) {
            ci.cancel();
        }
    }
}

