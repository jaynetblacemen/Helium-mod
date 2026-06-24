/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4587
 *  net.minecraft.class_630
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.FastAnimationOptimizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_630;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_630.class})
public abstract class ModelPartAnimationMixin {
    @Shadow
    public float field_3654;
    @Shadow
    public float field_3675;
    @Shadow
    public float field_3674;
    @Shadow
    public float field_37938;
    @Shadow
    public float field_37939;
    @Shadow
    public float field_37940;
    @Shadow
    public float field_3657;
    @Shadow
    public float field_3656;
    @Shadow
    public float field_3655;
    @Unique
    private final Quaternionf helium$reusedQuat = new Quaternionf();

    @Inject(method={"method_22703"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void helium$fastApplyTransform(class_4587 matrices, CallbackInfo ci) {
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastAnimations) {
                return;
            }
            if (!FastAnimationOptimizer.isInitialized()) {
                return;
            }
            matrices.method_46416(this.field_3657 / 16.0f, this.field_3656 / 16.0f, this.field_3655 / 16.0f);
            if (this.field_3654 != 0.0f || this.field_3675 != 0.0f || this.field_3674 != 0.0f) {
                matrices.method_22907((Quaternionfc)this.helium$reusedQuat.rotationZYX(this.field_3674, this.field_3675, this.field_3654));
            }
            if (this.field_37938 != 1.0f || this.field_37939 != 1.0f || this.field_37940 != 1.0f) {
                matrices.method_22905(this.field_37938, this.field_37939, this.field_37940);
            }
            ci.cancel();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

