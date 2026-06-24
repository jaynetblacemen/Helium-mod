/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_761
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_761.class})
public abstract class WorldRendererPipelineMixin {
    @Unique
    private static boolean helium$failed = false;

    @Inject(method={"method_22710"}, at={@At(value="HEAD")}, require=0)
    private void helium$frameStart(CallbackInfo ci) {
        if (helium$failed) {
            return;
        }
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.renderPipelining) {
                return;
            }
            if (!RenderPipeline.isInitialized()) {
                return;
            }
            class_310 client = class_310.method_1551();
            int maxFps = (Integer)client.field_1690.method_42524().method_41753();
            if (maxFps > 0 && maxFps < 260) {
                RenderPipeline.setTargetFps(maxFps);
            }
            RenderPipeline.onFrameStart();
        }
        catch (Throwable t) {
            helium$failed = true;
            HeliumClient.LOGGER.warn("render pipeline hook disabled ({})", (Object)t.getClass().getSimpleName());
        }
    }

    @Inject(method={"method_22710"}, at={@At(value="RETURN")}, require=0)
    private void helium$frameEnd(CallbackInfo ci) {
        if (helium$failed) {
            return;
        }
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.renderPipelining) {
                return;
            }
            if (!RenderPipeline.isInitialized()) {
                return;
            }
            RenderPipeline.onFrameEnd();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

