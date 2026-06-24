/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_4267
 *  net.minecraft.class_437
 *  net.minecraft.class_500
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.helium.mixin.multiplayer;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.mixin.multiplayer.MultiplayerScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_4267;
import net.minecraft.class_437;
import net.minecraft.class_500;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_500.class})
public abstract class MultiplayerScreenMixin {
    @Shadow
    protected class_4267 field_3043;

    @Redirect(method={"method_2534"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_310;method_1507(Lnet/minecraft/class_437;)V"))
    private void helium$preserveScrollOnRefresh(class_310 client, class_437 newScreen) {
        HeliumConfig config = HeliumClient.getConfig();
        if (!(config != null && config.modEnabled && config.preserveScrollOnRefresh && newScreen instanceof class_500 && this.field_3043 != null)) {
            client.method_1507(newScreen);
            return;
        }
        double scrollY = this.field_3043.method_44387();
        client.method_1507(newScreen);
        class_4267 newWidget = ((MultiplayerScreenAccessor)newScreen).helium$getServerListWidget();
        if (newWidget != null) {
            newWidget.method_44382(scrollY);
        }
    }
}

