/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.helium.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class DwmEnums {
    private DwmEnums() {
    }

    @Environment(value=EnvType.CLIENT)
    public static enum WindowCorner {
        DEFAULT("default"),
        DO_NOT_ROUND("square"),
        ROUND("round"),
        ROUND_SMALL("round_small");

        public final String id;

        private WindowCorner(String id) {
            this.id = id;
        }

        public static WindowCorner fromString(String s) {
            if (s == null) {
                return ROUND;
            }
            for (WindowCorner c : WindowCorner.values()) {
                if (!c.id.equalsIgnoreCase(s) && !c.name().equalsIgnoreCase(s)) continue;
                return c;
            }
            return ROUND;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum WindowMaterial {
        AUTO("auto"),
        NONE("none"),
        MICA("mica"),
        ACRYLIC("acrylic"),
        TABBED("tabbed");

        public final String id;

        private WindowMaterial(String id) {
            this.id = id;
        }

        public static WindowMaterial fromString(String s) {
            if (s == null) {
                return TABBED;
            }
            for (WindowMaterial m : WindowMaterial.values()) {
                if (!m.id.equalsIgnoreCase(s) && !m.name().equalsIgnoreCase(s)) continue;
                return m;
            }
            return TABBED;
        }
    }
}

