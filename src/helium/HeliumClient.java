/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_304$class_11900
 *  net.minecraft.class_310
 *  net.minecraft.class_3675$class_307
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.helium;

import com.helium.compat.CrossLoaderCompat;
import com.helium.config.HeliumConfig;
import com.helium.feature.FullbrightManager;
import com.helium.gpu.AdaptiveSyncManager;
import com.helium.gpu.AmdOptimizer;
import com.helium.gpu.GpuDetector;
import com.helium.gpu.IntelOptimizer;
import com.helium.gpu.NvidiaOptimizer;
import com.helium.idle.IdleManager;
import com.helium.lighting.AsyncLightEngine;
import com.helium.math.FastMath;
import com.helium.math.SimdMath;
import com.helium.memory.AllocationReducer;
import com.helium.memory.BufferPool;
import com.helium.memory.NativeMemoryManager;
import com.helium.memory.ObjectPool;
import com.helium.network.FastIpPingOptimizer;
import com.helium.network.PacketBatcher;
import com.helium.platform.DeviceDetector;
import com.helium.render.EnumValueCache;
import com.helium.render.FastAnimationOptimizer;
import com.helium.render.FastWorldLoadingOptimizer;
import com.helium.render.GLStateCache;
import com.helium.render.HeliumBlockEntityCulling;
import com.helium.render.ModelCache;
import com.helium.render.RenderPipeline;
import com.helium.render.TemporalReprojection;
import com.helium.resource.BackgroundResourceProcessor;
import com.helium.startup.FastStartup;
import com.helium.threading.EventPoller;
import com.helium.threading.ThreadPriorityManager;
import com.helium.util.VersionCompat;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public class HeliumClient
implements ClientModInitializer {
    public static final String MOD_ID = "helium";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"helium");
    private static HeliumConfig config;
    private static class_304 fullbrightKey;
    private static boolean hasLithium;
    private static boolean hasIris;
    private static boolean hasImmediatelyFast;
    private static boolean fastMathFailed;
    private static boolean memoryOptsFailed;
    private static boolean glStateCacheFailed;
    private static boolean threadOptsFailed;
    private static boolean networkOptsFailed;
    private static boolean startupOptsFailed;
    private static boolean nativeMemoryFailed;
    private static boolean renderPipelineFailed;
    private static boolean modelCacheFailed;
    private static boolean allocationReducerFailed;
    private static boolean simdMathFailed;
    private static boolean asyncLightFailed;
    private static boolean packetBatcherFailed;
    private static boolean idleManagerFailed;
    private static boolean gpuDetectorFailed;
    private static boolean gpuOptsFailed;
    private static boolean adaptiveSyncFailed;
    private static boolean temporalReprojectionFailed;
    private static boolean fastAnimFailed;
    private static boolean enumCacheFailed;
    private static boolean fastWorldLoadFailed;
    private static boolean gpuInitDeferred;
    private static boolean isAndroid;

    public void onInitializeClient() {
        long start = System.nanoTime();
        config = HeliumConfig.load();
        isAndroid = DeviceDetector.isAndroid();
        if (isAndroid) {
            LOGGER.warn("android detected - disabling gl state cache for compatibility");
            HeliumClient.config.glStateCache = false;
            config.save();
        }
        if (!HeliumClient.config.modEnabled) {
            LOGGER.info("helium is disabled via config");
            return;
        }
        this.detectCompatibleMods();
        this.initFeatureSafely("FastMath", () -> {
            if (HeliumClient.config.fastMath) {
                FastMath.init();
            }
        }, () -> {
            fastMathFailed = true;
        });
        this.initFeatureSafely("MemoryOptimizations", () -> {
            if (HeliumClient.config.memoryOptimizations) {
                ObjectPool.init(512);
                BufferPool.init(64);
            }
        }, () -> {
            memoryOptsFailed = true;
        });
        this.initFeatureSafely("GLStateCache", () -> {
            if (HeliumClient.config.glStateCache && !hasImmediatelyFast) {
                GLStateCache.init();
            } else if (hasImmediatelyFast) {
                LOGGER.info("gl state cache disabled - ImmediatelyFast handles this");
            }
        }, () -> {
            glStateCacheFailed = true;
        });
        this.initFeatureSafely("ThreadOptimizations", () -> {
            if (HeliumClient.config.threadOptimizations) {
                ThreadPriorityManager.init();
                EventPoller.init(1000);
            }
        }, () -> {
            threadOptsFailed = true;
        });
        this.initFeatureSafely("NetworkOptimizations", () -> {
            if (HeliumClient.config.networkOptimizations) {
                BackgroundResourceProcessor.init();
            }
        }, () -> {
            networkOptsFailed = true;
        });
        this.initFeatureSafely("FastStartup", () -> {
            if (HeliumClient.config.fastStartup) {
                FastStartup.init();
            }
        }, () -> {
            startupOptsFailed = true;
        });
        this.initFeatureSafely("NativeMemory", () -> {
            if (HeliumClient.config.nativeMemory) {
                NativeMemoryManager.init(HeliumClient.config.nativeMemoryPoolMb);
            }
        }, () -> {
            nativeMemoryFailed = true;
        });
        this.initFeatureSafely("RenderPipeline", () -> {
            if (HeliumClient.config.renderPipelining) {
                RenderPipeline.init();
            }
        }, () -> {
            renderPipelineFailed = true;
        });
        this.initFeatureSafely("BlockEntityCulling", () -> {
            if (HeliumClient.config.blockEntityCulling && !HeliumBlockEntityCulling.isRegistered()) {
                HeliumBlockEntityCulling.register();
            }
        }, () -> LOGGER.warn("block entity culling fallback failed, will rely on mixin"));
        this.initFeatureSafely("ModelCache", () -> {
            if (HeliumClient.config.modelCache) {
                ModelCache.init(HeliumClient.config.modelCacheMaxMb);
            }
        }, () -> {
            modelCacheFailed = true;
        });
        this.initFeatureSafely("AllocationReducer", () -> {
            if (HeliumClient.config.reducedAllocations) {
                AllocationReducer.init();
            }
        }, () -> {
            allocationReducerFailed = true;
        });
        this.initFeatureSafely("SimdMath", () -> {
            if (HeliumClient.config.simdMath) {
                SimdMath.init();
            }
        }, () -> {
            simdMathFailed = true;
        });
        this.initFeatureSafely("AsyncLightEngine", () -> {
            if (HeliumClient.config.asyncLightUpdates) {
                AsyncLightEngine.init();
            }
        }, () -> {
            asyncLightFailed = true;
        });
        this.initFeatureSafely("PacketBatcher", () -> {
            if (HeliumClient.config.packetBatching) {
                PacketBatcher.init();
            }
        }, () -> {
            packetBatcherFailed = true;
        });
        this.initFeatureSafely("IdleManager", () -> {
            if (HeliumClient.config.autoPauseOnIdle) {
                IdleManager.init(HeliumClient.config.idleTimeoutSeconds, HeliumClient.config.idleFpsLimit);
            }
        }, () -> {
            idleManagerFailed = true;
        });
        this.initFeatureSafely("TemporalReprojection", () -> {
            if (HeliumClient.config.temporalReprojection) {
                TemporalReprojection.init();
            }
        }, () -> {
            temporalReprojectionFailed = true;
        });
        this.initFeatureSafely("FastAnimations", () -> {
            if (HeliumClient.config.fastAnimations) {
                FastAnimationOptimizer.init();
            }
        }, () -> {
            fastAnimFailed = true;
        });
        this.initFeatureSafely("EnumValueCache", () -> {
            if (HeliumClient.config.cachedEnumValues) {
                EnumValueCache.init();
            }
        }, () -> {
            enumCacheFailed = true;
        });
        this.initFeatureSafely("FastWorldLoading", () -> {
            if (HeliumClient.config.fastWorldLoading) {
                FastWorldLoadingOptimizer.init();
            }
        }, () -> {
            fastWorldLoadFailed = true;
        });
        this.initFeatureSafely("FastIpPing", () -> {
            if (HeliumClient.config.fastIpPing) {
                FastIpPingOptimizer.init();
            }
        }, null);
        fullbrightKey = CrossLoaderCompat.registerkeybinding(HeliumClient.createKeyBinding("helium.key.fullbright", class_3675.class_307.field_1668, 71, "helium.key.category"));
        if (HeliumClient.config.fullbright) {
            FullbrightManager.enable();
        }
        CrossLoaderCompat.registertickevent(() -> {
            if (fullbrightKey.method_1436()) {
                FullbrightManager.toggle();
                HeliumClient.config.fullbright = FullbrightManager.isEnabled();
                config.save();
            }
        });
        CrossLoaderCompat.registertickevent(() -> {
            class_310 client = class_310.method_1551();
            if (gpuInitDeferred && client != null && client.method_22683() != null) {
                gpuInitDeferred = false;
                this.initDeferredGpuFeatures();
            }
        });
        long elapsed = (System.nanoTime() - start) / 1000000L;
        LOGGER.info("initialized in {}ms", (Object)elapsed);
    }

    private void initDeferredGpuFeatures() {
        this.initFeatureSafely("RenderThreadPriority", () -> {
            if (HeliumClient.config.threadOptimizations) {
                ThreadPriorityManager.initRenderThread();
            }
        }, null);
        this.initFeatureSafely("GpuDetector", () -> GpuDetector.init(), () -> {
            gpuDetectorFailed = true;
        });
        this.initFeatureSafely("GpuOptimizations", () -> {
            if (HeliumClient.config.nvidiaOptimizations && GpuDetector.isNvidia()) {
                NvidiaOptimizer.init();
            }
            if (HeliumClient.config.amdOptimizations && GpuDetector.isAmd()) {
                AmdOptimizer.init();
            }
            if (HeliumClient.config.intelOptimizations && GpuDetector.isIntel()) {
                IntelOptimizer.init();
            }
        }, () -> {
            gpuOptsFailed = true;
        });
        this.initFeatureSafely("AdaptiveSync", () -> {
            class_310 mc;
            if (HeliumClient.config.adaptiveSync && (mc = class_310.method_1551()) != null && mc.method_22683() != null) {
                AdaptiveSyncManager.init(mc.method_22683().method_4490());
            }
        }, () -> {
            adaptiveSyncFailed = true;
        });
        LOGGER.info("deferred gpu features initialized");
    }

    private void detectCompatibleMods() {
        FabricLoader loader = FabricLoader.getInstance();
        hasLithium = loader.isModLoaded("lithium");
        hasIris = loader.isModLoaded("iris");
        hasImmediatelyFast = loader.isModLoaded("immediatelyfast");
        if (hasLithium) {
            LOGGER.info("lithium detected - compatible");
        }
        if (hasIris) {
            LOGGER.info("iris detected - compatible");
        }
        if (hasImmediatelyFast) {
            LOGGER.info("immediatelyfast detected - disabling gl state cache");
        }
    }

    public static boolean hasImmediatelyFast() {
        return hasImmediatelyFast;
    }

    public static boolean hasLithium() {
        return hasLithium;
    }

    public static boolean hasIris() {
        return hasIris;
    }

    public static HeliumConfig getConfig() {
        return config;
    }

    private void initFeatureSafely(String name, Runnable init, Runnable onFailure) {
        block2: {
            try {
                init.run();
            }
            catch (Throwable t) {
                LOGGER.error("{} failed to initialize, feature disabled", (Object)name, (Object)t);
                if (onFailure == null) break block2;
                onFailure.run();
            }
        }
    }

    public static boolean isFastMathAvailable() {
        return !fastMathFailed;
    }

    public static boolean isMemoryOptsAvailable() {
        return !memoryOptsFailed;
    }

    public static boolean isGlStateCacheAvailable() {
        return !glStateCacheFailed;
    }

    public static boolean isThreadOptsAvailable() {
        return !threadOptsFailed;
    }

    public static boolean isNetworkOptsAvailable() {
        return !networkOptsFailed;
    }

    public static boolean isStartupOptsAvailable() {
        return !startupOptsFailed;
    }

    public static boolean isNativeMemoryAvailable() {
        return !nativeMemoryFailed;
    }

    public static boolean isRenderPipelineAvailable() {
        return !renderPipelineFailed;
    }

    public static boolean isModelCacheAvailable() {
        return !modelCacheFailed;
    }

    public static boolean isAllocationReducerAvailable() {
        return !allocationReducerFailed;
    }

    public static boolean isSimdMathAvailable() {
        return !simdMathFailed;
    }

    public static boolean isAsyncLightAvailable() {
        return !asyncLightFailed;
    }

    public static boolean isPacketBatcherAvailable() {
        return !packetBatcherFailed;
    }

    public static boolean isIdleManagerAvailable() {
        return !idleManagerFailed;
    }

    public static boolean isGpuDetectorAvailable() {
        return !gpuDetectorFailed;
    }

    public static boolean isGpuOptsAvailable() {
        return !gpuOptsFailed;
    }

    public static boolean isAdaptiveSyncAvailable() {
        return !adaptiveSyncFailed;
    }

    public static boolean isTemporalReprojectionAvailable() {
        return !temporalReprojectionFailed;
    }

    public static boolean isAndroid() {
        return DeviceDetector.isAndroid();
    }

    private static class_304 createKeyBinding(String id, class_3675.class_307 type, int code, String category) {
        try {
            Method createMethod = class_304.class_11900.class.getMethod("create", class_2960.class);
            Object categoryObj = createMethod.invoke(null, VersionCompat.createIdentifier(MOD_ID, "keys"));
            return new class_304(id, type, code, (class_304.class_11900)categoryObj);
        }
        catch (Throwable e1) {
            try {
                Constructor ctor = class_304.class.getConstructor(String.class, class_3675.class_307.class, Integer.TYPE, String.class);
                return (class_304)ctor.newInstance(id, type, code, category);
            }
            catch (Throwable e2) {
                LOGGER.warn("failed to create keybinding, using fallback");
                return new class_304(id, code, class_304.class_11900.field_62556);
            }
        }
    }

    static {
        hasLithium = false;
        hasIris = false;
        hasImmediatelyFast = false;
        fastMathFailed = false;
        memoryOptsFailed = false;
        glStateCacheFailed = false;
        threadOptsFailed = false;
        networkOptsFailed = false;
        startupOptsFailed = false;
        nativeMemoryFailed = false;
        renderPipelineFailed = false;
        modelCacheFailed = false;
        allocationReducerFailed = false;
        simdMathFailed = false;
        asyncLightFailed = false;
        packetBatcherFailed = false;
        idleManagerFailed = false;
        gpuDetectorFailed = false;
        gpuOptsFailed = false;
        adaptiveSyncFailed = false;
        temporalReprojectionFailed = false;
        fastAnimFailed = false;
        enumCacheFailed = false;
        fastWorldLoadFailed = false;
        gpuInitDeferred = true;
        isAndroid = false;
    }
}

