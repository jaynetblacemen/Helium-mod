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
import java.util.List;
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
@Mixin(targets={"net/caffeinemc/mods/sodium/client/gui/SodiumOptionsGUI"}, remap=false)
public abstract class SodiumOptionsGUILegacyMixin {
    @Unique
    private Object helium$dummyPage;
    @Unique
    private Object helium$previousPage;
    @Unique
    private Field helium$pagesField;
    @Unique
    private Field helium$currentPageField;

    @Inject(method={"<init>"}, at={@At(value="TAIL")}, require=0)
    private void helium$addHeliumPage(class_437 prevScreen, CallbackInfo ci) {
        try {
            Class<?> thisClass = this.getClass();
            this.helium$pagesField = this.helium$findField(thisClass, "pages");
            this.helium$currentPageField = this.helium$findField(thisClass, "currentPage");
            if (this.helium$pagesField == null || this.helium$currentPageField == null) {
                HeliumClient.LOGGER.debug("legacy sodium compat: could not find pages/currentPage fields");
                return;
            }
            this.helium$pagesField.setAccessible(true);
            this.helium$currentPageField.setAccessible(true);
            this.helium$dummyPage = HeliumSodiumLegacyPage.createHeliumPage();
            if (this.helium$dummyPage == null) {
                return;
            }
            List pages = (List)this.helium$pagesField.get(this);
            pages.add(this.helium$dummyPage);
            this.helium$previousPage = this.helium$currentPageField.get(this);
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.debug("legacy sodium compat: failed to add helium page - {}", (Object)t.getMessage());
        }
    }

    @Inject(method={"rebuildGUI"}, at={@At(value="HEAD")}, require=0, cancellable=true)
    private void helium$interceptRebuildGUI(CallbackInfo ci) {
        try {
            if (this.helium$dummyPage == null || this.helium$currentPageField == null || this.helium$pagesField == null) {
                return;
            }
            Object currentPage = this.helium$currentPageField.get(this);
            if (currentPage != null && currentPage != this.helium$dummyPage) {
                this.helium$previousPage = currentPage;
            }
            if (currentPage == this.helium$dummyPage) {
                List pages = (List)this.helium$pagesField.get(this);
                Object restorePage = this.helium$previousPage != null ? this.helium$previousPage : pages.get(0);
                this.helium$currentPageField.set(this, restorePage);
                ci.cancel();
                class_437 parentScreen = (class_437)this;
                class_310.method_1551().execute(() -> class_310.method_1551().method_1507(HeliumConfigScreen.create(parentScreen)));
            }
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.debug("legacy sodium compat: rebuildGUI intercept failed - {}", (Object)t.getMessage());
        }
    }

    @Unique
    private Field helium$findField(Class<?> clazz, String name) {
        for (Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField(name);
            }
            catch (NoSuchFieldException e) {
                continue;
            }
        }
        return null;
    }
}

