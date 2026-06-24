/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 */
package com.helium.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;

@Environment(value=EnvType.CLIENT)
public interface ServerPreviewUpdater {
    public void helium$updateServerData(String var1, String[] var2, String var3, long var4);

    public void helium$updateFavicon(byte[] var1);

    public void helium$setMotdText(class_2561 var1);
}

