/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2350
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.EnumValueCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_2350.class})
public abstract class DirectionValuesMixin {
    @Unique
    private static boolean helium$failed = false;

    @Inject(method={"values"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void helium$cachedValues(CallbackInfoReturnable<class_2350[]> cir) {
        if (helium$failed) {
            return;
        }
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.cachedEnumValues) {
                return;
            }
            if (!EnumValueCache.isInitialized()) {
                return;
            }
            class_2350[] cached = EnumValueCache.getDirections();
            if (cached != null) {
                cir.setReturnValue((Object)cached);
            }
        }
        catch (Throwable t) {
            helium$failed = true;
        }
    }
}

