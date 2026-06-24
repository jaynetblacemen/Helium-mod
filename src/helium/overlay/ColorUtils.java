/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.overlay;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ColorUtils {
    private ColorUtils() {
    }

    public static int parseColor(String colorStr, int transparency) {
        try {
            if (colorStr == null || colorStr.isEmpty()) {
                return Integer.MIN_VALUE;
            }
            if (colorStr.charAt(0) == '#') {
                colorStr = colorStr.substring(1);
            }
            int color = Integer.parseInt(colorStr, 16);
            int alpha = (int)((double)transparency * 2.55);
            return alpha << 24 | color;
        }
        catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    public static int createColor(int red, int green, int blue, int alpha) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int createColor(int red, int green, int blue) {
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    public static int withAlpha(int color, int alpha) {
        return alpha << 24 | color & 0xFFFFFF;
    }
}

