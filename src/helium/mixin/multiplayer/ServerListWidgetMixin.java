/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4267
 *  net.minecraft.class_4267$class_4270
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.multiplayer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4267;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_4267.class})
public abstract class ServerListWidgetMixin {
    @Shadow
    @Mutable
    @Final
    private static ThreadPoolExecutor field_19105;
    @Shadow
    @Final
    private List<class_4267.class_4270> field_19109;
    @Unique
    private static final int HELIUM_THREAD_OVERHEAD = 5;
    @Unique
    private static boolean helium$poolInitialized;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void helium$initPingerPool(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.fastServerPing) {
            return;
        }
        if (!helium$poolInitialized) {
            helium$poolInitialized = true;
            this.helium$rebuildThreadPool();
        }
    }

    @Inject(method={"method_20131"}, at={@At(value="HEAD")}, require=0)
    private void helium$onUpdateEntries(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.fastServerPing) {
            return;
        }
        if (field_19105.getActiveCount() >= 5) {
            this.helium$rebuildThreadPool();
        }
    }

    @Unique
    private void helium$rebuildThreadPool() {
        try {
            field_19105.shutdownNow();
        }
        catch (Exception exception) {
            // empty catch block
        }
        int serverCount = this.field_19109 != null ? this.field_19109.size() : 0;
        int poolSize = Math.max(serverCount + 5, Runtime.getRuntime().availableProcessors());
        field_19105 = new ScheduledThreadPoolExecutor(poolSize, new ThreadFactoryBuilder().setNameFormat("Helium-ServerPinger-%d").setDaemon(true).build());
    }

    static {
        helium$poolInitialized = false;
    }
}

