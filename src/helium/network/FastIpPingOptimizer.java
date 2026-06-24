/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.network;

import com.helium.HeliumClient;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import sun.misc.Unsafe;

@Environment(value=EnvType.CLIENT)
public final class FastIpPingOptimizer {
    private static volatile boolean initialized = false;
    private static boolean useUnsafe = false;
    private static Field holderField;
    private static Field hostNameField;
    private static Unsafe unsafeInstance;
    private static long holderFieldOffset;
    private static long hostNameFieldOffset;

    private FastIpPingOptimizer() {
    }

    public static void init() {
        try {
            holderField = InetAddress.class.getDeclaredField("holder");
            holderField.setAccessible(true);
            Object testHolder = holderField.get(InetAddress.getLoopbackAddress());
            hostNameField = testHolder.getClass().getDeclaredField("hostName");
            hostNameField.setAccessible(true);
            initialized = true;
            HeliumClient.LOGGER.debug("fast ip ping initialized via reflection");
        }
        catch (Throwable t) {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                unsafeInstance = (Unsafe)f.get(null);
                Field hf = InetAddress.class.getDeclaredField("holder");
                holderFieldOffset = unsafeInstance.objectFieldOffset(hf);
                Object testHolder = unsafeInstance.getObject(InetAddress.getLoopbackAddress(), holderFieldOffset);
                Field hnf = testHolder.getClass().getDeclaredField("hostName");
                hostNameFieldOffset = unsafeInstance.objectFieldOffset(hnf);
                useUnsafe = true;
                initialized = true;
                HeliumClient.LOGGER.debug("fast ip ping initialized via unsafe");
            }
            catch (Throwable t2) {
                HeliumClient.LOGGER.warn("fast ip ping unavailable - both reflection and unsafe failed");
            }
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void patchAddress(InetSocketAddress socketAddr) {
        if (!initialized || socketAddr == null) {
            return;
        }
        InetAddress addr = socketAddr.getAddress();
        if (addr == null) {
            return;
        }
        try {
            if (useUnsafe) {
                Object holder = unsafeInstance.getObject(addr, holderFieldOffset);
                if (holder != null && unsafeInstance.getObject(holder, hostNameFieldOffset) == null) {
                    unsafeInstance.putObject(holder, hostNameFieldOffset, addr.getHostAddress());
                }
            } else {
                Object holder = holderField.get(addr);
                if (holder != null && hostNameField.get(holder) == null) {
                    hostNameField.set(holder, addr.getHostAddress());
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

