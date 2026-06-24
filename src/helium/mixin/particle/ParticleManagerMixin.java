/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_702
 *  net.minecraft.class_703
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.particle;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.particle.ParticleBatcher;
import com.helium.particle.ParticleLimiter;
import com.helium.threading.ParticleWorkerPool;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_702;
import net.minecraft.class_703;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_702.class})
public abstract class ParticleManagerMixin {
    @Unique
    private int helium$particleAddCount = 0;
    @Unique
    private static boolean helium$particleCullFailed = false;

    @Inject(method={"method_3057"}, at={@At(value="HEAD")})
    private void helium$initParticlePool(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null) {
            return;
        }
        if (config.threadOptimizations && !ParticleWorkerPool.isInitialized()) {
            ParticleWorkerPool.init(Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
        }
        if (config.particleLimiting && !ParticleLimiter.isInitialized()) {
            ParticleLimiter.init(config.maxParticles);
        }
        if (config.particleBatching && !ParticleBatcher.isInitialized()) {
            ParticleBatcher.init();
        }
        if (ParticleBatcher.isInitialized()) {
            ParticleBatcher.tick();
        }
        if (config.particleLimiting && ParticleLimiter.isInitialized()) {
            ParticleLimiter.setParticleCount(this.helium$particleAddCount);
        }
        this.helium$particleAddCount = Math.max(0, this.helium$particleAddCount - 20);
    }

    @Inject(method={"method_3058"}, at={@At(value="HEAD")}, cancellable=true)
    private void helium$cullDistantParticles(class_703 particle, CallbackInfo ci) {
        block2: {
            try {
                this.helium$cullDistantParticlesInternal(particle, ci);
            }
            catch (Throwable t) {
                if (helium$particleCullFailed) break block2;
                helium$particleCullFailed = true;
                HeliumClient.LOGGER.warn("particle culling disabled on this mc version ({})", (Object)t.getClass().getSimpleName());
            }
        }
    }

    @Unique
    private void helium$cullDistantParticlesInternal(class_703 particle, CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null) {
            return;
        }
        boolean doCulling = config.particleCulling;
        boolean doLimiting = config.particleLimiting;
        boolean doBatching = config.particleBatching;
        if (doCulling) {
            double maxDist;
            double pz;
            double dz;
            double py;
            double dy;
            int cullDist = config.particleCullDistance;
            double px = client.field_1724.method_23317();
            double dx = particle.method_3064().method_1005().field_1352 - px;
            double dist = dx * dx + (dy = particle.method_3064().method_1005().field_1351 - (py = client.field_1724.method_23318())) * dy + (dz = particle.method_3064().method_1005().field_1350 - (pz = client.field_1724.method_23321())) * dz;
            if (dist > (maxDist = (double)(cullDist * cullDist))) {
                ci.cancel();
                return;
            }
        }
        if (doLimiting && ParticleLimiter.isInitialized() && !ParticleLimiter.canAddParticle(particle)) {
            ci.cancel();
            return;
        }
        ++this.helium$particleAddCount;
        if (doBatching && ParticleBatcher.isInitialized()) {
            ParticleBatcher.recordParticleType(particle.getClass().getSimpleName());
        }
    }
}

