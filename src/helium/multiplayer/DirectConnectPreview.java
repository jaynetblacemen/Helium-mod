/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12239
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_642
 *  net.minecraft.class_642$class_8678
 *  net.minecraft.class_642$class_9083
 *  net.minecraft.class_644
 */
package com.helium.multiplayer;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.multiplayer.ServerPreviewUpdater;
import java.net.UnknownHostException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12239;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_642;
import net.minecraft.class_644;

@Environment(value=EnvType.CLIENT)
public final class DirectConnectPreview {
    private static String _lastAddress = "";
    private static long _lastPingTime = 0L;
    private static final long DEBOUNCE_MS = 500L;

    private DirectConnectPreview() {
    }

    public static void onAddressChanged(String address) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.directConnectPreview) {
            return;
        }
        if (address == null || address.isBlank()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (address.equals(_lastAddress) && now - _lastPingTime < 500L) {
            return;
        }
        _lastAddress = address;
        _lastPingTime = now;
        Thread.startVirtualThread(() -> DirectConnectPreview.pingServer(address));
    }

    private static void pingServer(String address) {
        class_642 info = new class_642(address, address, class_642.class_8678.field_45611);
        class_644 pinger = new class_644();
        try {
            pinger.method_3003(info, () -> {}, () -> DirectConnectPreview.dispatchResult(info), class_12239.method_75867((boolean)true));
        }
        catch (UnknownHostException e) {
            info.field_3757 = class_2561.method_43470((String)"Unknown host");
            info.field_3753 = class_2561.method_43470((String)"0/0");
            info.method_55824(class_642.class_9083.field_47882);
            DirectConnectPreview.dispatchResult(info);
        }
    }

    private static void dispatchResult(class_642 info) {
        if (info == null) {
            return;
        }
        class_310 client = class_310.method_1551();
        client.execute(() -> {
            class_437 screen = client.field_1755;
            if (screen instanceof ServerPreviewUpdater) {
                String[] stringArray;
                ServerPreviewUpdater updater = (ServerPreviewUpdater)screen;
                if (info.field_3757 != null) {
                    updater.helium$setMotdText(info.field_3757);
                } else {
                    updater.helium$setMotdText((class_2561)class_2561.method_43473());
                }
                if (info.field_3757 != null) {
                    stringArray = info.field_3757.getString().split("\n");
                } else {
                    String[] stringArray2 = new String[1];
                    stringArray = stringArray2;
                    stringArray2[0] = "";
                }
                String[] motdLines = stringArray;
                updater.helium$updateServerData(info.field_3752 != null ? info.field_3752 : "", motdLines, info.field_3753 != null ? info.field_3753.getString() : "0/0", info.field_3758);
                updater.helium$updateFavicon(info.method_49306());
            }
        });
    }
}

