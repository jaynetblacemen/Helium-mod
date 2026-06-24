/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.network;

import com.helium.memory.BufferPool;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class BufferOptimizer {
    private static final ConcurrentLinkedQueue<byte[]> PACKET_BUFFER_POOL = new ConcurrentLinkedQueue();
    private static final int MAX_POOLED_BUFFERS = 256;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int TRIM_THRESHOLD = 192;
    private static final AtomicInteger pooledCount = new AtomicInteger(0);
    private static volatile long lastTrimTick = 0L;
    private static final int TRIM_INTERVAL_TICKS = 200;

    private BufferOptimizer() {
    }

    public static byte[] borrowPacketBuffer(int minSize) {
        if (minSize <= 8192) {
            byte[] buf = PACKET_BUFFER_POOL.poll();
            if (buf != null) {
                pooledCount.decrementAndGet();
                return buf;
            }
            return new byte[8192];
        }
        return new byte[minSize];
    }

    public static void returnPacketBuffer(byte[] buffer) {
        if (buffer != null && buffer.length == 8192 && pooledCount.get() < 256) {
            PACKET_BUFFER_POOL.offer(buffer);
            pooledCount.incrementAndGet();
        }
    }

    public static void tick(long currentTick) {
        if (currentTick - lastTrimTick < 200L) {
            return;
        }
        lastTrimTick = currentTick;
        int count = pooledCount.get();
        if (count > 192) {
            byte[] removed;
            int toRemove = count - 192;
            for (int i = 0; i < toRemove && (removed = PACKET_BUFFER_POOL.poll()) != null; ++i) {
                pooledCount.decrementAndGet();
            }
        }
    }

    public static int getPooledCount() {
        return pooledCount.get();
    }

    public static ByteBuffer borrowDirectBuffer(int minSize) {
        return BufferPool.borrow(minSize);
    }

    public static void returnDirectBuffer(ByteBuffer buffer) {
        BufferPool.release(buffer);
    }
}

