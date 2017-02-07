package me.theminecoder.mcsurvival;

import com.google.gson.Gson;
import me.theminecoder.mcsurvival.listeners.ChatListener;
import me.theminecoder.mcsurvival.listeners.DeathListener;
import me.theminecoder.mcsurvival.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public final class Survival extends JavaPlugin {

    private static Survival instance;

    private Gson gson;

    @Override
    public void onEnable() {
        instance = this;

        this.getDataFolder().mkdirs();

        gson = new Gson();

        Stream.of(
                new ChatListener(),
                new DeathListener(),
                new JoinListener()
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
    }

    public static Survival getInstance() {
        return instance;
    }

    public Gson getGson() {
        return gson;
    }
}
