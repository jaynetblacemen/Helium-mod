/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_638
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.tick;

import com.helium.HeliumClient;
import com.helium.lighting.AsyncLightEngine;
import com.helium.memory.MemoryCompactor;
import com.helium.tick.ClientTickCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_638.class})
public abstract class ClientWorldMixin {
    @Unique
    private long helium$tickCounter = 0L;

    @Inject(method={"method_8441"}, at={@At(value="HEAD")})
    private void helium$tickCache(CallbackInfo ci) {
        if (HeliumClient.getConfig() == null || !HeliumClient.getConfig().modEnabled) {
            return;
        }
        long time = this.helium$tickCounter++;
        ClientTickCache.tick(time);
        if (HeliumClient.getConfig().memoryOptimizations) {
            MemoryCompactor.tick(time);
        }
        if (HeliumClient.getConfig().asyncLightUpdates && AsyncLightEngine.isInitialized()) {
            AsyncLightEngine.applyCompleted();
        }
    }
}

