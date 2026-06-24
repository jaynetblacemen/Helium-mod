/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class RenderBatcher {
    private static final int INITIAL_BUFFER_SIZE = 65536;
    private static final int MAX_BATCH_SIZE = 4096;
    private static ByteBuffer vertexBuffer;
    private static int vertexCount;
    private static int currentTexture;
    private static boolean batching;

    private RenderBatcher() {
    }

    public static void beginBatch(int textureId) {
        if (vertexBuffer == null) {
            vertexBuffer = ByteBuffer.allocateDirect(65536).order(ByteOrder.nativeOrder());
        }
        vertexBuffer.clear();
        vertexCount = 0;
        currentTexture = textureId;
        batching = true;
    }

    public static boolean isBatching() {
        return batching;
    }

    public static boolean canBatch(int textureId) {
        return batching && textureId == currentTexture && vertexCount < 4096;
    }

    public static void addVertex(float x, float y, float z, float u, float v, int color) {
        if (!batching || vertexCount >= 4096) {
            return;
        }
        if (vertexBuffer.remaining() < 24) {
            RenderBatcher.growBuffer();
        }
        vertexBuffer.putFloat(x);
        vertexBuffer.putFloat(y);
        vertexBuffer.putFloat(z);
        vertexBuffer.putFloat(u);
        vertexBuffer.putFloat(v);
        vertexBuffer.putInt(color);
        ++vertexCount;
    }

    public static int getVertexCount() {
        return vertexCount;
    }

    public static ByteBuffer getBuffer() {
        if (vertexBuffer != null) {
            vertexBuffer.flip();
        }
        return vertexBuffer;
    }

    public static void endBatch() {
        batching = false;
        vertexCount = 0;
        currentTexture = -1;
    }

    private static void growBuffer() {
        int newCapacity = vertexBuffer.capacity() * 2;
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(newCapacity).order(ByteOrder.nativeOrder());
        vertexBuffer.flip();
        newBuffer.put(vertexBuffer);
        vertexBuffer = newBuffer;
    }

    static {
        vertexCount = 0;
        currentTexture = -1;
        batching = false;
    }
}

