/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.data;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class FastIntMap<V> {
    private static final int DEFAULT_CAPACITY = 64;
    private static final float LOAD_FACTOR = 0.75f;
    private int[] keys;
    private boolean[] occupied;
    private Object[] values;
    private int size;
    private int capacity;
    private int threshold;

    public FastIntMap() {
        this(64);
    }

    public FastIntMap(int initialCapacity) {
        this.capacity = FastIntMap.nextPowerOfTwo(initialCapacity);
        this.threshold = (int)((float)this.capacity * 0.75f);
        this.keys = new int[this.capacity];
        this.values = new Object[this.capacity];
        this.occupied = new boolean[this.capacity];
    }

    public V get(int key) {
        int index;
        int i = index = this.indexOf(key);
        while (this.occupied[i]) {
            if (this.keys[i] == key) {
                return (V)this.values[i];
            }
            if ((i = i + 1 & this.capacity - 1) != index) continue;
            return null;
        }
        return null;
    }

    public void put(int key, V value) {
        if (this.size >= this.threshold) {
            this.resize();
        }
        int i = this.indexOf(key);
        while (this.occupied[i]) {
            if (this.keys[i] == key) {
                this.values[i] = value;
                return;
            }
            i = i + 1 & this.capacity - 1;
        }
        this.keys[i] = key;
        this.values[i] = value;
        this.occupied[i] = true;
        ++this.size;
    }

    public V remove(int key) {
        int i = this.indexOf(key);
        while (this.occupied[i]) {
            if (this.keys[i] == key) {
                Object old = this.values[i];
                this.occupied[i] = false;
                this.values[i] = null;
                --this.size;
                this.rehashFrom(i);
                return (V)old;
            }
            i = i + 1 & this.capacity - 1;
        }
        return null;
    }

    public int size() {
        return this.size;
    }

    public void clear() {
        Arrays.fill(this.occupied, false);
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    private int indexOf(int key) {
        return key * -1640531527 & this.capacity - 1;
    }

    private void rehashFrom(int start) {
        int i = start + 1 & this.capacity - 1;
        while (this.occupied[i]) {
            int k = this.keys[i];
            Object v = this.values[i];
            this.occupied[i] = false;
            this.values[i] = null;
            --this.size;
            this.put(k, v);
            i = i + 1 & this.capacity - 1;
        }
    }

    private void resize() {
        int oldCapacity = this.capacity;
        int[] oldKeys = this.keys;
        Object[] oldValues = this.values;
        boolean[] oldOccupied = this.occupied;
        this.capacity = oldCapacity << 1;
        this.threshold = (int)((float)this.capacity * 0.75f);
        this.keys = new int[this.capacity];
        this.values = new Object[this.capacity];
        this.occupied = new boolean[this.capacity];
        this.size = 0;
        for (int i = 0; i < oldCapacity; ++i) {
            if (!oldOccupied[i]) continue;
            this.put(oldKeys[i], oldValues[i]);
        }
    }

    private static int nextPowerOfTwo(int value) {
        int v = value - 1;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        return v + 1;
    }
}

