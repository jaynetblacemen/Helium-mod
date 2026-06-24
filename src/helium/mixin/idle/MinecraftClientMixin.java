/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_310.class})
public abstract class MinecraftClientMixin {
    @Unique
    private long helium$lastFrameTime = 0L;

    @Inject(method={"method_1523"}, at={@At(value="RETURN")}, require=0)
    private void helium$throttleIdleFps(boolean tick, CallbackInfo ci) {
        long elapsed;
        long remaining;
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.autoPauseOnIdle) {
            return;
        }
        if (!IdleManager.isInitialized() || !IdleManager.isIdle()) {
            this.helium$lastFrameTime = 0L;
            return;
        }
        long now = System.nanoTime();
        long frameIntervalNs = 1000000000L / (long)Math.max(1, IdleManager.getIdleFpsLimit());
        if (this.helium$lastFrameTime > 0L && (remaining = frameIntervalNs - (elapsed = now - this.helium$lastFrameTime)) > 1000000L) {
            try {
                Thread.sleep(remaining / 1000000L, (int)(remaining % 1000000L));
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        this.helium$lastFrameTime = System.nanoTime();
    }
}

