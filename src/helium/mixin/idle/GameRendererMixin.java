/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_757
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.idle;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.idle.IdleManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_757.class})
public abstract class GameRendererMixin {
    @Unique
    private static boolean helium$callbacksRegistered = false;

    @Inject(method={"method_3192"}, at={@At(value="HEAD")}, require=0)
    private void helium$tickIdleCheck(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled) {
            return;
        }
        if (!IdleManager.isInitialized()) {
            return;
        }
        if (!helium$callbacksRegistered) {
            helium$callbacksRegistered = true;
            try {
                class_310 client = class_310.method_1551();
                if (client != null && client.method_22683() != null) {
                    IdleManager.setWindow(client.method_22683().method_4490());
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        IdleManager.tick();
    }
}

