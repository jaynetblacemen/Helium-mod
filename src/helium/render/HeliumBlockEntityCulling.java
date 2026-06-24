/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.blockentity.BlockEntityRenderHandler
 *  net.caffeinemc.mods.sodium.api.blockentity.BlockEntityRenderPredicate
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2382
 *  net.minecraft.class_2591
 *  net.minecraft.class_310
 */
package com.helium.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.caffeinemc.mods.sodium.api.blockentity.BlockEntityRenderHandler;
import net.caffeinemc.mods.sodium.api.blockentity.BlockEntityRenderPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2382;
import net.minecraft.class_2591;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class HeliumBlockEntityCulling {
    private static volatile boolean registered = false;

    private HeliumBlockEntityCulling() {
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        BlockEntityRenderPredicate predicate = (world, pos, entity) -> {
            double maxDist;
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.blockEntityCulling) {
                return true;
            }
            class_310 client = class_310.method_1551();
            if (client.field_1724 == null) {
                return true;
            }
            double dist = client.field_1724.method_24515().method_10262((class_2382)pos);
            return dist <= (maxDist = (double)(config.blockEntityCullDistance * config.blockEntityCullDistance));
        };
        try {
            BlockEntityRenderHandler handler = BlockEntityRenderHandler.instance();
            handler.addRenderPredicate(class_2591.field_11914, predicate);
            handler.addRenderPredicate(class_2591.field_11911, predicate);
            handler.addRenderPredicate(class_2591.field_40330, predicate);
            handler.addRenderPredicate(class_2591.field_11905, predicate);
            handler.addRenderPredicate(class_2591.field_16413, predicate);
            handler.addRenderPredicate(class_2591.field_17380, predicate);
            handler.addRenderPredicate(class_2591.field_11912, predicate);
            handler.addRenderPredicate(class_2591.field_11898, predicate);
            handler.addRenderPredicate(class_2591.field_11906, predicate);
            handler.addRenderPredicate(class_2591.field_42781, predicate);
            handler.addRenderPredicate(class_2591.field_11910, predicate);
            handler.addRenderPredicate(class_2591.field_11896, predicate);
            handler.addRenderPredicate(class_2591.field_11913, predicate);
            handler.addRenderPredicate(class_2591.field_11902, predicate);
            HeliumClient.LOGGER.info("block entity culling registered via sodium api");
        }
        catch (Exception e) {
            HeliumClient.LOGGER.warn("failed to register block entity culling", (Throwable)e);
        }
    }
}

