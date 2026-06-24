/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 *  net.minecraft.class_7528
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.helium.mixin.ui;

import com.helium.ui.ScrollMath;
import com.helium.ui.ScrollableWidgetManipulator;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import net.minecraft.class_7528;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_7528.class})
public abstract class ScrollableWidgetMixin
implements ScrollableWidgetManipulator {
    @Shadow
    private double field_39497;
    @Unique
    private double helium$animationTimer = 0.0;
    @Unique
    private double helium$scrollStartVelocity = 0.0;
    @Unique
    private boolean helium$renderSmooth = false;

    @Shadow
    public abstract int method_44390();

    @Shadow
    public abstract void method_44382(double var1);

    @Override
    public void helium$manipulateScrollAmount(float delta) {
        if (!ScrollMath.isEnabled()) {
            return;
        }
        this.helium$renderSmooth = true;
        this.helium$checkOutOfBounds(delta);
        if (Math.abs(ScrollMath.scrollbarVelocity(this.helium$animationTimer, this.helium$scrollStartVelocity)) < 1.0) {
            return;
        }
        this.helium$applyMotion(delta);
    }

    @Unique
    private void helium$applyMotion(float delta) {
        this.method_44382(this.field_39497 + ScrollMath.scrollbarVelocity(this.helium$animationTimer, this.helium$scrollStartVelocity) * (double)delta);
        this.helium$animationTimer += (double)(delta * 10.0f) / ScrollMath.getAnimationDuration();
    }

    @Unique
    private void helium$checkOutOfBounds(float delta) {
        if (this.field_39497 < 0.0) {
            this.method_44382(this.field_39497 + ScrollMath.pushBackStrength(Math.abs(this.field_39497), delta));
            if (this.field_39497 > -0.2) {
                this.field_39497 = 0.0;
            }
        }
        if (this.field_39497 > (double)this.method_44390()) {
            this.method_44382(this.field_39497 - ScrollMath.pushBackStrength(this.field_39497 - (double)this.method_44390(), delta));
            if (this.field_39497 < (double)this.method_44390() + 0.2) {
                this.field_39497 = this.method_44390();
            }
        }
    }

    @WrapOperation(method={"method_25401"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_7528;method_44382(D)V")}, require=0)
    private void helium$captureVelocity(class_7528 instance, double targetScrollY, Operation<Void> original) {
        if (!ScrollMath.isEnabled() || !this.helium$renderSmooth) {
            original.call(new Object[]{instance, targetScrollY});
            return;
        }
        double diff = targetScrollY - this.field_39497;
        diff = Math.signum(diff) * Math.min(Math.abs(diff), 10.0);
        if (Math.signum(diff *= ScrollMath.getScrollSpeed()) != Math.signum(this.helium$scrollStartVelocity)) {
            diff *= 2.5;
        }
        this.helium$animationTimer *= 0.5;
        this.helium$scrollStartVelocity = ScrollMath.scrollbarVelocity(this.helium$animationTimer, this.helium$scrollStartVelocity) + diff;
        this.helium$animationTimer = 0.0;
    }

    @WrapOperation(method={"method_25403"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_7528;method_44382(D)V")}, require=0)
    private void helium$clampDraggedScrollY(class_7528 instance, double targetScrollY, Operation<Void> original) {
        original.call(new Object[]{instance, class_3532.method_15350((double)targetScrollY, (double)0.0, (double)this.method_44390())});
    }

    @WrapMethod(method={"method_44382"}, require=0)
    private void helium$setScrollYUnclamped(double targetScrollY, Operation<Void> original) {
        if (!ScrollMath.isEnabled() || !this.helium$renderSmooth) {
            original.call(new Object[]{targetScrollY});
            return;
        }
        if (targetScrollY > (double)this.method_44390() + 100000.0 || targetScrollY < -100000.0) {
            original.call(new Object[]{targetScrollY});
        } else {
            this.field_39497 = targetScrollY;
        }
    }
}

