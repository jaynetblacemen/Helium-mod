/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_5321
 *  net.minecraft.class_6880
 */
package com.helium.overlay;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.overlay.ColorUtils;
import com.helium.overlay.FpsStats;
import com.helium.overlay.OverlayPosition;
import com.helium.particle.ParticleLimiter;
import java.util.ArrayList;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_5321;
import net.minecraft.class_6880;

@Environment(value=EnvType.CLIENT)
public final class OverlayRenderer {
    private static final int PADDING = 6;
    private static final int LINE_HEIGHT = 10;
    private static final int SHADOW_OFFSET = 1;
    private static final FpsStats fpsStats = new FpsStats();
    private static volatile boolean renderFailed = false;

    private OverlayRenderer() {
    }

    public static void render(class_332 context, class_310 client) {
        if (renderFailed) {
            return;
        }
        try {
            OverlayRenderer.renderInternal(context, client);
        }
        catch (Throwable t) {
            renderFailed = true;
            HeliumClient.LOGGER.warn("overlay disabled on this mc version ({})", (Object)t.getClass().getSimpleName());
        }
    }

    private static void renderInternal(class_332 context, class_310 client) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.fpsOverlay) {
            return;
        }
        if (client.field_1724 == null) {
            return;
        }
        if (client.field_1690.field_1842) {
            return;
        }
        fpsStats.updateFps(client.method_47599());
        int screenWidth = client.method_22683().method_4486();
        int screenHeight = client.method_22683().method_4502();
        String[] lines = OverlayRenderer.buildOverlayLines(client, config);
        int maxWidth = 0;
        for (String line : lines) {
            int w = client.field_1772.method_1727(line);
            if (w <= maxWidth) continue;
            maxWidth = w;
        }
        int totalHeight = lines.length * 10;
        int boxWidth = maxWidth + 12;
        int boxHeight = totalHeight + 12 - 2;
        OverlayPosition position = OverlayRenderer.parsePosition(config.overlayPosition);
        int[] pos = OverlayRenderer.calculatePosition(position, screenWidth, screenHeight, boxWidth, boxHeight);
        int boxX = pos[0];
        int boxY = pos[1];
        int bgColor = ColorUtils.parseColor(config.overlayBackgroundColor, config.overlayTransparency);
        int textColor = ColorUtils.parseColor(config.overlayTextColor, 100);
        OverlayRenderer.drawShadow(context, boxX, boxY, boxWidth, boxHeight);
        OverlayRenderer.drawRoundedBox(context, boxX, boxY, boxWidth, boxHeight, bgColor);
        int textX = boxX + 6;
        int textY = boxY + 6;
        for (String line : lines) {
            context.method_51439(client.field_1772, (class_2561)class_2561.method_43470((String)line), textX, textY, textColor, false);
            textY += 10;
        }
    }

    private static String[] buildOverlayLines(class_310 client, HeliumConfig config) {
        ArrayList<String> lines = new ArrayList<String>();
        if (config.overlayShowFps) {
            lines.add(String.format("%d FPS", fpsStats.getCurrentFps()));
        }
        if (config.overlayShowFpsMinMaxAvg) {
            lines.add(String.format("\u2193%d \u2191%d ~%d", fpsStats.getMinFps(), fpsStats.getMaxFps(), fpsStats.getAvgFps()));
        }
        if (config.overlayShowMemory) {
            Runtime rt = Runtime.getRuntime();
            long usedMb = (rt.totalMemory() - rt.freeMemory()) / 1024L / 1024L;
            long maxMb = rt.maxMemory() / 1024L / 1024L;
            lines.add(String.format("%dMB / %dMB", usedMb, maxMb));
        }
        if (config.overlayShowParticles && config.particleLimiting && ParticleLimiter.isInitialized()) {
            lines.add(String.format("Particles: %d/%d", ParticleLimiter.getCurrentCount(), ParticleLimiter.getMaxParticles()));
        }
        if (config.overlayShowCoordinates && client.field_1724 != null) {
            lines.add(String.format("X: %.0f Y: %.0f Z: %.0f", client.field_1724.method_23317(), client.field_1724.method_23318(), client.field_1724.method_23321()));
        }
        if (config.overlayShowBiome && client.field_1724 != null && client.field_1687 != null) {
            try {
                class_6880 biomeEntry = client.field_1687.method_23753(client.field_1724.method_24515());
                Optional biomeKey = biomeEntry.method_40230();
                if (biomeKey.isPresent()) {
                    String biomeName = ((class_5321)biomeKey.get()).method_29177().method_12832();
                    biomeName = biomeName.replace('_', ' ');
                    StringBuilder formatted = new StringBuilder();
                    boolean capitalize = true;
                    for (char c : biomeName.toCharArray()) {
                        formatted.append(capitalize ? Character.toUpperCase(c) : c);
                        capitalize = c == ' ';
                    }
                    lines.add(formatted.toString());
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (lines.isEmpty()) {
            lines.add("Helium");
        }
        return lines.toArray(new String[0]);
    }

    private static OverlayPosition parsePosition(String posStr) {
        if (posStr == null) {
            return OverlayPosition.TOP_LEFT;
        }
        return switch (posStr.toUpperCase()) {
            case "TOP_RIGHT" -> OverlayPosition.TOP_RIGHT;
            case "BOTTOM_LEFT" -> OverlayPosition.BOTTOM_LEFT;
            case "BOTTOM_RIGHT" -> OverlayPosition.BOTTOM_RIGHT;
            default -> OverlayPosition.TOP_LEFT;
        };
    }

    private static int[] calculatePosition(OverlayPosition position, int screenWidth, int screenHeight, int boxWidth, int boxHeight) {
        int[] nArray;
        int margin = 10;
        switch (position) {
            case TOP_RIGHT: {
                int[] nArray2 = new int[2];
                nArray2[0] = screenWidth - boxWidth - margin;
                nArray = nArray2;
                nArray2[1] = margin;
                break;
            }
            case BOTTOM_LEFT: {
                int[] nArray3 = new int[2];
                nArray3[0] = margin;
                nArray = nArray3;
                nArray3[1] = screenHeight - boxHeight - margin;
                break;
            }
            case BOTTOM_RIGHT: {
                int[] nArray4 = new int[2];
                nArray4[0] = screenWidth - boxWidth - margin;
                nArray = nArray4;
                nArray4[1] = screenHeight - boxHeight - margin;
                break;
            }
            default: {
                int[] nArray5 = new int[2];
                nArray5[0] = margin;
                nArray = nArray5;
                nArray5[1] = margin;
            }
        }
        return nArray;
    }

    private static void drawShadow(class_332 context, int x, int y, int width, int height) {
        int shadowColor = ColorUtils.createColor(0, 0, 0, 40);
        context.method_25294(x + 1, y + 1, x + width + 1, y + height + 1, shadowColor);
    }

    private static void drawRoundedBox(class_332 context, int x, int y, int width, int height, int color) {
        context.method_25294(x, y, x + width, y + height, color);
        int cornerRadius = 2;
        int cornerColor = ColorUtils.withAlpha(color, color >> 24 & 0xFF);
        context.method_25294(x - 1, y + cornerRadius, x, y + height - cornerRadius, cornerColor);
        context.method_25294(x + width, y + cornerRadius, x + width + 1, y + height - cornerRadius, cornerColor);
        context.method_25294(x + cornerRadius, y - 1, x + width - cornerRadius, y, cornerColor);
        context.method_25294(x + cornerRadius, y + height, x + width - cornerRadius, y + height + 1, cornerColor);
    }

    public static FpsStats getFpsStats() {
        return fpsStats;
    }
}

