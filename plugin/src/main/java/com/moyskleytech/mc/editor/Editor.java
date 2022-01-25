package com.moyskleytech.mc.editor;

import com.moyskleytech.mc.editor.listeners.GameModeListener;
import com.moyskleytech.mc.editor.utils.Logger;
import com.moyskleytech.mc.editor.utils.Logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.moyskleytech.mc.banking.VersionInfo;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.plugin.PluginContainer;
import org.screamingsandals.lib.utils.PlatformType;
import org.screamingsandals.lib.utils.annotations.Init;
import org.screamingsandals.lib.utils.annotations.Plugin;
import org.screamingsandals.lib.utils.annotations.PluginDependencies;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.npc.NPCManager;

import java.util.ArrayList;
import java.util.List;

@Plugin(id = "BWEditor", authors = { "boiscljo" }, loadTime = Plugin.LoadTime.POSTWORLD, version = VersionInfo.VERSION)
@PluginDependencies(platform = PlatformType.BUKKIT, dependencies = {"BedWars"}, softDependencies = { "ViaVersion"})
@Init(services = {
        Logger.class,
        GameModeListener.class,
        HologramManager.class,
        NPCManager.class
})
public class Editor extends PluginContainer {

    private static Editor instance;

    public static Editor getInstance() {
        return instance;
    }
    
    private JavaPlugin cachedPluginInstance;
    private final List<Listener> registeredListeners = new ArrayList<>();

    public static JavaPlugin getPluginInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("SBA has not yet been initialized!");
        }
        if (instance.cachedPluginInstance == null) {
            instance.cachedPluginInstance = (JavaPlugin) instance.getPluginDescription().as(JavaPlugin.class);
        }
        return instance.cachedPluginInstance;
    }

    @Override
    public void enable() {
        instance = this;
        cachedPluginInstance = instance.getPluginDescription().as(JavaPlugin.class);
        Logger.init(cachedPluginInstance);
    }

    @Override
    public void postEnable() {
        
        
        Logger.info("Plugin has finished loading!");
        Logger.info("BwEditor Initialized on JAVA {}", System.getProperty("java.version"));
        Logger.trace("API has been registered!");

        Logger.setMode(Level.ERROR);
    }

    public void registerListener(@NotNull Listener listener) {
        if (registeredListeners.contains(listener)) {
            return;
        }
        Bukkit.getServer().getPluginManager().registerEvents(listener, getPluginInstance());
        Logger.trace("Registered listener: {}", listener.getClass().getSimpleName());
    }

    public void unregisterListener(@NotNull Listener listener) {
        if (!registeredListeners.contains(listener)) {
            return;
        }
        HandlerList.unregisterAll(listener);
        registeredListeners.remove(listener);
        Logger.trace("Unregistered listener: {}", listener.getClass().getSimpleName());
    }

    public List<Listener> getRegisteredListeners() {
        return List.copyOf(registeredListeners);
    }

    @Override
    public void disable() {
        EventManager.getDefaultEventManager().unregisterAll();
        EventManager.getDefaultEventManager().destroy();
        Bukkit.getServer().getServicesManager().unregisterAll(getPluginInstance());
    }

    public boolean isSnapshot() {
        return getVersion().contains("SNAPSHOT") || getVersion().contains("dev");
    }

    public String getVersion() {
        return getPluginDescription().getVersion();
    }

    public JavaPlugin getJavaPlugin() {
        return instance.getPluginDescription().as(JavaPlugin.class);
    }

}
