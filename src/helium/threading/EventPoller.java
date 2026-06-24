/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.threading;

import com.helium.HeliumClient;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class EventPoller {
    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static final AtomicLong lastPollTime = new AtomicLong(0L);
    private static long pollIntervalNanos = 1000000L;

    private EventPoller() {
    }

    public static void init(int targetPollRateHz) {
        pollIntervalNanos = 1000000000L / (long)Math.max(1, targetPollRateHz);
        running.set(true);
        HeliumClient.LOGGER.info("event poller initialized at {}hz", (Object)targetPollRateHz);
    }

    public static boolean shouldPoll() {
        long last;
        if (!running.get()) {
            return true;
        }
        long now = System.nanoTime();
        if (now - (last = lastPollTime.get()) >= pollIntervalNanos) {
            lastPollTime.set(now);
            return true;
        }
        return false;
    }

    public static void shutdown() {
        running.set(false);
    }
}

