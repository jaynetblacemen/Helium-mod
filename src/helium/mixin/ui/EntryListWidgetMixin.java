/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 *  net.minecraft.class_350
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.ui;

import com.helium.ui.ScrollMath;
import com.helium.ui.ScrollableWidgetManipulator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_350.class})
public abstract class EntryListWidgetMixin {
    @Inject(method={"method_48579"}, at={@At(value="HEAD")}, require=0)
    private void helium$manipulateScrollAmount(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ScrollMath.isEnabled()) {
            return;
        }
        EntryListWidgetMixin entryListWidgetMixin = this;
        if (entryListWidgetMixin instanceof ScrollableWidgetManipulator) {
            ScrollableWidgetManipulator manipulator = (ScrollableWidgetManipulator)((Object)entryListWidgetMixin);
            manipulator.helium$manipulateScrollAmount(delta);
        }
    }
}

