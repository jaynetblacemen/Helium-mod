/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338$class_2339
 *  org.joml.Vector3f
 */
package com.helium.memory;

import java.util.ArrayDeque;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public final class ObjectPool {
    private static final ThreadLocal<ArrayDeque<class_2338.class_2339>> BLOCK_POS_POOL = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<ArrayDeque<Vector3f>> VEC3F_POOL = ThreadLocal.withInitial(ArrayDeque::new);
    private static int maxPoolSize = 512;

    private ObjectPool() {
    }

    public static void init(int poolSize) {
        maxPoolSize = poolSize;
    }

    public static class_2338.class_2339 borrowBlockPos() {
        ArrayDeque<class_2338.class_2339> pool = BLOCK_POS_POOL.get();
        class_2338.class_2339 pos = pool.pollFirst();
        return pos != null ? pos : new class_2338.class_2339();
    }

    public static void returnBlockPos(class_2338.class_2339 pos) {
        ArrayDeque<class_2338.class_2339> pool = BLOCK_POS_POOL.get();
        if (pool.size() < maxPoolSize) {
            pos.method_10103(0, 0, 0);
            pool.offerFirst(pos);
        }
    }

    public static Vector3f borrowVec3f() {
        ArrayDeque<Vector3f> pool = VEC3F_POOL.get();
        Vector3f vec = pool.pollFirst();
        return vec != null ? vec.set(0.0f, 0.0f, 0.0f) : new Vector3f();
    }

    public static void returnVec3f(Vector3f vec) {
        ArrayDeque<Vector3f> pool = VEC3F_POOL.get();
        if (pool.size() < maxPoolSize) {
            pool.offerFirst(vec);
        }
    }

    public static <T> Pool<T> create(Supplier<T> factory, int capacity) {
        return new Pool<T>(factory, capacity);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Pool<T> {
        private final ArrayDeque<T> objects;
        private final Supplier<T> factory;
        private final int capacity;

        Pool(Supplier<T> factory, int capacity) {
            this.factory = factory;
            this.capacity = capacity;
            this.objects = new ArrayDeque(capacity);
        }

        public T borrow() {
            T obj = this.objects.pollFirst();
            return obj != null ? obj : this.factory.get();
        }

        public void release(T obj) {
            if (this.objects.size() < this.capacity) {
                this.objects.offerFirst(obj);
            }
        }
    }
}

