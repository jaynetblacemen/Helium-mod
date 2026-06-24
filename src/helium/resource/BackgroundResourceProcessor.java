/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.resource;

import com.helium.HeliumClient;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class BackgroundResourceProcessor {
    private static ExecutorService executor;
    private static boolean initialized;

    private BackgroundResourceProcessor() {
    }

    public static void init() {
        int threads = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        AtomicInteger counter = new AtomicInteger(0);
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "helium-resource-" + counter.getAndIncrement());
            t.setDaemon(true);
            t.setPriority(3);
            return t;
        };
        executor = Executors.newFixedThreadPool(threads, factory);
        initialized = true;
        HeliumClient.LOGGER.info("background resource processor initialized with {} threads", (Object)threads);
    }

    public static <T> CompletableFuture<T> submitAsync(Supplier<T> task) {
        if (!initialized) {
            BackgroundResourceProcessor.init();
        }
        return CompletableFuture.supplyAsync(task, executor);
    }

    public static CompletableFuture<Void> submitAsync(Runnable task) {
        if (!initialized) {
            BackgroundResourceProcessor.init();
        }
        return CompletableFuture.runAsync(task, executor);
    }

    public static void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    static {
        initialized = false;
    }
}

