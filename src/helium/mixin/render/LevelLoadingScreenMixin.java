/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3928
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.FastWorldLoadingOptimizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3928;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_3928.class})
public abstract class LevelLoadingScreenMixin {
    @Unique
    private static boolean helium$failed = false;
    @Unique
    private boolean helium$tracked = false;

    @Inject(method={"method_72800"}, at={@At(value="HEAD")}, require=0)
    private void helium$onLoadStart(CallbackInfo ci) {
        if (helium$failed) {
            return;
        }
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastWorldLoading) {
                return;
            }
            if (!FastWorldLoadingOptimizer.isInitialized()) {
                return;
            }
            if (!this.helium$tracked) {
                this.helium$tracked = true;
                FastWorldLoadingOptimizer.onWorldLoadStart();
            }
        }
        catch (Throwable t) {
            helium$failed = true;
        }
    }

    @Inject(method={"method_25422"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void helium$allowEscClose(CallbackInfoReturnable<Boolean> cir) {
        if (helium$failed) {
            return;
        }
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastWorldLoading) {
                return;
            }
            cir.setReturnValue((Object)true);
        }
        catch (Throwable t) {
            helium$failed = true;
        }
    }

    @Inject(method={"method_25419"}, at={@At(value="HEAD")}, require=0)
    private void helium$onLoadEnd(CallbackInfo ci) {
        if (helium$failed) {
            return;
        }
        try {
            if (this.helium$tracked) {
                FastWorldLoadingOptimizer.onWorldLoadEnd();
                this.helium$tracked = false;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

