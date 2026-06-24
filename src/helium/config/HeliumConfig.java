/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.helium.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.helium.HeliumClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(value=EnvType.CLIENT)
public class HeliumConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("helium.json");
    public boolean modEnabled = true;
    public boolean fastMath = true;
    public boolean glStateCache = false;
    public boolean memoryOptimizations = true;
    public boolean threadOptimizations = true;
    public boolean networkOptimizations = true;
    public boolean fastStartup = true;
    public boolean entityCulling = true;
    public int entityCullDistance = 64;
    public boolean blockEntityCulling = true;
    public int blockEntityCullDistance = 48;
    public boolean particleCulling = true;
    public int particleCullDistance = 32;
    public boolean particleLimiting = true;
    public int maxParticles = 1000;
    public boolean particlePriority = true;
    public boolean particleBatching = true;
    public boolean animationThrottling = true;
    public boolean smoothScrolling = true;
    public boolean windowStyle = true;
    public String windowMaterial = "TABBED";
    public String windowCorner = "ROUND";
    public boolean fastServerPing = true;
    public boolean preserveScrollOnRefresh = true;
    public boolean directConnectPreview = true;
    public boolean fpsOverlay = true;
    public boolean overlayShowFps = true;
    public boolean overlayShowFpsMinMaxAvg = false;
    public boolean overlayShowMemory = false;
    public boolean overlayShowParticles = false;
    public boolean overlayShowCoordinates = false;
    public boolean overlayShowBiome = false;
    public String overlayPosition = "TOP_LEFT";
    public int overlayTransparency = 60;
    public String overlayBackgroundColor = "#000000";
    public String overlayTextColor = "#FFFFFF";
    public boolean nativeMemory = true;
    public int nativeMemoryPoolMb = 64;
    public boolean renderPipelining = false;
    public boolean modelCache = true;
    public int modelCacheMaxMb = 64;
    public boolean reducedAllocations = true;
    public boolean simdMath = true;
    public boolean asyncLightUpdates = true;
    public boolean packetBatching = true;
    public boolean autoPauseOnIdle = false;
    public int idleTimeoutSeconds = 60;
    public int idleFpsLimit = 5;
    public boolean nvidiaOptimizations = true;
    public boolean amdOptimizations = true;
    public boolean intelOptimizations = true;
    public boolean adaptiveSync = false;
    public int displaySyncRefreshRate = -1;
    public boolean temporalReprojection = false;
    public boolean fullbright = false;
    public boolean fastAnimations = false;
    public boolean cachedEnumValues = true;
    public boolean fastWorldLoading = false;
    public boolean fastIpPing = true;
    public boolean devMode = true;

    public static HeliumConfig load() {
        if (Files.exists(CONFIG_PATH, new LinkOption[0])) {
            try {
                String json = Files.readString(CONFIG_PATH);
                HeliumConfig cfg = (HeliumConfig)GSON.fromJson(json, HeliumConfig.class);
                if (cfg != null) {
                    cfg.save();
                    return cfg;
                }
            }
            catch (IOException e) {
                HeliumClient.LOGGER.warn("failed to load config, using defaults", (Throwable)e);
            }
        }
        HeliumConfig cfg = new HeliumConfig();
        cfg.save();
        return cfg;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent(), new FileAttribute[0]);
            Files.writeString(CONFIG_PATH, (CharSequence)GSON.toJson((Object)this), new OpenOption[0]);
        }
        catch (IOException e) {
            HeliumClient.LOGGER.warn("failed to save config", (Throwable)e);
        }
    }

    public boolean exportToFile(Path path) {
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Files.writeString(path, (CharSequence)GSON.toJson((Object)this), new OpenOption[0]);
            HeliumClient.LOGGER.info("config exported to {}", (Object)path);
            return true;
        }
        catch (IOException e) {
            HeliumClient.LOGGER.warn("failed to export config to {}", (Object)path, (Object)e);
            return false;
        }
    }

    public static HeliumConfig importFromFile(Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            HeliumClient.LOGGER.warn("import file does not exist: {}", (Object)path);
            return null;
        }
        try {
            String json = Files.readString(path);
            HeliumConfig imported = (HeliumConfig)GSON.fromJson(json, HeliumConfig.class);
            if (imported != null) {
                HeliumClient.LOGGER.info("config imported from {}", (Object)path);
                return imported;
            }
        }
        catch (IOException e) {
            HeliumClient.LOGGER.warn("failed to import config from {}", (Object)path, (Object)e);
        }
        return null;
    }

    public void copyFrom(HeliumConfig other) {
        if (other == null) {
            return;
        }
        this.modEnabled = other.modEnabled;
        this.fastMath = other.fastMath;
        this.glStateCache = other.glStateCache;
        this.memoryOptimizations = other.memoryOptimizations;
        this.threadOptimizations = other.threadOptimizations;
        this.networkOptimizations = other.networkOptimizations;
        this.fastStartup = other.fastStartup;
        this.entityCulling = other.entityCulling;
        this.entityCullDistance = other.entityCullDistance;
        this.blockEntityCulling = other.blockEntityCulling;
        this.blockEntityCullDistance = other.blockEntityCullDistance;
        this.particleCulling = other.particleCulling;
        this.particleCullDistance = other.particleCullDistance;
        this.particleLimiting = other.particleLimiting;
        this.maxParticles = other.maxParticles;
        this.particlePriority = other.particlePriority;
        this.particleBatching = other.particleBatching;
        this.animationThrottling = other.animationThrottling;
        this.fastServerPing = other.fastServerPing;
        this.preserveScrollOnRefresh = other.preserveScrollOnRefresh;
        this.directConnectPreview = other.directConnectPreview;
        this.fpsOverlay = other.fpsOverlay;
        this.overlayShowFps = other.overlayShowFps;
        this.overlayShowFpsMinMaxAvg = other.overlayShowFpsMinMaxAvg;
        this.overlayShowMemory = other.overlayShowMemory;
        this.overlayShowParticles = other.overlayShowParticles;
        this.overlayPosition = other.overlayPosition;
        this.overlayTransparency = other.overlayTransparency;
        this.overlayBackgroundColor = other.overlayBackgroundColor;
        this.overlayTextColor = other.overlayTextColor;
        this.nativeMemory = other.nativeMemory;
        this.nativeMemoryPoolMb = other.nativeMemoryPoolMb;
        this.renderPipelining = other.renderPipelining;
        this.modelCache = other.modelCache;
        this.modelCacheMaxMb = other.modelCacheMaxMb;
        this.reducedAllocations = other.reducedAllocations;
        this.simdMath = other.simdMath;
        this.asyncLightUpdates = other.asyncLightUpdates;
        this.packetBatching = other.packetBatching;
        this.autoPauseOnIdle = other.autoPauseOnIdle;
        this.idleTimeoutSeconds = other.idleTimeoutSeconds;
        this.idleFpsLimit = other.idleFpsLimit;
        this.nvidiaOptimizations = other.nvidiaOptimizations;
        this.amdOptimizations = other.amdOptimizations;
        this.intelOptimizations = other.intelOptimizations;
        this.adaptiveSync = other.adaptiveSync;
        this.displaySyncRefreshRate = other.displaySyncRefreshRate;
        this.temporalReprojection = other.temporalReprojection;
        this.fullbright = other.fullbright;
        this.fastAnimations = other.fastAnimations;
        this.cachedEnumValues = other.cachedEnumValues;
        this.fastWorldLoading = other.fastWorldLoading;
        this.fastIpPing = other.fastIpPing;
        this.overlayShowCoordinates = other.overlayShowCoordinates;
        this.overlayShowBiome = other.overlayShowBiome;
        this.devMode = other.devMode;
        this.smoothScrolling = other.smoothScrolling;
        this.windowStyle = other.windowStyle;
        this.windowMaterial = other.windowMaterial;
        this.windowCorner = other.windowCorner;
    }
}

