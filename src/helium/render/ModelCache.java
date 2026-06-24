/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.render;

import com.helium.HeliumClient;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ModelCache {
    private static final Object LOCK = new Object();
    private static Map<Long, Object> cache;
    private static volatile boolean initialized;
    private static volatile int maxEntries;
    private static volatile int hits;
    private static volatile int misses;

    private ModelCache() {
    }

    public static void init(int maxSizeMb) {
        if (initialized) {
            return;
        }
        maxEntries = Math.max(1024, maxSizeMb * 1024 * 1024 / 512);
        cache = new LinkedHashMap<Long, Object>(1024, 0.75f, true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Object> eldest) {
                return this.size() > maxEntries;
            }
        };
        initialized = true;
        HeliumClient.LOGGER.info("model cache initialized (max {} entries, ~{}mb)", (Object)maxEntries, (Object)maxSizeMb);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T get(long key) {
        if (!initialized) {
            return null;
        }
        Object object = LOCK;
        synchronized (object) {
            Object val = cache.get(key);
            if (val != null) {
                ++hits;
                return (T)val;
            }
            ++misses;
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void put(long key, Object value) {
        if (!initialized || value == null) {
            return;
        }
        Object object = LOCK;
        synchronized (object) {
            cache.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void invalidate(long key) {
        if (!initialized) {
            return;
        }
        Object object = LOCK;
        synchronized (object) {
            cache.remove(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void invalidateAll() {
        if (!initialized) {
            return;
        }
        Object object = LOCK;
        synchronized (object) {
            cache.clear();
        }
        hits = 0;
        misses = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int size() {
        if (!initialized) {
            return 0;
        }
        Object object = LOCK;
        synchronized (object) {
            return cache.size();
        }
    }

    public static int getHits() {
        return hits;
    }

    public static int getMisses() {
        return misses;
    }

    public static float getHitRate() {
        int total = hits + misses;
        return total > 0 ? (float)hits / (float)total : 0.0f;
    }

    static {
        initialized = false;
        maxEntries = 8192;
        hits = 0;
        misses = 0;
    }
}

