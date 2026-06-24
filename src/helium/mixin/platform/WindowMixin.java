/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_156
 *  net.minecraft.class_156$class_158
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.platform;

import com.helium.platform.DwmApi;
import com.helium.platform.WindowsVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_156;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1041.class})
public abstract class WindowMixin {
    @Shadow
    @Final
    private long field_5187;
    @Shadow
    private boolean field_5191;

    @Inject(method={"<init>"}, at={@At(value="TAIL")}, require=0)
    private void helium$initWindowStyle(CallbackInfo ci) {
        if (class_156.method_668() != class_156.class_158.field_1133) {
            return;
        }
        WindowsVersion.init();
        DwmApi.applyWindowStyle(this.field_5191, this.field_5187);
    }

    @Inject(method={"setFullscreen"}, at={@At(value="TAIL")}, require=0)
    private void helium$onFullscreenToggle(boolean fullscreen, CallbackInfo ci) {
        if (class_156.method_668() != class_156.class_158.field_1133) {
            return;
        }
        DwmApi.applyWindowStyle(fullscreen, this.field_5187);
    }
}

