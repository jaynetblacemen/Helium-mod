/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  org.objectweb.asm.tree.ClassNode
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package com.helium.mixin;

import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

@Environment(value=EnvType.CLIENT)
public class HeliumMixinPlugin
implements IMixinConfigPlugin {
    private boolean hasOpenGlStateManager = false;
    private boolean hasPlatformGlStateManager = false;
    private boolean hasImmediatelyFast = false;
    private boolean hasModernSodiumApi = false;

    public void onLoad(String mixinPackage) {
        this.hasOpenGlStateManager = this.classExistsOnClasspath("com/mojang/blaze3d/opengl/GlStateManager.class");
        this.hasPlatformGlStateManager = this.classExistsOnClasspath("com/mojang/blaze3d/platform/GlStateManager.class");
        this.hasImmediatelyFast = FabricLoader.getInstance().isModLoaded("immediatelyfast");
        this.hasModernSodiumApi = this.classExistsOnClasspath("net/caffeinemc/mods/sodium/api/config/ConfigEntryPoint.class");
    }

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("GlStateManagerMixin")) {
            return this.hasOpenGlStateManager && !this.hasImmediatelyFast;
        }
        if (mixinClassName.endsWith("GlStateManagerLegacyMixin")) {
            return this.hasPlatformGlStateManager && !this.hasImmediatelyFast;
        }
        if (mixinClassName.endsWith("SodiumOptionsGUILegacyMixin")) {
            return !this.hasModernSodiumApi;
        }
        return true;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private boolean classExistsOnClasspath(String resourcePath) {
        return this.getClass().getClassLoader().getResource(resourcePath) != null;
    }
}

