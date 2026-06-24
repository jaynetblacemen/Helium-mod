/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_6368
 *  net.minecraft.class_6370
 *  net.minecraft.class_639
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.helium.mixin.network;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.network.FastIpPingOptimizer;
import java.net.InetSocketAddress;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_6368;
import net.minecraft.class_6370;
import net.minecraft.class_639;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_6370.class})
public abstract class AllowedAddressResolverMixin {
    @Inject(method={"method_36907"}, at={@At(value="RETURN")}, require=0)
    private void helium$patchReverseDns(class_639 address, CallbackInfoReturnable<Optional<class_6368>> cir) {
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastIpPing) {
                return;
            }
            if (!FastIpPingOptimizer.isInitialized()) {
                return;
            }
            Optional result = (Optional)cir.getReturnValue();
            if (result == null || result.isEmpty()) {
                return;
            }
            InetSocketAddress socketAddr = ((class_6368)result.get()).method_36902();
            FastIpPingOptimizer.patchAddress(socketAddr);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

