/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1588
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_4604
 *  net.minecraft.class_897
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.TemporalReprojection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1588;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_4604;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_897.class})
public abstract class EntityCullingMixin<T extends class_1297> {
    @Unique
    private static boolean helium$frustumFailed = false;

    @Inject(method={"method_3933"}, at={@At(value="HEAD")}, cancellable=true)
    private void helium$cullDistantEntities(T entity, class_4604 frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null) {
            return;
        }
        if (entity instanceof class_1657) {
            return;
        }
        double dx = entity.method_23317() - client.field_1724.method_23317();
        double dy = entity.method_23318() - client.field_1724.method_23318();
        double dz = entity.method_23321() - client.field_1724.method_23321();
        double distSq = dx * dx + dy * dy + dz * dz;
        if (config.entityCulling) {
            double maxDist = config.entityCullDistance * config.entityCullDistance;
            if (distSq > maxDist) {
                cir.setReturnValue((Object)false);
                return;
            }
            if (!helium$frustumFailed) {
                try {
                    float yaw = client.field_1724.method_36454();
                    float yawRad = (float)Math.toRadians(yaw);
                    double forwardX = -Math.sin(yawRad);
                    double forwardZ = Math.cos(yawRad);
                    double dot = dx * forwardX + dz * forwardZ;
                    if (dot < -16.0 && distSq > 256.0) {
                        cir.setReturnValue((Object)false);
                        return;
                    }
                }
                catch (Throwable t) {
                    helium$frustumFailed = true;
                }
            }
        }
        if (config.temporalReprojection && TemporalReprojection.isInitialized() && !(entity instanceof class_1588) && TemporalReprojection.shouldSkipEntity(distSq)) {
            cir.setReturnValue((Object)false);
        }
    }
}

