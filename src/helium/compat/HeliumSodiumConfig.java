/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint
 *  net.caffeinemc.mods.sodium.api.config.StorageEventHandler
 *  net.caffeinemc.mods.sodium.api.config.option.OptionImpact
 *  net.caffeinemc.mods.sodium.api.config.structure.BooleanOptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.EnumOptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.ModOptionsBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.PageBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 */
package com.helium.compat;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.feature.FullbrightManager;
import com.helium.gpu.GpuDetector;
import com.helium.idle.IdleManager;
import com.helium.platform.DeviceDetector;
import com.helium.platform.DwmApi;
import com.helium.platform.DwmEnums;
import com.helium.render.DisplaySyncOptimizer;
import com.helium.render.FastWorldLoadingOptimizer;
import com.helium.util.VersionCompat;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.structure.BooleanOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.EnumOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ModOptionsBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.PageBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public class HeliumSodiumConfig
implements ConfigEntryPoint {
    private static final String NAMESPACE = "helium";
    private static final int[] DISPLAY_SYNC_HZ = new int[]{0, 60, 75, 120, 144, 165, 240, 360, 500, -1};

    public void registerConfigLate(ConfigBuilder builder) {
        try {
            this.registerConfigInternal(builder);
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.warn("failed to register helium config in sodium - sodium api may be incompatible", t);
        }
    }

    private void registerConfigInternal(ConfigBuilder builder) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null) {
            return;
        }
        StorageEventHandler storage = config::save;
        ModOptionsBuilder mod = builder.registerModOptions(NAMESPACE);
        mod.setName("Helium");
        mod.setIcon(VersionCompat.createIdentifier(NAMESPACE, "textures/icon-only.png"));
        OptionPageBuilder renderPage = builder.createOptionPage();
        renderPage.setName((class_2561)class_2561.method_43471((String)"helium.page.rendering"));
        OptionGroupBuilder cullingGroup = builder.createOptionGroup();
        cullingGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.distance_culling"));
        BooleanOptionBuilder entityCull = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "entity_culling"));
        entityCull.setName((class_2561)class_2561.method_43471((String)"helium.option.entity_culling"));
        entityCull.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.entity_culling.tooltip"));
        entityCull.setImpact(OptionImpact.MEDIUM);
        entityCull.setDefaultValue(Boolean.valueOf(true));
        entityCull.setStorageHandler(storage);
        entityCull.setBinding(v -> {
            config.entityCulling = v;
        }, () -> config.entityCulling);
        cullingGroup.addOption((OptionBuilder)entityCull);
        IntegerOptionBuilder entityDist = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "entity_cull_distance"));
        entityDist.setName((class_2561)class_2561.method_43471((String)"helium.option.entity_cull_distance"));
        entityDist.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.entity_cull_distance.tooltip"));
        entityDist.setImpact(OptionImpact.MEDIUM);
        entityDist.setDefaultValue(Integer.valueOf(64));
        entityDist.setRange(16, 128, 8);
        entityDist.setValueFormatter(v -> class_2561.method_43469((String)"helium.suffix.blocks", (Object[])new Object[]{v}));
        entityDist.setStorageHandler(storage);
        entityDist.setBinding(v -> {
            config.entityCullDistance = v;
        }, () -> config.entityCullDistance);
        cullingGroup.addOption((OptionBuilder)entityDist);
        BooleanOptionBuilder blockEntityCull = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "block_entity_culling"));
        blockEntityCull.setName((class_2561)class_2561.method_43471((String)"helium.option.block_entity_culling"));
        blockEntityCull.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.block_entity_culling.tooltip"));
        blockEntityCull.setImpact(OptionImpact.HIGH);
        blockEntityCull.setDefaultValue(Boolean.valueOf(true));
        blockEntityCull.setStorageHandler(storage);
        blockEntityCull.setBinding(v -> {
            config.blockEntityCulling = v;
        }, () -> config.blockEntityCulling);
        cullingGroup.addOption((OptionBuilder)blockEntityCull);
        IntegerOptionBuilder blockEntityDist = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "block_entity_cull_distance"));
        blockEntityDist.setName((class_2561)class_2561.method_43471((String)"helium.option.block_entity_cull_distance"));
        blockEntityDist.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.block_entity_cull_distance.tooltip"));
        blockEntityDist.setImpact(OptionImpact.HIGH);
        blockEntityDist.setDefaultValue(Integer.valueOf(48));
        blockEntityDist.setRange(16, 96, 8);
        blockEntityDist.setValueFormatter(v -> class_2561.method_43469((String)"helium.suffix.blocks", (Object[])new Object[]{v}));
        blockEntityDist.setStorageHandler(storage);
        blockEntityDist.setBinding(v -> {
            config.blockEntityCullDistance = v;
        }, () -> config.blockEntityCullDistance);
        cullingGroup.addOption((OptionBuilder)blockEntityDist);
        BooleanOptionBuilder particleCull = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "particle_culling"));
        particleCull.setName((class_2561)class_2561.method_43471((String)"helium.option.particle_culling"));
        particleCull.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.particle_culling.tooltip"));
        particleCull.setImpact(OptionImpact.MEDIUM);
        particleCull.setDefaultValue(Boolean.valueOf(true));
        particleCull.setStorageHandler(storage);
        particleCull.setBinding(v -> {
            config.particleCulling = v;
        }, () -> config.particleCulling);
        cullingGroup.addOption((OptionBuilder)particleCull);
        IntegerOptionBuilder particleDist = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "particle_cull_distance"));
        particleDist.setName((class_2561)class_2561.method_43471((String)"helium.option.particle_cull_distance"));
        particleDist.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.particle_cull_distance.tooltip"));
        particleDist.setImpact(OptionImpact.MEDIUM);
        particleDist.setDefaultValue(Integer.valueOf(32));
        particleDist.setRange(8, 64, 4);
        particleDist.setValueFormatter(v -> class_2561.method_43469((String)"helium.suffix.blocks", (Object[])new Object[]{v}));
        particleDist.setStorageHandler(storage);
        particleDist.setBinding(v -> {
            config.particleCullDistance = v;
        }, () -> config.particleCullDistance);
        cullingGroup.addOption((OptionBuilder)particleDist);
        renderPage.addOptionGroup(cullingGroup);
        OptionGroupBuilder particleGroup = builder.createOptionGroup();
        particleGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.particle_optimization"));
        BooleanOptionBuilder particleLimit = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "particle_limiting"));
        particleLimit.setName((class_2561)class_2561.method_43471((String)"helium.option.particle_limiting"));
        particleLimit.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.particle_limiting.tooltip"));
        particleLimit.setImpact(OptionImpact.HIGH);
        particleLimit.setDefaultValue(Boolean.valueOf(true));
        particleLimit.setStorageHandler(storage);
        particleLimit.setBinding(v -> {
            config.particleLimiting = v;
        }, () -> config.particleLimiting);
        particleGroup.addOption((OptionBuilder)particleLimit);
        IntegerOptionBuilder maxParticles = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "max_particles"));
        maxParticles.setName((class_2561)class_2561.method_43471((String)"helium.option.max_particles"));
        maxParticles.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.max_particles.tooltip"));
        maxParticles.setImpact(OptionImpact.HIGH);
        maxParticles.setDefaultValue(Integer.valueOf(1000));
        maxParticles.setRange(100, 5000, 100);
        maxParticles.setValueFormatter(v -> class_2561.method_30163((String)String.valueOf(v)));
        maxParticles.setStorageHandler(storage);
        maxParticles.setBinding(v -> {
            config.maxParticles = v;
        }, () -> config.maxParticles);
        particleGroup.addOption((OptionBuilder)maxParticles);
        BooleanOptionBuilder particlePriorityOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "particle_priority"));
        particlePriorityOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.particle_priority"));
        particlePriorityOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.particle_priority.tooltip"));
        particlePriorityOpt.setImpact(OptionImpact.LOW);
        particlePriorityOpt.setDefaultValue(Boolean.valueOf(true));
        particlePriorityOpt.setStorageHandler(storage);
        particlePriorityOpt.setBinding(v -> {
            config.particlePriority = v;
        }, () -> config.particlePriority);
        particleGroup.addOption((OptionBuilder)particlePriorityOpt);
        BooleanOptionBuilder particleBatch = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "particle_batching"));
        particleBatch.setName((class_2561)class_2561.method_43471((String)"helium.option.particle_batching"));
        particleBatch.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.particle_batching.tooltip"));
        particleBatch.setImpact(OptionImpact.LOW);
        particleBatch.setDefaultValue(Boolean.valueOf(true));
        particleBatch.setStorageHandler(storage);
        particleBatch.setBinding(v -> {
            config.particleBatching = v;
        }, () -> config.particleBatching);
        particleGroup.addOption((OptionBuilder)particleBatch);
        renderPage.addOptionGroup(particleGroup);
        OptionGroupBuilder renderOptGroup = builder.createOptionGroup();
        renderOptGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.render_pipeline"));
        BooleanOptionBuilder animThrottle = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "animation_throttling"));
        animThrottle.setName((class_2561)class_2561.method_43471((String)"helium.option.animation_throttling"));
        animThrottle.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.animation_throttling.tooltip"));
        animThrottle.setImpact(OptionImpact.LOW);
        animThrottle.setDefaultValue(Boolean.valueOf(true));
        animThrottle.setStorageHandler(storage);
        animThrottle.setBinding(v -> {
            config.animationThrottling = v;
        }, () -> config.animationThrottling);
        renderOptGroup.addOption((OptionBuilder)animThrottle);
        BooleanOptionBuilder fastMathOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_math"));
        fastMathOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.fast_math"));
        fastMathOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.fast_math.tooltip"));
        fastMathOpt.setImpact(OptionImpact.LOW);
        fastMathOpt.setDefaultValue(Boolean.valueOf(true));
        fastMathOpt.setStorageHandler(storage);
        fastMathOpt.setBinding(v -> {
            config.fastMath = v;
        }, () -> config.fastMath);
        renderOptGroup.addOption((OptionBuilder)fastMathOpt);
        BooleanOptionBuilder glCache = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "gl_state_cache"));
        glCache.setName((class_2561)class_2561.method_43471((String)"helium.option.gl_state_cache.experimental"));
        if (HeliumClient.isAndroid()) {
            glCache.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.gl_state_cache.tooltip.android"));
            glCache.setEnabled(false);
            glCache.setDefaultValue(Boolean.valueOf(false));
            glCache.setBinding(v -> {}, () -> false);
        } else {
            glCache.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.gl_state_cache.tooltip.experimental"));
            glCache.setDefaultValue(Boolean.valueOf(false));
            glCache.setBinding(v -> {
                config.glStateCache = v;
            }, () -> config.glStateCache);
        }
        glCache.setImpact(OptionImpact.VARIES);
        glCache.setStorageHandler(storage);
        renderOptGroup.addOption((OptionBuilder)glCache);
        BooleanOptionBuilder fastAnimOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_animations"));
        fastAnimOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.fast_animations"));
        fastAnimOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.fast_animations.tooltip"));
        fastAnimOpt.setImpact(OptionImpact.MEDIUM);
        fastAnimOpt.setDefaultValue(Boolean.valueOf(false));
        fastAnimOpt.setStorageHandler(storage);
        fastAnimOpt.setBinding(v -> {
            config.fastAnimations = v;
        }, () -> config.fastAnimations);
        renderOptGroup.addOption((OptionBuilder)fastAnimOpt);
        BooleanOptionBuilder enumCacheOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "cached_enum_values"));
        enumCacheOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.cached_enum_values"));
        enumCacheOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.cached_enum_values.tooltip"));
        enumCacheOpt.setImpact(OptionImpact.MEDIUM);
        enumCacheOpt.setDefaultValue(Boolean.valueOf(true));
        enumCacheOpt.setStorageHandler(storage);
        enumCacheOpt.setBinding(v -> {
            config.cachedEnumValues = v;
        }, () -> config.cachedEnumValues);
        renderOptGroup.addOption((OptionBuilder)enumCacheOpt);
        renderPage.addOptionGroup(renderOptGroup);
        OptionGroupBuilder cachingGroup = builder.createOptionGroup();
        cachingGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.caching"));
        BooleanOptionBuilder modelCacheOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "model_cache"));
        modelCacheOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.model_cache"));
        modelCacheOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.model_cache.tooltip"));
        modelCacheOpt.setImpact(OptionImpact.MEDIUM);
        modelCacheOpt.setDefaultValue(Boolean.valueOf(true));
        modelCacheOpt.setStorageHandler(storage);
        modelCacheOpt.setBinding(v -> {
            config.modelCache = v;
        }, () -> config.modelCache);
        cachingGroup.addOption((OptionBuilder)modelCacheOpt);
        IntegerOptionBuilder modelCacheSizeOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "model_cache_max_mb"));
        modelCacheSizeOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.model_cache_size"));
        modelCacheSizeOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.model_cache_size.tooltip"));
        modelCacheSizeOpt.setImpact(OptionImpact.MEDIUM);
        modelCacheSizeOpt.setDefaultValue(Integer.valueOf(64));
        modelCacheSizeOpt.setRange(16, 256, 16);
        modelCacheSizeOpt.setValueFormatter(v -> class_2561.method_43469((String)"helium.suffix.mb", (Object[])new Object[]{v}));
        modelCacheSizeOpt.setStorageHandler(storage);
        modelCacheSizeOpt.setBinding(v -> {
            config.modelCacheMaxMb = v;
        }, () -> config.modelCacheMaxMb);
        cachingGroup.addOption((OptionBuilder)modelCacheSizeOpt);
        renderPage.addOptionGroup(cachingGroup);
        mod.addPage((PageBuilder)renderPage);
        OptionPageBuilder generalPage = builder.createOptionPage();
        generalPage.setName((class_2561)class_2561.method_43471((String)"helium.page.general"));
        OptionGroupBuilder engineGroup = builder.createOptionGroup();
        engineGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.engine"));
        BooleanOptionBuilder memOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "memory_optimizations"));
        memOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.memory_optimizations"));
        memOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.memory_optimizations.tooltip"));
        memOpt.setImpact(OptionImpact.LOW);
        memOpt.setDefaultValue(Boolean.valueOf(true));
        memOpt.setStorageHandler(storage);
        memOpt.setBinding(v -> {
            config.memoryOptimizations = v;
        }, () -> config.memoryOptimizations);
        engineGroup.addOption((OptionBuilder)memOpt);
        BooleanOptionBuilder threadOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "thread_optimizations"));
        threadOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.thread_optimizations"));
        threadOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.thread_optimizations.tooltip"));
        threadOpt.setImpact(OptionImpact.LOW);
        threadOpt.setDefaultValue(Boolean.valueOf(true));
        threadOpt.setStorageHandler(storage);
        threadOpt.setBinding(v -> {
            config.threadOptimizations = v;
        }, () -> config.threadOptimizations);
        engineGroup.addOption((OptionBuilder)threadOpt);
        BooleanOptionBuilder startupOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_startup"));
        startupOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.fast_startup"));
        startupOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.fast_startup.tooltip"));
        startupOpt.setImpact(OptionImpact.LOW);
        startupOpt.setDefaultValue(Boolean.valueOf(true));
        startupOpt.setStorageHandler(storage);
        startupOpt.setBinding(v -> {
            config.fastStartup = v;
        }, () -> config.fastStartup);
        engineGroup.addOption((OptionBuilder)startupOpt);
        BooleanOptionBuilder fastWorldLoadOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_world_loading"));
        fastWorldLoadOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.fast_world_loading"));
        fastWorldLoadOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.fast_world_loading.tooltip"));
        fastWorldLoadOpt.setImpact(OptionImpact.LOW);
        fastWorldLoadOpt.setDefaultValue(Boolean.valueOf(false));
        fastWorldLoadOpt.setStorageHandler(storage);
        fastWorldLoadOpt.setBinding(v -> {
            config.fastWorldLoading = v;
            if (v.booleanValue() && FastWorldLoadingOptimizer.isInitialized()) {
                FastWorldLoadingOptimizer.enable();
            } else {
                FastWorldLoadingOptimizer.disable();
            }
        }, () -> config.fastWorldLoading);
        engineGroup.addOption((OptionBuilder)fastWorldLoadOpt);
        BooleanOptionBuilder reducedAllocOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "reduced_allocations"));
        reducedAllocOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.reduced_allocations"));
        reducedAllocOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.reduced_allocations.tooltip"));
        reducedAllocOpt.setImpact(OptionImpact.LOW);
        reducedAllocOpt.setDefaultValue(Boolean.valueOf(true));
        reducedAllocOpt.setStorageHandler(storage);
        reducedAllocOpt.setBinding(v -> {
            config.reducedAllocations = v;
        }, () -> config.reducedAllocations);
        engineGroup.addOption((OptionBuilder)reducedAllocOpt);
        BooleanOptionBuilder idleOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "auto_pause_on_idle"));
        idleOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.auto_pause_on_idle"));
        idleOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.auto_pause_on_idle.tooltip"));
        idleOpt.setImpact(OptionImpact.LOW);
        idleOpt.setDefaultValue(Boolean.valueOf(false));
        idleOpt.setStorageHandler(storage);
        idleOpt.setBinding(v -> {
            config.autoPauseOnIdle = v;
        }, () -> config.autoPauseOnIdle);
        engineGroup.addOption((OptionBuilder)idleOpt);
        IntegerOptionBuilder idleTimeoutOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "idle_timeout_seconds"));
        idleTimeoutOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.idle_timeout"));
        idleTimeoutOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.idle_timeout.tooltip"));
        idleTimeoutOpt.setImpact(OptionImpact.LOW);
        idleTimeoutOpt.setDefaultValue(Integer.valueOf(60));
        idleTimeoutOpt.setRange(10, 300, 10);
        idleTimeoutOpt.setValueFormatter(v -> class_2561.method_43469((String)"helium.suffix.seconds", (Object[])new Object[]{v}));
        idleTimeoutOpt.setStorageHandler(storage);
        idleTimeoutOpt.setBinding(v -> {
            config.idleTimeoutSeconds = v;
            if (IdleManager.isInitialized()) {
                IdleManager.setTimeoutSeconds(v);
            }
        }, () -> config.idleTimeoutSeconds);
        engineGroup.addOption((OptionBuilder)idleTimeoutOpt);
        IntegerOptionBuilder idleFpsOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "idle_fps_limit"));
        idleFpsOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.idle_fps_limit"));
        idleFpsOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.idle_fps_limit.tooltip"));
        idleFpsOpt.setImpact(OptionImpact.LOW);
        idleFpsOpt.setDefaultValue(Integer.valueOf(5));
        idleFpsOpt.setRange(1, 30, 1);
        idleFpsOpt.setValueFormatter(v -> class_2561.method_43469((String)"helium.suffix.fps", (Object[])new Object[]{v}));
        idleFpsOpt.setStorageHandler(storage);
        idleFpsOpt.setBinding(v -> {
            config.idleFpsLimit = v;
            if (IdleManager.isInitialized()) {
                IdleManager.setIdleFpsLimit(v);
            }
        }, () -> config.idleFpsLimit);
        engineGroup.addOption((OptionBuilder)idleFpsOpt);
        generalPage.addOptionGroup(engineGroup);
        BooleanOptionBuilder fullbrightOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fullbright"));
        fullbrightOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.fullbright"));
        fullbrightOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.fullbright.tooltip"));
        fullbrightOpt.setImpact(OptionImpact.LOW);
        fullbrightOpt.setDefaultValue(Boolean.valueOf(false));
        fullbrightOpt.setStorageHandler(storage);
        fullbrightOpt.setBinding(v -> {
            config.fullbright = v;
            FullbrightManager.setEnabled(v);
        }, () -> config.fullbright);
        engineGroup.addOption((OptionBuilder)fullbrightOpt);
        BooleanOptionBuilder netOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "network_optimizations"));
        netOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.network_optimizations"));
        netOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.network_optimizations.tooltip"));
        netOpt.setImpact(OptionImpact.LOW);
        netOpt.setDefaultValue(Boolean.valueOf(true));
        netOpt.setStorageHandler(storage);
        netOpt.setBinding(v -> {
            config.networkOptimizations = v;
        }, () -> config.networkOptimizations);
        engineGroup.addOption((OptionBuilder)netOpt);
        mod.addPage((PageBuilder)generalPage);
        OptionPageBuilder qolPage = builder.createOptionPage();
        qolPage.setName((class_2561)class_2561.method_43471((String)"helium.page.qol"));
        OptionGroupBuilder universalQolGroup = builder.createOptionGroup();
        universalQolGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.universal_qol"));
        BooleanOptionBuilder smoothScrollOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "smooth_scrolling"));
        smoothScrollOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.smooth_scrolling"));
        smoothScrollOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.smooth_scrolling.tooltip"));
        smoothScrollOpt.setImpact(OptionImpact.LOW);
        smoothScrollOpt.setDefaultValue(Boolean.valueOf(true));
        smoothScrollOpt.setStorageHandler(storage);
        smoothScrollOpt.setBinding(v -> {
            config.smoothScrolling = v;
        }, () -> config.smoothScrolling);
        universalQolGroup.addOption((OptionBuilder)smoothScrollOpt);
        BooleanOptionBuilder windowStyleOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "window_style"));
        windowStyleOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.window_style"));
        windowStyleOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.window_style.tooltip"));
        windowStyleOpt.setImpact(OptionImpact.LOW);
        windowStyleOpt.setDefaultValue(Boolean.valueOf(true));
        windowStyleOpt.setStorageHandler(storage);
        windowStyleOpt.setEnabled(DeviceDetector.isWindows());
        if (!DeviceDetector.isWindows()) {
            windowStyleOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.window_style.tooltip.non_windows"));
        }
        windowStyleOpt.setBinding(v -> {
            config.windowStyle = v;
            DwmApi.applyWindowStyle(false, class_310.method_1551().method_22683().method_4490());
        }, () -> config.windowStyle);
        universalQolGroup.addOption((OptionBuilder)windowStyleOpt);
        EnumOptionBuilder windowMaterialOpt = builder.createEnumOption(VersionCompat.createIdentifier(NAMESPACE, "window_material"), DwmEnums.WindowMaterial.class);
        windowMaterialOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.window_material"));
        windowMaterialOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.window_material.tooltip"));
        windowMaterialOpt.setImpact(OptionImpact.LOW);
        windowMaterialOpt.setDefaultValue((Enum)DwmEnums.WindowMaterial.TABBED);
        windowMaterialOpt.setElementNameProvider(v -> class_2561.method_43471((String)("helium.option.window_material." + v.id)));
        windowMaterialOpt.setStorageHandler(storage);
        windowMaterialOpt.setEnabled(DeviceDetector.isWindows());
        if (!DeviceDetector.isWindows()) {
            windowMaterialOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.window_style.tooltip.non_windows"));
        }
        windowMaterialOpt.setBinding(v -> {
            config.windowMaterial = v.name();
            DwmApi.applyWindowStyle(false, class_310.method_1551().method_22683().method_4490());
        }, () -> DwmEnums.WindowMaterial.fromString(config.windowMaterial));
        universalQolGroup.addOption((OptionBuilder)windowMaterialOpt);
        EnumOptionBuilder windowCornerOpt = builder.createEnumOption(VersionCompat.createIdentifier(NAMESPACE, "window_corner"), DwmEnums.WindowCorner.class);
        windowCornerOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.window_corner"));
        windowCornerOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.window_corner.tooltip"));
        windowCornerOpt.setImpact(OptionImpact.LOW);
        windowCornerOpt.setDefaultValue((Enum)DwmEnums.WindowCorner.ROUND);
        windowCornerOpt.setElementNameProvider(v -> class_2561.method_43471((String)("helium.option.window_corner." + v.id)));
        windowCornerOpt.setStorageHandler(storage);
        windowCornerOpt.setEnabled(DeviceDetector.isWindows());
        if (!DeviceDetector.isWindows()) {
            windowCornerOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.window_style.tooltip.non_windows"));
        }
        windowCornerOpt.setBinding(v -> {
            config.windowCorner = v.name();
            DwmApi.applyWindowStyle(false, class_310.method_1551().method_22683().method_4490());
        }, () -> DwmEnums.WindowCorner.fromString(config.windowCorner));
        universalQolGroup.addOption((OptionBuilder)windowCornerOpt);
        qolPage.addOptionGroup(universalQolGroup);
        OptionGroupBuilder serverGroup = builder.createOptionGroup();
        serverGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.multiplayer"));
        BooleanOptionBuilder fastPing = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_server_ping"));
        fastPing.setName((class_2561)class_2561.method_43471((String)"helium.option.fast_server_ping"));
        fastPing.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.fast_server_ping.tooltip"));
        fastPing.setImpact(OptionImpact.LOW);
        fastPing.setDefaultValue(Boolean.valueOf(true));
        fastPing.setStorageHandler(storage);
        fastPing.setBinding(v -> {
            config.fastServerPing = v;
        }, () -> config.fastServerPing);
        serverGroup.addOption((OptionBuilder)fastPing);
        BooleanOptionBuilder fastIpPing = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_ip_ping"));
        fastIpPing.setName((class_2561)class_2561.method_43471((String)"helium.option.fast_ip_ping"));
        fastIpPing.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.fast_ip_ping.tooltip"));
        fastIpPing.setImpact(OptionImpact.LOW);
        fastIpPing.setDefaultValue(Boolean.valueOf(true));
        fastIpPing.setStorageHandler(storage);
        fastIpPing.setBinding(v -> {
            config.fastIpPing = v;
        }, () -> config.fastIpPing);
        serverGroup.addOption((OptionBuilder)fastIpPing);
        BooleanOptionBuilder scrollKeep = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "preserve_scroll_on_refresh"));
        scrollKeep.setName((class_2561)class_2561.method_43471((String)"helium.option.preserve_scroll"));
        scrollKeep.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.preserve_scroll.tooltip"));
        scrollKeep.setImpact(OptionImpact.LOW);
        scrollKeep.setDefaultValue(Boolean.valueOf(true));
        scrollKeep.setStorageHandler(storage);
        scrollKeep.setBinding(v -> {
            config.preserveScrollOnRefresh = v;
        }, () -> config.preserveScrollOnRefresh);
        serverGroup.addOption((OptionBuilder)scrollKeep);
        BooleanOptionBuilder directPreview = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "direct_connect_preview"));
        directPreview.setName((class_2561)class_2561.method_43471((String)"helium.option.direct_connect_preview"));
        directPreview.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.direct_connect_preview.tooltip"));
        directPreview.setImpact(OptionImpact.LOW);
        directPreview.setDefaultValue(Boolean.valueOf(true));
        directPreview.setStorageHandler(storage);
        directPreview.setBinding(v -> {
            config.directConnectPreview = v;
        }, () -> config.directConnectPreview);
        serverGroup.addOption((OptionBuilder)directPreview);
        qolPage.addOptionGroup(serverGroup);
        mod.addPage((PageBuilder)qolPage);
        OptionPageBuilder overlayPage = builder.createOptionPage();
        overlayPage.setName((class_2561)class_2561.method_43471((String)"helium.page.overlay"));
        OptionGroupBuilder overlayGroup = builder.createOptionGroup();
        overlayGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.fps_overlay"));
        BooleanOptionBuilder fpsOverlayOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fps_overlay"));
        fpsOverlayOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.fps_overlay"));
        fpsOverlayOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.fps_overlay.tooltip"));
        fpsOverlayOpt.setImpact(OptionImpact.LOW);
        fpsOverlayOpt.setDefaultValue(Boolean.valueOf(true));
        fpsOverlayOpt.setStorageHandler(storage);
        fpsOverlayOpt.setBinding(v -> {
            config.fpsOverlay = v;
        }, () -> config.fpsOverlay);
        overlayGroup.addOption((OptionBuilder)fpsOverlayOpt);
        IntegerOptionBuilder overlayTransOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_transparency"));
        overlayTransOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.overlay_transparency"));
        overlayTransOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.overlay_transparency.tooltip"));
        overlayTransOpt.setImpact(OptionImpact.LOW);
        overlayTransOpt.setDefaultValue(Integer.valueOf(60));
        overlayTransOpt.setRange(0, 100, 10);
        overlayTransOpt.setValueFormatter(v -> class_2561.method_43469((String)"helium.suffix.percent", (Object[])new Object[]{v}));
        overlayTransOpt.setStorageHandler(storage);
        overlayTransOpt.setBinding(v -> {
            config.overlayTransparency = v;
        }, () -> config.overlayTransparency);
        overlayGroup.addOption((OptionBuilder)overlayTransOpt);
        overlayPage.addOptionGroup(overlayGroup);
        OptionGroupBuilder overlayContentGroup = builder.createOptionGroup();
        overlayContentGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.overlay_content"));
        BooleanOptionBuilder showFpsOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_fps"));
        showFpsOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.show_fps"));
        showFpsOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.show_fps.tooltip"));
        showFpsOpt.setImpact(OptionImpact.LOW);
        showFpsOpt.setDefaultValue(Boolean.valueOf(true));
        showFpsOpt.setStorageHandler(storage);
        showFpsOpt.setBinding(v -> {
            config.overlayShowFps = v;
        }, () -> config.overlayShowFps);
        overlayContentGroup.addOption((OptionBuilder)showFpsOpt);
        BooleanOptionBuilder showFpsMinMaxAvgOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_fps_stats"));
        showFpsMinMaxAvgOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.show_fps_stats"));
        showFpsMinMaxAvgOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.show_fps_stats.tooltip"));
        showFpsMinMaxAvgOpt.setImpact(OptionImpact.LOW);
        showFpsMinMaxAvgOpt.setDefaultValue(Boolean.valueOf(false));
        showFpsMinMaxAvgOpt.setStorageHandler(storage);
        showFpsMinMaxAvgOpt.setBinding(v -> {
            config.overlayShowFpsMinMaxAvg = v;
        }, () -> config.overlayShowFpsMinMaxAvg);
        overlayContentGroup.addOption((OptionBuilder)showFpsMinMaxAvgOpt);
        BooleanOptionBuilder showMemoryOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_memory"));
        showMemoryOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.show_memory"));
        showMemoryOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.show_memory.tooltip"));
        showMemoryOpt.setImpact(OptionImpact.LOW);
        showMemoryOpt.setDefaultValue(Boolean.valueOf(false));
        showMemoryOpt.setStorageHandler(storage);
        showMemoryOpt.setBinding(v -> {
            config.overlayShowMemory = v;
        }, () -> config.overlayShowMemory);
        overlayContentGroup.addOption((OptionBuilder)showMemoryOpt);
        BooleanOptionBuilder showParticlesOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_particles"));
        showParticlesOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.show_particles"));
        showParticlesOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.show_particles.tooltip"));
        showParticlesOpt.setImpact(OptionImpact.LOW);
        showParticlesOpt.setDefaultValue(Boolean.valueOf(false));
        showParticlesOpt.setStorageHandler(storage);
        showParticlesOpt.setBinding(v -> {
            config.overlayShowParticles = v;
        }, () -> config.overlayShowParticles);
        overlayContentGroup.addOption((OptionBuilder)showParticlesOpt);
        BooleanOptionBuilder showCoordsOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_coordinates"));
        showCoordsOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.show_coordinates"));
        showCoordsOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.show_coordinates.tooltip"));
        showCoordsOpt.setImpact(OptionImpact.LOW);
        showCoordsOpt.setDefaultValue(Boolean.valueOf(false));
        showCoordsOpt.setStorageHandler(storage);
        showCoordsOpt.setBinding(v -> {
            config.overlayShowCoordinates = v;
        }, () -> config.overlayShowCoordinates);
        overlayContentGroup.addOption((OptionBuilder)showCoordsOpt);
        BooleanOptionBuilder showBiomeOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_biome"));
        showBiomeOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.show_biome"));
        showBiomeOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.show_biome.tooltip"));
        showBiomeOpt.setImpact(OptionImpact.LOW);
        showBiomeOpt.setDefaultValue(Boolean.valueOf(false));
        showBiomeOpt.setStorageHandler(storage);
        showBiomeOpt.setBinding(v -> {
            config.overlayShowBiome = v;
        }, () -> config.overlayShowBiome);
        overlayContentGroup.addOption((OptionBuilder)showBiomeOpt);
        overlayPage.addOptionGroup(overlayContentGroup);
        mod.addPage((PageBuilder)overlayPage);
        OptionPageBuilder advancedPage = builder.createOptionPage();
        advancedPage.setName((class_2561)class_2561.method_43471((String)"helium.page.advanced"));
        OptionGroupBuilder advancedGroup = builder.createOptionGroup();
        advancedGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.experimental"));
        BooleanOptionBuilder nativeMemOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "native_memory"));
        nativeMemOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.native_memory"));
        nativeMemOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.native_memory.tooltip"));
        nativeMemOpt.setImpact(OptionImpact.MEDIUM);
        nativeMemOpt.setDefaultValue(Boolean.valueOf(true));
        nativeMemOpt.setStorageHandler(storage);
        nativeMemOpt.setBinding(v -> {
            config.nativeMemory = v;
        }, () -> config.nativeMemory);
        advancedGroup.addOption((OptionBuilder)nativeMemOpt);
        IntegerOptionBuilder nativeMemPoolOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "native_memory_pool_mb"));
        nativeMemPoolOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.native_memory_pool_size"));
        nativeMemPoolOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.native_memory_pool_size.tooltip"));
        nativeMemPoolOpt.setImpact(OptionImpact.MEDIUM);
        nativeMemPoolOpt.setDefaultValue(Integer.valueOf(64));
        nativeMemPoolOpt.setRange(16, 256, 16);
        nativeMemPoolOpt.setValueFormatter(v -> class_2561.method_43469((String)"helium.suffix.mb", (Object[])new Object[]{v}));
        nativeMemPoolOpt.setStorageHandler(storage);
        nativeMemPoolOpt.setBinding(v -> {
            config.nativeMemoryPoolMb = v;
        }, () -> config.nativeMemoryPoolMb);
        advancedGroup.addOption((OptionBuilder)nativeMemPoolOpt);
        BooleanOptionBuilder renderPipeOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "render_pipelining"));
        renderPipeOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.render_pipelining"));
        renderPipeOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.render_pipelining.tooltip"));
        renderPipeOpt.setImpact(OptionImpact.HIGH);
        renderPipeOpt.setDefaultValue(Boolean.valueOf(false));
        renderPipeOpt.setStorageHandler(storage);
        renderPipeOpt.setBinding(v -> {
            config.renderPipelining = v;
        }, () -> config.renderPipelining);
        advancedGroup.addOption((OptionBuilder)renderPipeOpt);
        BooleanOptionBuilder simdOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "simd_math"));
        simdOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.simd_math"));
        simdOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.simd_math.tooltip"));
        simdOpt.setImpact(OptionImpact.MEDIUM);
        simdOpt.setDefaultValue(Boolean.valueOf(true));
        simdOpt.setStorageHandler(storage);
        simdOpt.setBinding(v -> {
            config.simdMath = v;
        }, () -> config.simdMath);
        advancedGroup.addOption((OptionBuilder)simdOpt);
        BooleanOptionBuilder asyncLightOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "async_light_updates"));
        asyncLightOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.async_light_updates"));
        asyncLightOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.async_light_updates.tooltip"));
        asyncLightOpt.setImpact(OptionImpact.MEDIUM);
        asyncLightOpt.setDefaultValue(Boolean.valueOf(true));
        asyncLightOpt.setStorageHandler(storage);
        asyncLightOpt.setBinding(v -> {
            config.asyncLightUpdates = v;
        }, () -> config.asyncLightUpdates);
        advancedGroup.addOption((OptionBuilder)asyncLightOpt);
        BooleanOptionBuilder packetBatchOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "packet_batching"));
        packetBatchOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.packet_batching"));
        packetBatchOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.packet_batching.tooltip"));
        packetBatchOpt.setImpact(OptionImpact.LOW);
        packetBatchOpt.setDefaultValue(Boolean.valueOf(true));
        packetBatchOpt.setStorageHandler(storage);
        packetBatchOpt.setBinding(v -> {
            config.packetBatching = v;
        }, () -> config.packetBatching);
        advancedGroup.addOption((OptionBuilder)packetBatchOpt);
        BooleanOptionBuilder temporalOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "temporal_reprojection"));
        temporalOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.temporal_reprojection"));
        temporalOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.temporal_reprojection.tooltip"));
        temporalOpt.setImpact(OptionImpact.HIGH);
        temporalOpt.setDefaultValue(Boolean.valueOf(false));
        temporalOpt.setStorageHandler(storage);
        temporalOpt.setBinding(v -> {
            config.temporalReprojection = v;
        }, () -> config.temporalReprojection);
        advancedGroup.addOption((OptionBuilder)temporalOpt);
        advancedPage.addOptionGroup(advancedGroup);
        OptionGroupBuilder gpuGroup = builder.createOptionGroup();
        gpuGroup.setName((class_2561)class_2561.method_43471((String)"helium.group.gpu_specific"));
        BooleanOptionBuilder nvidiaOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "nvidia_optimizations"));
        nvidiaOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.nvidia_optimizations"));
        nvidiaOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.nvidia_optimizations.tooltip"));
        nvidiaOpt.setImpact(OptionImpact.MEDIUM);
        nvidiaOpt.setDefaultValue(Boolean.valueOf(true));
        nvidiaOpt.setStorageHandler(storage);
        nvidiaOpt.setEnabled(!GpuDetector.isInitialized() || GpuDetector.isNvidia());
        nvidiaOpt.setBinding(v -> {
            config.nvidiaOptimizations = v;
        }, () -> config.nvidiaOptimizations);
        gpuGroup.addOption((OptionBuilder)nvidiaOpt);
        BooleanOptionBuilder amdOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "amd_optimizations"));
        amdOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.amd_optimizations"));
        amdOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.amd_optimizations.tooltip"));
        amdOpt.setImpact(OptionImpact.MEDIUM);
        amdOpt.setDefaultValue(Boolean.valueOf(true));
        amdOpt.setStorageHandler(storage);
        amdOpt.setEnabled(!GpuDetector.isInitialized() || GpuDetector.isAmd());
        amdOpt.setBinding(v -> {
            config.amdOptimizations = v;
        }, () -> config.amdOptimizations);
        gpuGroup.addOption((OptionBuilder)amdOpt);
        BooleanOptionBuilder intelOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "intel_optimizations"));
        intelOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.intel_optimizations"));
        intelOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.intel_optimizations.tooltip"));
        intelOpt.setImpact(OptionImpact.MEDIUM);
        intelOpt.setDefaultValue(Boolean.valueOf(true));
        intelOpt.setStorageHandler(storage);
        intelOpt.setEnabled(!GpuDetector.isInitialized() || GpuDetector.isIntel());
        intelOpt.setBinding(v -> {
            config.intelOptimizations = v;
        }, () -> config.intelOptimizations);
        gpuGroup.addOption((OptionBuilder)intelOpt);
        BooleanOptionBuilder adaptiveSyncOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "adaptive_sync"));
        adaptiveSyncOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.adaptive_sync"));
        adaptiveSyncOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.adaptive_sync.tooltip"));
        adaptiveSyncOpt.setImpact(OptionImpact.LOW);
        adaptiveSyncOpt.setDefaultValue(Boolean.valueOf(false));
        adaptiveSyncOpt.setStorageHandler(storage);
        adaptiveSyncOpt.setBinding(v -> {
            config.adaptiveSync = v;
        }, () -> config.adaptiveSync);
        gpuGroup.addOption((OptionBuilder)adaptiveSyncOpt);
        IntegerOptionBuilder displaySyncOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "display_sync_refresh_rate"));
        displaySyncOpt.setName((class_2561)class_2561.method_43471((String)"helium.option.display_sync_optimization"));
        displaySyncOpt.setTooltip((class_2561)class_2561.method_43471((String)"helium.option.display_sync_optimization.tooltip"));
        displaySyncOpt.setImpact(OptionImpact.VARIES);
        displaySyncOpt.setDefaultValue(Integer.valueOf(9));
        displaySyncOpt.setRange(0, 9, 1);
        displaySyncOpt.setValueFormatter(v -> {
            int val = DISPLAY_SYNC_HZ[Math.min(v, DISPLAY_SYNC_HZ.length - 1)];
            if (val == 0) {
                return class_2561.method_43471((String)"helium.option.display_sync_optimization.off");
            }
            if (val == -1) {
                return class_2561.method_43471((String)"helium.option.display_sync_optimization.auto");
            }
            return class_2561.method_30163((String)(val + " Hz"));
        });
        displaySyncOpt.setStorageHandler(storage);
        displaySyncOpt.setBinding(v -> {
            config.displaySyncRefreshRate = DISPLAY_SYNC_HZ[Math.min(v, DISPLAY_SYNC_HZ.length - 1)];
            DisplaySyncOptimizer.reset();
        }, () -> {
            int val = config.displaySyncRefreshRate;
            for (int i = 0; i < DISPLAY_SYNC_HZ.length; ++i) {
                if (DISPLAY_SYNC_HZ[i] != val) continue;
                return i;
            }
            return 9;
        });
        gpuGroup.addOption((OptionBuilder)displaySyncOpt);
        advancedPage.addOptionGroup(gpuGroup);
        mod.addPage((PageBuilder)advancedPage);
    }
}

