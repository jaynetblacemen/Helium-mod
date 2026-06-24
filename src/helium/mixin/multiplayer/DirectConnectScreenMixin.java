/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1060
 *  net.minecraft.class_10799
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_420
 *  net.minecraft.class_437
 *  net.minecraft.class_5348
 *  net.minecraft.class_5481
 *  net.minecraft.class_642
 *  net.minecraft.class_8573
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.multiplayer;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.multiplayer.DirectConnectPreview;
import com.helium.multiplayer.ServerPreviewUpdater;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1060;
import net.minecraft.class_10799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_420;
import net.minecraft.class_437;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import net.minecraft.class_642;
import net.minecraft.class_8573;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_420.class})
public abstract class DirectConnectScreenMixin
extends class_437
implements ServerPreviewUpdater {
    @Unique
    private static final class_2960 PING_1 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/ping_1.png");
    @Unique
    private static final class_2960 PING_2 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/ping_2.png");
    @Unique
    private static final class_2960 PING_3 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/ping_3.png");
    @Unique
    private static final class_2960 PING_4 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/ping_4.png");
    @Unique
    private static final class_2960 PING_5 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/ping_5.png");
    @Unique
    private static final class_2960 PINGING_1 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/pinging_1.png");
    @Unique
    private static final class_2960 PINGING_2 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/pinging_2.png");
    @Unique
    private static final class_2960 PINGING_3 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/pinging_3.png");
    @Unique
    private static final class_2960 PINGING_4 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/pinging_4.png");
    @Unique
    private static final class_2960 PINGING_5 = class_2960.method_60655((String)"helium", (String)"gui/serverlist/pinging_5.png");
    @Unique
    private static final class_2960 DEFAULT_ICON = class_2960.method_60655((String)"helium", (String)"gui/serverlist/default_icon.png");
    @Shadow
    private class_342 field_2463;
    @Unique
    private String helium$lastAddress = "";
    @Unique
    private String helium$serverName = "";
    @Unique
    private class_2561 helium$motdText = class_2561.method_43473();
    @Unique
    private String helium$playerCount = "0/0";
    @Unique
    private long helium$pingValue = -1L;
    @Unique
    private class_8573 helium$serverIcon = null;
    @Unique
    private byte[] helium$lastFavicon = null;

    protected DirectConnectScreenMixin(class_2561 title) {
        super(title);
    }

    @Override
    public void helium$updateServerData(String name, String[] motd, String players, long ping) {
        this.helium$serverName = name != null ? name : "";
        this.helium$playerCount = players != null ? players : "0/0";
        this.helium$pingValue = ping;
    }

    @Override
    public void helium$setMotdText(class_2561 motd) {
        this.helium$motdText = motd != null ? motd : class_2561.method_43473();
    }

    @Override
    public void helium$updateFavicon(byte[] faviconBytes) {
        block8: {
            class_310 client = class_310.method_1551();
            if (!client.method_18854()) {
                client.execute(() -> this.helium$updateFavicon(faviconBytes));
                return;
            }
            if (faviconBytes == null) {
                if (this.helium$serverIcon != null) {
                    this.helium$serverIcon.close();
                    this.helium$serverIcon = null;
                }
                this.helium$lastFavicon = null;
                return;
            }
            if (this.helium$lastFavicon != null && Arrays.equals(faviconBytes, this.helium$lastFavicon)) {
                return;
            }
            this.helium$lastFavicon = faviconBytes;
            byte[] valid = class_642.method_53885((byte[])faviconBytes);
            if (valid == null) {
                return;
            }
            try {
                if (this.helium$serverIcon != null) {
                    this.helium$serverIcon.close();
                    this.helium$serverIcon = null;
                }
                String idSource = this.helium$lastAddress != null && !this.helium$lastAddress.isBlank() ? this.helium$lastAddress : "helium_preview";
                this.helium$serverIcon = class_8573.method_52202((class_1060)client.method_1531(), (String)idSource);
                class_1011 img = class_1011.method_49277((byte[])valid);
                this.helium$serverIcon.method_52199(img);
            }
            catch (Exception e) {
                if (this.helium$serverIcon == null) break block8;
                this.helium$serverIcon.close();
                this.helium$serverIcon = null;
            }
        }
    }

    @Inject(method={"method_25394"}, at={@At(value="TAIL")}, require=0)
    private void helium$renderServerPreview(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        class_2960 pingTexture;
        String address;
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.directConnectPreview) {
            return;
        }
        if (this.field_2463 != null && (address = this.field_2463.method_1882()) != null && !address.isBlank() && !address.equals(this.helium$lastAddress)) {
            this.helium$lastAddress = address;
            DirectConnectPreview.onAddressChanged(address);
        }
        int maxRowWidth = 305;
        int minRowWidth = 200;
        int sidePadding = 20;
        int rowWidth = Math.min(maxRowWidth, Math.max(minRowWidth, this.field_22789 - sidePadding * 2));
        int baseX = (this.field_22789 - rowWidth) / 2;
        int baseY = this.field_2463 != null ? this.field_2463.method_46427() + this.field_2463.method_25364() + 8 : this.field_22790 / 2 + 30;
        int iconSize = 32;
        if (this.helium$serverIcon != null) {
            context.method_25290(class_10799.field_56883, this.helium$serverIcon.method_52201(), baseX, baseY, 0.0f, 0.0f, iconSize, iconSize, iconSize, iconSize);
        } else {
            context.method_25290(class_10799.field_56883, DEFAULT_ICON, baseX, baseY, 0.0f, 0.0f, iconSize, iconSize, iconSize, iconSize);
        }
        int textX = baseX + iconSize + 3;
        context.method_27535(this.field_22793, (class_2561)class_2561.method_43470((String)this.helium$serverName), textX, baseY, -1);
        if (this.helium$motdText != null && !this.helium$motdText.getString().isEmpty()) {
            int motdY = baseY + 12;
            Objects.requireNonNull(this.field_22793);
            int lineHeight = 9;
            int availableWidth = rowWidth - iconSize - 10;
            List lines = this.field_22793.method_1728((class_5348)this.helium$motdText, availableWidth);
            for (class_5481 line : lines) {
                context.method_35720(this.field_22793, line, textX, motdY, -1);
                motdY += lineHeight;
            }
        }
        if (this.helium$pingValue < 0L) {
            long tick = System.currentTimeMillis() / 100L;
            int frame = (int)(tick % 8L);
            if (frame > 4) {
                frame = 8 - frame;
            }
            pingTexture = switch (frame) {
                case 1 -> PINGING_2;
                case 2 -> PINGING_3;
                case 3 -> PINGING_4;
                case 4 -> PINGING_5;
                default -> PINGING_1;
            };
        } else {
            pingTexture = this.helium$pingValue < 150L ? PING_5 : (this.helium$pingValue < 300L ? PING_4 : (this.helium$pingValue < 600L ? PING_3 : (this.helium$pingValue < 1000L ? PING_2 : PING_1)));
        }
        int pingX = baseX + rowWidth - 10 - 5;
        int pingWidth = 10;
        int pingHeight = 8;
        context.method_25290(class_10799.field_56883, pingTexture, pingX, baseY, 0.0f, 0.0f, pingWidth, pingHeight, pingWidth, pingHeight);
        if (this.helium$pingValue >= 0L) {
            String players = this.helium$playerCount.contains("/") ? this.helium$playerCount.split("/")[0] : this.helium$playerCount;
            String maxPlayers = this.helium$playerCount.contains("/") ? this.helium$playerCount.split("/")[1] : "0";
            String slash = "/";
            int playersWidth = this.field_22793.method_1727(players);
            int slashWidth = this.field_22793.method_1727(slash);
            int maxPlayersWidth = this.field_22793.method_1727(maxPlayers);
            int playerTextX = pingX - (playersWidth + slashWidth + maxPlayersWidth) - 5;
            context.method_27535(this.field_22793, (class_2561)class_2561.method_43470((String)players), playerTextX, baseY, -5592406);
            context.method_27535(this.field_22793, (class_2561)class_2561.method_43470((String)slash), playerTextX + playersWidth, baseY, -11184811);
            context.method_27535(this.field_22793, (class_2561)class_2561.method_43470((String)maxPlayers), playerTextX + playersWidth + slashWidth, baseY, -5592406);
        }
    }
}

