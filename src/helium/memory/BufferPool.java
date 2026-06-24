/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.memory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class BufferPool {
    private static final int[] BUCKET_SIZES = new int[]{256, 1024, 4096, 16384, 65536, 262144};
    private static final ConcurrentLinkedDeque<ByteBuffer>[] BUCKETS = new ConcurrentLinkedDeque[BUCKET_SIZES.length];
    private static final AtomicInteger[] BUCKET_COUNTS = new AtomicInteger[BUCKET_SIZES.length];
    private static volatile int maxPerBucket = 64;

    private BufferPool() {
    }

    public static void init(int poolSize) {
        maxPerBucket = poolSize;
        for (int i = 0; i < BUCKETS.length; ++i) {
            BufferPool.BUCKETS[i] = new ConcurrentLinkedDeque();
            BufferPool.BUCKET_COUNTS[i] = new AtomicInteger(0);
        }
    }

    public static ByteBuffer borrow(int minCapacity) {
        ByteBuffer buf;
        int bucketIndex = BufferPool.findBucket(minCapacity);
        if (bucketIndex >= 0 && BUCKETS[bucketIndex] != null && BUCKET_COUNTS[bucketIndex] != null && (buf = BUCKETS[bucketIndex].pollFirst()) != null) {
            BUCKET_COUNTS[bucketIndex].decrementAndGet();
            buf.clear();
            return buf;
        }
        int size = bucketIndex >= 0 ? BUCKET_SIZES[bucketIndex] : minCapacity;
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    public static void release(ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect()) {
            return;
        }
        int capacity = buffer.capacity();
        int bucketIndex = BufferPool.findExactBucket(capacity);
        if (bucketIndex >= 0 && BUCKETS[bucketIndex] != null && BUCKET_COUNTS[bucketIndex] != null && BUCKET_COUNTS[bucketIndex].get() < maxPerBucket) {
            buffer.clear();
            BUCKETS[bucketIndex].offerFirst(buffer);
            BUCKET_COUNTS[bucketIndex].incrementAndGet();
        }
    }

    private static int findBucket(int minCapacity) {
        for (int i = 0; i < BUCKET_SIZES.length; ++i) {
            if (BUCKET_SIZES[i] < minCapacity) continue;
            return i;
        }
        return -1;
    }

    private static int findExactBucket(int capacity) {
        for (int i = 0; i < BUCKET_SIZES.length; ++i) {
            if (BUCKET_SIZES[i] != capacity) continue;
            return i;
        }
        return -1;
    }
}

