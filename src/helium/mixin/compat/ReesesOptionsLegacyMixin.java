/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.helium.mixin.compat;

import com.helium.HeliumClient;
import com.helium.compat.HeliumSodiumLegacyPage;
import com.helium.config.HeliumConfigScreen;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(targets={"me/flashyreese/mods/reeses_sodium_options/client/gui/SodiumVideoOptionsScreen"}, remap=false)
public abstract class ReesesOptionsLegacyMixin {
    @Unique
    private Object helium$heliumPage;
    @Unique
    private Object helium$heliumPageName;
    @Unique
    private Field helium$pagesField;
    @Unique
    private Field helium$tabSelectedField;

    @Inject(method={"<init>"}, at={@At(value="TAIL")}, require=0)
    private void helium$init(CallbackInfo ci) {
        try {
            Class<?> thisClass = this.getClass();
            this.helium$pagesField = this.helium$findField(thisClass, "pages");
            this.helium$tabSelectedField = this.helium$findStaticField(thisClass, "tabFrameSelectedTab");
            if (this.helium$pagesField == null) {
                HeliumClient.LOGGER.debug("reeses compat: pages field not found");
                return;
            }
            this.helium$pagesField.setAccessible(true);
            if (this.helium$tabSelectedField != null) {
                this.helium$tabSelectedField.setAccessible(true);
            }
            this.helium$heliumPage = HeliumSodiumLegacyPage.createHeliumPage();
            if (this.helium$heliumPage == null) {
                return;
            }
            this.helium$heliumPageName = this.helium$heliumPage.getClass().getMethod("getName", new Class[0]).invoke(this.helium$heliumPage, new Object[0]);
            List pages = (List)this.helium$pagesField.get(this);
            pages.add(this.helium$heliumPage);
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.debug("reeses compat: init failed - {}", (Object)t.getMessage());
        }
    }

    @Inject(method={"updateControls"}, at={@At(value="HEAD")}, require=0)
    private void helium$checkTabSwitch(CallbackInfo ci) {
        try {
            boolean isHeliumTab;
            if (this.helium$heliumPage == null || this.helium$tabSelectedField == null) {
                return;
            }
            AtomicReference tabRef = (AtomicReference)this.helium$tabSelectedField.get(null);
            if (tabRef == null) {
                return;
            }
            Object selected = tabRef.get();
            if (selected == null) {
                return;
            }
            boolean bl = isHeliumTab = selected == this.helium$heliumPageName;
            if (!isHeliumTab) {
                try {
                    String s = (String)selected.getClass().getMethod("getString", new Class[0]).invoke(selected, new Object[0]);
                    isHeliumTab = "Helium".equals(s);
                }
                catch (Throwable ignored) {
                    isHeliumTab = selected.toString().contains("Helium");
                }
            }
            if (isHeliumTab) {
                List pages = (List)this.helium$pagesField.get(this);
                for (Object page : pages) {
                    if (page == this.helium$heliumPage) continue;
                    Object pageName = page.getClass().getMethod("getName", new Class[0]).invoke(page, new Object[0]);
                    tabRef.set(pageName);
                    break;
                }
                class_437 parent = (class_437)this;
                class_310.method_1551().execute(() -> class_310.method_1551().method_1507(HeliumConfigScreen.create(parent)));
            }
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.debug("reeses compat: updateControls check failed - {}", (Object)t.getMessage());
        }
    }

    @Unique
    private Field helium$findField(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            }
            catch (NoSuchFieldException e) {
                continue;
            }
        }
        return null;
    }

    @Unique
    private Field helium$findStaticField(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                if (!Modifier.isStatic(f.getModifiers())) continue;
                return f;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
        }
        return null;
    }
}

