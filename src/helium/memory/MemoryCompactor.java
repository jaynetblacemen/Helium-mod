/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.memory;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class MemoryCompactor {
    private static final ConcurrentHashMap<String, WeakReference<String>> STRING_INTERN_POOL = new ConcurrentHashMap(4096);
    private static final ConcurrentHashMap<Long, int[]> PALETTE_DEDUP = new ConcurrentHashMap(1024);
    private static long lastCompactTick = 0L;
    private static final int COMPACT_INTERVAL_TICKS = 600;
    private static final int MAX_STRING_POOL_SIZE = 16384;

    private MemoryCompactor() {
    }

    public static String deduplicateString(String value) {
        String existing;
        if (value == null) {
            return null;
        }
        WeakReference<String> ref = STRING_INTERN_POOL.get(value);
        if (ref != null && (existing = (String)ref.get()) != null) {
            return existing;
        }
        if (STRING_INTERN_POOL.size() < 16384) {
            STRING_INTERN_POOL.put(value, new WeakReference<String>(value));
        }
        return value;
    }

    public static int[] deduplicatePalette(long hash, int[] palette) {
        int[] existing = PALETTE_DEDUP.get(hash);
        if (existing != null && Arrays.equals(existing, palette)) {
            return existing;
        }
        PALETTE_DEDUP.put(hash, palette);
        return palette;
    }

    public static long hashPalette(int[] palette) {
        long hash = -3750763034362895579L;
        for (int v : palette) {
            hash ^= (long)v;
            hash *= 1099511628211L;
        }
        return hash;
    }

    public static void tick(long currentTick) {
        if (currentTick - lastCompactTick >= 600L) {
            MemoryCompactor.compact();
            lastCompactTick = currentTick;
        }
    }

    public static void compact() {
        STRING_INTERN_POOL.entrySet().removeIf(entry -> ((WeakReference)entry.getValue()).get() == null);
        if (PALETTE_DEDUP.size() > 8192) {
            Iterator<Map.Entry<Long, int[]>> it = PALETTE_DEDUP.entrySet().iterator();
            for (int toRemove = PALETTE_DEDUP.size() / 4; it.hasNext() && toRemove > 0; --toRemove) {
                it.next();
                it.remove();
            }
        }
    }
}

