/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.threading;

import com.helium.HeliumClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ThreadPriorityManager {
    private ThreadPriorityManager() {
    }

    public static void init() {
        try {
            Thread initThread = Thread.currentThread();
            initThread.setPriority(10);
            HeliumClient.LOGGER.info("init thread priority set to {}", (Object)initThread.getPriority());
        }
        catch (SecurityException e) {
            HeliumClient.LOGGER.warn("failed to set init thread priority", (Throwable)e);
        }
    }

    public static void initRenderThread() {
        try {
            Thread renderThread = Thread.currentThread();
            renderThread.setPriority(10);
            HeliumClient.LOGGER.info("render thread priority set to {}", (Object)renderThread.getPriority());
        }
        catch (SecurityException e) {
            HeliumClient.LOGGER.warn("failed to set render thread priority", (Throwable)e);
        }
    }

    public static Thread createWorker(String name, Runnable task) {
        Thread thread = new Thread(task, "helium-" + name);
        thread.setDaemon(true);
        thread.setPriority(4);
        return thread;
    }
}

