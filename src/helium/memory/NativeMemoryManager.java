/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.memory;

import com.helium.HeliumClient;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class NativeMemoryManager {
    private static final int[] POOL_SIZES = new int[]{1024, 4096, 16384, 65536, 262144, 0x100000};
    private static final ConcurrentLinkedDeque<ByteBuffer>[] POOLS = new ConcurrentLinkedDeque[POOL_SIZES.length];
    private static final ConcurrentHashMap<Long, AllocationInfo> allocations = new ConcurrentHashMap();
    private static final AtomicLong cleanupCounter = new AtomicLong(0L);
    private static final int CLEANUP_INTERVAL = 1000;
    private static final AtomicLong allocationIdCounter = new AtomicLong(0L);
    private static final AtomicLong totalAllocatedBytes = new AtomicLong(0L);
    private static final AtomicLong totalPooledBytes = new AtomicLong(0L);
    private static volatile long maxMemoryBytes = 0x4000000L;
    private static volatile boolean initialized = false;

    private NativeMemoryManager() {
    }

    public static void init(int maxMemoryMb) {
        maxMemoryBytes = (long)maxMemoryMb * 1024L * 1024L;
        for (int i = 0; i < POOLS.length; ++i) {
            NativeMemoryManager.POOLS[i] = new ConcurrentLinkedDeque();
        }
        initialized = true;
        HeliumClient.LOGGER.info("native memory manager initialized with {}MB limit", (Object)maxMemoryMb);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static ByteBuffer allocate(int size) {
        int actualSize;
        if (!initialized) {
            return null;
        }
        int poolIndex = NativeMemoryManager.findPoolIndex(size);
        int n = actualSize = poolIndex >= 0 ? POOL_SIZES[poolIndex] : size;
        if (totalAllocatedBytes.get() + (long)actualSize > maxMemoryBytes) {
            NativeMemoryManager.evictFromPools(actualSize);
            if (totalAllocatedBytes.get() + (long)actualSize > maxMemoryBytes) {
                HeliumClient.LOGGER.warn("native memory limit reached, cannot allocate {} bytes", (Object)size);
                return null;
            }
        }
        ByteBuffer buffer = null;
        if (poolIndex >= 0 && (buffer = POOLS[poolIndex].pollFirst()) != null) {
            totalPooledBytes.addAndGet(-buffer.capacity());
            buffer.clear();
        }
        if (buffer == null) {
            try {
                buffer = ByteBuffer.allocateDirect(actualSize).order(ByteOrder.nativeOrder());
                totalAllocatedBytes.addAndGet(actualSize);
            }
            catch (OutOfMemoryError e) {
                HeliumClient.LOGGER.error("failed to allocate {} bytes of native memory", (Object)actualSize);
                return null;
            }
        }
        long id = allocationIdCounter.incrementAndGet();
        allocations.put(id, new AllocationInfo(new WeakReference<ByteBuffer>(buffer), actualSize, poolIndex));
        return buffer;
    }

    public static void free(ByteBuffer buffer) {
        if (!initialized || buffer == null || !buffer.isDirect()) {
            return;
        }
        Long idToRemove = null;
        AllocationInfo info = null;
        for (Map.Entry<Long, AllocationInfo> entry : allocations.entrySet()) {
            AllocationInfo ai = entry.getValue();
            ByteBuffer ref = (ByteBuffer)ai.bufferRef.get();
            if (ref != buffer) continue;
            idToRemove = entry.getKey();
            info = ai;
            break;
        }
        if (idToRemove != null) {
            allocations.remove(idToRemove);
        }
        if (info != null && info.poolIndex >= 0) {
            buffer.clear();
            POOLS[info.poolIndex].offerFirst(buffer);
            totalPooledBytes.addAndGet(info.size);
        }
        if (cleanupCounter.incrementAndGet() % 1000L == 0L) {
            NativeMemoryManager.cleanupStaleAllocations();
        }
    }

    private static void cleanupStaleAllocations() {
        Iterator<Map.Entry<Long, AllocationInfo>> it = allocations.entrySet().iterator();
        while (it.hasNext()) {
            AllocationInfo info = it.next().getValue();
            if (info.bufferRef.get() != null) continue;
            it.remove();
        }
    }

    private static int findPoolIndex(int size) {
        for (int i = 0; i < POOL_SIZES.length; ++i) {
            if (POOL_SIZES[i] < size) continue;
            return i;
        }
        return -1;
    }

    private static void evictFromPools(long bytesNeeded) {
        long evicted = 0L;
        for (int i = POOL_SIZES.length - 1; i >= 0 && evicted < bytesNeeded; --i) {
            ByteBuffer buf;
            while ((buf = POOLS[i].pollLast()) != null && evicted < bytesNeeded) {
                evicted += (long)buf.capacity();
                totalPooledBytes.addAndGet(-buf.capacity());
                totalAllocatedBytes.addAndGet(-buf.capacity());
            }
        }
    }

    public static ByteBuffer allocateAligned(int size, int alignment) {
        int alignedSize = (size + alignment - 1) / alignment * alignment;
        return NativeMemoryManager.allocate(alignedSize);
    }

    public static void copy(ByteBuffer src, ByteBuffer dst, int length) {
        if (src == null || dst == null) {
            return;
        }
        int srcPos = src.position();
        int dstPos = dst.position();
        int copyLen = Math.min(length, Math.min(src.remaining(), dst.remaining()));
        for (int i = 0; i < copyLen; ++i) {
            dst.put(dstPos + i, src.get(srcPos + i));
        }
    }

    public static void zero(ByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        for (int i = 0; i < buffer.capacity(); ++i) {
            buffer.put(i, (byte)0);
        }
    }

    public static long getTotalAllocatedBytes() {
        return totalAllocatedBytes.get();
    }

    public static long getTotalPooledBytes() {
        return totalPooledBytes.get();
    }

    public static long getActiveBytes() {
        return totalAllocatedBytes.get() - totalPooledBytes.get();
    }

    public static long getMaxMemoryBytes() {
        return maxMemoryBytes;
    }

    public static int getAllocationCount() {
        return allocations.size();
    }

    public static void shutdown() {
        for (int i = 0; i < POOLS.length; ++i) {
            if (POOLS[i] == null) continue;
            POOLS[i].clear();
        }
        allocations.clear();
        totalAllocatedBytes.set(0L);
        totalPooledBytes.set(0L);
        initialized = false;
        HeliumClient.LOGGER.info("native memory manager shutdown");
    }

    public static double getUsagePercent() {
        if (maxMemoryBytes <= 0L) {
            return 0.0;
        }
        return (double)NativeMemoryManager.getActiveBytes() / (double)maxMemoryBytes * 100.0;
    }

    public static String getStats() {
        return String.format("Native Memory: %dMB active / %dMB pooled / %dMB limit (%d allocations)", NativeMemoryManager.getActiveBytes() / 0x100000L, NativeMemoryManager.getTotalPooledBytes() / 0x100000L, maxMemoryBytes / 0x100000L, NativeMemoryManager.getAllocationCount());
    }

    @Environment(value=EnvType.CLIENT)
    private record AllocationInfo(WeakReference<ByteBuffer> bufferRef, int size, int poolIndex) {
    }
}

