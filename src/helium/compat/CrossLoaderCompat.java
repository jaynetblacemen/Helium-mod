/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.helium.compat;

import com.helium.HeliumClient;
import com.helium.mixin.compat.GameOptionsAccessor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_304;
import net.minecraft.class_310;
import org.apache.commons.lang3.ArrayUtils;

@Environment(value=EnvType.CLIENT)
public final class CrossLoaderCompat {
    private static final List<Runnable> tickhandlers = new ArrayList<Runnable>();
    private static final List<class_304> pendingkeybindings = new ArrayList<class_304>();
    private static boolean fabrickeybindingavailable = false;
    private static boolean fabrictickavailable = false;
    private static boolean keybindingsregistered = false;

    public static class_304 registerkeybinding(class_304 keybinding) {
        if (fabrickeybindingavailable) {
            return CrossLoaderCompat.registerwithfabricapi(keybinding);
        }
        pendingkeybindings.add(keybinding);
        return keybinding;
    }

    private static class_304 registerwithfabricapi(class_304 keybinding) {
        try {
            Class<?> helper = Class.forName("net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper");
            Method register = helper.getMethod("registerKeyBinding", class_304.class);
            return (class_304)register.invoke(null, keybinding);
        }
        catch (Exception e) {
            HeliumClient.LOGGER.warn("fabric api keybinding failed, deferring: {}", (Object)e.getMessage());
            pendingkeybindings.add(keybinding);
            return keybinding;
        }
    }

    public static void registerpendingkeybindings() {
        if (keybindingsregistered || pendingkeybindings.isEmpty()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1690 == null) {
            return;
        }
        try {
            GameOptionsAccessor accessor = (GameOptionsAccessor)client.field_1690;
            Object[] current = accessor.helium$getallkeys();
            for (class_304 keybinding : pendingkeybindings) {
                current = (class_304[])ArrayUtils.add((Object[])current, (Object)keybinding);
            }
            accessor.helium$setallkeys((class_304[])current);
            keybindingsregistered = true;
            HeliumClient.LOGGER.info("registered {} keybindings via vanilla fallback", (Object)pendingkeybindings.size());
            pendingkeybindings.clear();
        }
        catch (Exception e) {
            HeliumClient.LOGGER.warn("deferred keybinding registration failed: {}", (Object)e.getMessage());
        }
    }

    public static void registertickevent(Runnable handler) {
        if (fabrictickavailable) {
            CrossLoaderCompat.registertickwithfabricapi(handler);
        } else {
            tickhandlers.add(handler);
        }
    }

    private static void registertickwithfabricapi(Runnable handler) {
        try {
            Class<?> events = Class.forName("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents");
            Object endtick = events.getField("END_CLIENT_TICK").get(null);
            Class<?> listenerclass = Class.forName("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents$EndTick");
            Object proxy = Proxy.newProxyInstance(listenerclass.getClassLoader(), new Class[]{listenerclass}, (p, method, args) -> {
                if ("onEndTick".equals(method.getName())) {
                    handler.run();
                }
                return null;
            });
            Method register = endtick.getClass().getMethod("register", listenerclass);
            register.invoke(endtick, proxy);
        }
        catch (Exception e) {
            HeliumClient.LOGGER.warn("fabric api tick event failed, using manual: {}", (Object)e.getMessage());
            tickhandlers.add(handler);
        }
    }

    public static void tick() {
        CrossLoaderCompat.registerpendingkeybindings();
        for (Runnable handler : tickhandlers) {
            try {
                handler.run();
            }
            catch (Exception e) {
                HeliumClient.LOGGER.debug("tick handler error: {}", (Object)e.getMessage());
            }
        }
    }

    public static boolean isfabrickeybindingavailable() {
        return fabrickeybindingavailable;
    }

    public static boolean isfabrictickavailable() {
        return fabrictickavailable;
    }

    static {
        try {
            Class.forName("net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper");
            fabrickeybindingavailable = true;
        }
        catch (ClassNotFoundException e) {
            fabrickeybindingavailable = false;
        }
        try {
            Class.forName("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents");
            fabrictickavailable = true;
        }
        catch (ClassNotFoundException e) {
            fabrictickavailable = false;
        }
    }
}

