/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3568
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.helium.mixin.lighting;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.lighting.AsyncLightEngine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3568;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_3568.class})
public abstract class LightingProviderMixin {
    @Unique
    private static boolean helium$failed = false;

    @Inject(method={"method_15516"}, at={@At(value="HEAD")}, cancellable=false, require=0)
    private void helium$trackLightUpdates(CallbackInfoReturnable<Integer> cir) {
        if (helium$failed) {
            return;
        }
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.asyncLightUpdates) {
                return;
            }
            if (!AsyncLightEngine.isInitialized()) {
                return;
            }
            AsyncLightEngine.onLightUpdateBatch();
        }
        catch (Throwable t) {
            helium$failed = true;
            HeliumClient.LOGGER.warn("async light tracking disabled ({})", (Object)t.getClass().getSimpleName());
        }
    }
}

