/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_312
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.idle;

import com.helium.idle.IdleManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_312.class})
public abstract class MouseMixin {
    @Inject(method={"method_1600"}, at={@At(value="HEAD")})
    private void helium$onMouseMove(long window, double x, double y, CallbackInfo ci) {
        if (IdleManager.isInitialized()) {
            IdleManager.onActivity();
        }
    }

    @Inject(method={"method_1598"}, at={@At(value="HEAD")})
    private void helium$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (IdleManager.isInitialized()) {
            IdleManager.onActivity();
        }
    }
}

