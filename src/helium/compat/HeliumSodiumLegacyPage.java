/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_5250
 */
package com.helium.compat;

import com.helium.HeliumClient;
import java.lang.reflect.Constructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_5250;

@Environment(value=EnvType.CLIENT)
public final class HeliumSodiumLegacyPage {
    private HeliumSodiumLegacyPage() {
    }

    public static Object createHeliumPage() {
        try {
            Class<?> optionClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.Option");
            Class<?> optionGroupClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionGroup");
            Class<?> immutableListClass = Class.forName("com.google.common.collect.ImmutableList");
            Object emptyOptions = immutableListClass.getMethod("of", new Class[0]).invoke(null, new Object[0]);
            Constructor<?> groupCtor = optionGroupClass.getDeclaredConstructor(immutableListClass);
            groupCtor.setAccessible(true);
            Object emptyGroup = groupCtor.newInstance(emptyOptions);
            Object groupsList = immutableListClass.getMethod("of", Object.class).invoke(null, emptyGroup);
            Class<?> optionPageClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionPage");
            class_5250 heliumName = class_2561.method_43470((String)"Helium");
            Constructor<?> pageCtor = optionPageClass.getConstructor(class_2561.class, immutableListClass);
            return pageCtor.newInstance(heliumName, groupsList);
        }
        catch (Throwable t) {
            HeliumClient.LOGGER.debug("helium legacy sodium: failed to create helium page - {}", (Object)t.getMessage());
            return null;
        }
    }
}

