/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_309
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.idle;

import com.helium.idle.IdleManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_309.class})
public abstract class KeyboardMixin {
    @Inject(method={"method_1466"}, at={@At(value="HEAD")})
    private void helium$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (IdleManager.isInitialized()) {
            IdleManager.onActivity();
        }
    }

    @Inject(method={"method_1457"}, at={@At(value="HEAD")})
    private void helium$onChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (IdleManager.isInitialized()) {
            IdleManager.onActivity();
        }
    }
}

