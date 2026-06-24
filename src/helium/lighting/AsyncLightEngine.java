/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.lighting;

import com.helium.HeliumClient;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class AsyncLightEngine {
    private static ExecutorService executor;
    private static final AtomicBoolean initialized;
    private static final ConcurrentLinkedQueue<LightUpdate> pendingUpdates;
    private static final ConcurrentLinkedQueue<LightUpdate> completedUpdates;
    private static final AtomicInteger queuedCount;
    private static final int MAX_QUEUED = 1024;
    private static final int MAX_APPLY_PER_TICK = 64;
    private static final AtomicInteger batchesThisFrame;
    private static final AtomicInteger deferredCount;
    private static volatile int maxBatchesPerFrame;

    private AsyncLightEngine() {
    }

    public static void init() {
        if (initialized.getAndSet(true)) {
            return;
        }
        AtomicInteger counter = new AtomicInteger(0);
        executor = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() / 4), r -> {
            Thread t = new Thread(r, "helium-light-" + counter.getAndIncrement());
            t.setDaemon(true);
            t.setPriority(3);
            return t;
        });
        HeliumClient.LOGGER.info("async light engine initialized");
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static void onLightUpdateBatch() {
        if (!initialized.get()) {
            return;
        }
        batchesThisFrame.incrementAndGet();
    }

    public static boolean isThrottling() {
        return initialized.get() && batchesThisFrame.get() > maxBatchesPerFrame;
    }

    public static void resetFrameCounters() {
        int batches = batchesThisFrame.getAndSet(0);
        if (batches > maxBatchesPerFrame) {
            deferredCount.addAndGet(batches - maxBatchesPerFrame);
        }
    }

    public static boolean canAcceptUpdate() {
        return initialized.get() && queuedCount.get() < 1024;
    }

    public static void trackUpdate() {
        if (!initialized.get()) {
            return;
        }
        queuedCount.incrementAndGet();
    }

    public static void onUpdateApplied() {
        if (!initialized.get()) {
            return;
        }
        queuedCount.updateAndGet(c -> Math.max(0, c - 1));
    }

    public static int applyCompleted() {
        LightUpdate update;
        int applied;
        if (!initialized.get()) {
            return 0;
        }
        AsyncLightEngine.resetFrameCounters();
        for (applied = 0; applied < 64 && (update = completedUpdates.poll()) != null; ++applied) {
            queuedCount.decrementAndGet();
        }
        if (deferredCount.get() > 0) {
            int appliedFinal = applied;
            deferredCount.updateAndGet(c -> Math.max(0, c - appliedFinal));
        }
        return applied;
    }

    public static int getDeferredCount() {
        return deferredCount.get();
    }

    public static int getQueuedCount() {
        return queuedCount.get();
    }

    public static void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
        pendingUpdates.clear();
        completedUpdates.clear();
        queuedCount.set(0);
        initialized.set(false);
    }

    static {
        initialized = new AtomicBoolean(false);
        pendingUpdates = new ConcurrentLinkedQueue();
        completedUpdates = new ConcurrentLinkedQueue();
        queuedCount = new AtomicInteger(0);
        batchesThisFrame = new AtomicInteger(0);
        deferredCount = new AtomicInteger(0);
        maxBatchesPerFrame = 32;
    }

    @Environment(value=EnvType.CLIENT)
    public static class LightUpdate {
        public final long posKey;
        public final int lightLevel;
        public final boolean isSky;
        public volatile boolean processed = false;

        public LightUpdate(long posKey, int lightLevel, boolean isSky) {
            this.posKey = posKey;
            this.lightLevel = lightLevel;
            this.isSky = isSky;
        }
    }
}

