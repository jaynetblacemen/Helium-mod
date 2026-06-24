/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.network;

import com.helium.HeliumClient;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class PacketBatcher {
    private static final ConcurrentLinkedQueue<byte[]> outgoingBatch = new ConcurrentLinkedQueue();
    private static final AtomicInteger batchSize = new AtomicInteger(0);
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static volatile int maxBatchSize = 32;
    private static volatile int maxBatchBytes = 65536;
    private static final AtomicInteger currentBatchBytes = new AtomicInteger(0);

    private PacketBatcher() {
    }

    public static void init() {
        if (initialized.getAndSet(true)) {
            return;
        }
        HeliumClient.LOGGER.info("packet batcher initialized (max batch={}, max bytes={})", (Object)maxBatchSize, (Object)maxBatchBytes);
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static boolean addToBatch(byte[] packetData) {
        if (!initialized.get() || packetData == null) {
            return false;
        }
        if (batchSize.get() >= maxBatchSize || currentBatchBytes.get() + packetData.length > maxBatchBytes) {
            return false;
        }
        outgoingBatch.offer(packetData);
        batchSize.incrementAndGet();
        currentBatchBytes.addAndGet(packetData.length);
        return true;
    }

    public static byte[][] flush() {
        byte[] data;
        if (!initialized.get() || batchSize.get() == 0) {
            return null;
        }
        int size = batchSize.get();
        byte[][] batch = new byte[size][];
        int idx = 0;
        while ((data = outgoingBatch.poll()) != null && idx < size) {
            batch[idx++] = data;
        }
        batchSize.set(0);
        currentBatchBytes.set(0);
        return (byte[][])(idx > 0 ? batch : null);
    }

    public static int getBatchSize() {
        return batchSize.get();
    }

    public static boolean shouldFlush() {
        return batchSize.get() >= maxBatchSize || currentBatchBytes.get() >= maxBatchBytes;
    }

    public static void shutdown() {
        outgoingBatch.clear();
        batchSize.set(0);
        currentBatchBytes.set(0);
        initialized.set(false);
    }
}

