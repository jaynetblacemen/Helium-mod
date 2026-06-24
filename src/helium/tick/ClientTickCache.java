/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.tick;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ClientTickCache {
    private static final int MAX_CACHE_SIZE = 16384;
    private static final Map<Long, Integer> biomeColorCache = new LinkedHashMap<Long, Integer>(1024, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
            return this.size() > 16384;
        }
    };
    private static final Map<Long, Integer> lightLevelCache = new LinkedHashMap<Long, Integer>(1024, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
            return this.size() > 16384;
        }
    };
    private static final Object biomeLock = new Object();
    private static final Object lightLock = new Object();

    private ClientTickCache() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int getCachedBiomeColor(long posKey, int fallback) {
        Object object = biomeLock;
        synchronized (object) {
            Integer cached = biomeColorCache.get(posKey);
            return cached != null ? cached : fallback;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void cacheBiomeColor(long posKey, int color) {
        Object object = biomeLock;
        synchronized (object) {
            biomeColorCache.put(posKey, color);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int getCachedLightLevel(long posKey, int fallback) {
        Object object = lightLock;
        synchronized (object) {
            Integer cached = lightLevelCache.get(posKey);
            return cached != null ? cached : fallback;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void cacheLightLevel(long posKey, int level) {
        Object object = lightLock;
        synchronized (object) {
            lightLevelCache.put(posKey, level);
        }
    }

    public static void tick(long currentTick) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void invalidateAll() {
        Object object = biomeLock;
        synchronized (object) {
            biomeColorCache.clear();
        }
        object = lightLock;
        synchronized (object) {
            lightLevelCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void invalidatePosition(long posKey) {
        Object object = biomeLock;
        synchronized (object) {
            biomeColorCache.remove(posKey);
        }
        object = lightLock;
        synchronized (object) {
            lightLevelCache.remove(posKey);
        }
    }

    public static long packPos(int x, int y, int z) {
        return ((long)x & 0x3FFFFFFL) << 38 | ((long)y & 0xFFFL) << 26 | (long)z & 0x3FFFFFFL;
    }
}

