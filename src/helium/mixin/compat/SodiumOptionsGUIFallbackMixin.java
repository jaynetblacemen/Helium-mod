/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.compat;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(targets={"net/caffeinemc/mods/sodium/client/gui/SodiumOptionsGUI"})
public abstract class SodiumOptionsGUIFallbackMixin
extends class_437 {
    protected SodiumOptionsGUIFallbackMixin(class_2561 title) {
        super(title);
    }

    @Inject(method={"rebuildGUI"}, at={@At(value="TAIL")}, require=0, remap=false)
    private void helium$addConfigButton(CallbackInfo ci) {
        try {
            this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_30163((String)"Helium"), button -> {
                if (this.field_22787 != null) {
                    this.field_22787.method_1507(HeliumConfigScreen.create(this));
                }
            }).method_46434(this.field_22789 - 86, 6, 80, 20).method_46431());
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.warn("failed to add helium button to sodium gui", t);
        }
    }
}

