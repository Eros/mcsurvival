package me.theminecoder.mcsurvival;

import com.google.gson.Gson;
import com.sk89q.squirrelid.cache.ProfileCache;
import com.sk89q.squirrelid.cache.SQLiteCache;
import com.sk89q.squirrelid.resolver.CacheForwardingService;
import com.sk89q.squirrelid.resolver.HttpRepositoryService;
import com.sk89q.squirrelid.resolver.ProfileService;
import me.theminecoder.mcsurvival.commands.message.MessageCommand;
import me.theminecoder.mcsurvival.commands.message.ReplyCommand;
import me.theminecoder.mcsurvival.commands.teleport.TpaCommand;
import me.theminecoder.mcsurvival.commands.teleport.TpacceptCommand;
import me.theminecoder.mcsurvival.commands.teleport.TpdenyCommand;
import me.theminecoder.mcsurvival.commands.util.ReloadDataCommand;
import me.theminecoder.mcsurvival.listeners.*;
import me.theminecoder.mcsurvival.managers.PlayerManager;
import me.theminecoder.mcsurvival.managers.WarpPadManager;
import me.theminecoder.mcsurvival.objects.SurvivalPlayer;
import me.theminecoder.mcsurvival.objects.WarpPad;
import me.theminecoder.mcsurvival.tasks.TooltipTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public final class Survival extends JavaPlugin {

    private static Survival instance;

    private Gson gson;
    private ProfileService profileService;
    private ProfileCache profileCache;

    public static Survival getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!this.getDataFolder().exists()) {
            if (!this.getDataFolder().mkdirs()) {
                throw new RuntimeException("Failed to create data directory!");
            }
        }

        gson = new Gson();

        try {
            profileCache = new SQLiteCache(new File(this.getDataFolder(), "uuidcache.sqlite"));
            profileService = new CacheForwardingService(
                    HttpRepositoryService.forMinecraft(),
                    profileCache
            );
        } catch (IOException e) {
            this.getLogger().severe("Error loading player name cache!");
            e.printStackTrace();
            return;
        }

        try {
            this.reload();
        } catch (IOException e) {
            this.getLogger().severe("Error loading manager data!");
            e.printStackTrace();
            return;
        }

        new TooltipTask().runTaskTimerAsynchronously(this, 0, 5);

        Stream.of(
                new BlockListener(),
                new ChatListener(),
                new ChunkListener(),
                new DeathListener(),
                new InteractListener(),
                new JoinListener()
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        Bukkit.getPluginCommand("tpa").setExecutor(new TpaCommand());
        Bukkit.getPluginCommand("tpaccept").setExecutor(new TpacceptCommand());
        Bukkit.getPluginCommand("tpdeny").setExecutor(new TpdenyCommand());
        Bukkit.getPluginCommand("msg").setExecutor(new MessageCommand());
        Bukkit.getPluginCommand("r").setExecutor(new ReplyCommand());
        Bukkit.getPluginCommand("reloaddata").setExecutor(new ReloadDataCommand());

        Bukkit.addRecipe(new ShapedRecipe(WarpPad.HAND_ITEM).shape(
                "E",
                "S"
        ).setIngredient('E', Material.EYE_OF_ENDER).setIngredient('S', new MaterialData(Material.STEP, (byte) 0)));
    }

    @Override
    public void onDisable() {
        try {
            this.save();
        } catch (IOException e) {
            this.getLogger().severe("Error saving manager data!");
            e.printStackTrace();
        }

        try {
            for (SurvivalPlayer survivalPlayer : PlayerManager.getPlayerMap().values()) {
                try (FileWriter writer = new FileWriter(new File(this.getDataFolder(), survivalPlayer.getUUID().toString() + ".json"))) {
                    getGson().toJson(survivalPlayer, writer);
                }
            }
        } catch (IOException e) {
            this.getLogger().severe("Error saving user data");
            e.printStackTrace();
        }

        WarpPadManager.getWarpPads().forEach(WarpPad::despawn);
    }

    public void reload() throws IOException {
        File warpPadData = new File(this.getDataFolder(), "warppads.json");
        WarpPadManager.getWarpPads().clear();
        if (warpPadData.exists()) {
            try (FileReader reader = new FileReader(warpPadData)) {
                Arrays.stream(getGson().fromJson(reader, WarpPad[].class)).forEach(warpPad -> {
                    if (!warpPad.getLocation().getChunk().isLoaded()) {
                        warpPad.getLocation().getChunk().load();
                    }
                    warpPad.spawn();
                    WarpPadManager.getWarpPads().add(warpPad);
                });
            }
        }
    }

    public void save() throws IOException {
        File warpPadData = new File(this.getDataFolder(), "warppads.json");
        try (FileWriter writer = new FileWriter(warpPadData)) {
            getGson().toJson(WarpPadManager.getWarpPads().stream().toArray(WarpPad[]::new), writer);
        }

    }

    public Gson getGson() {
        return gson;
    }

    public ProfileService getProfileService() {
        return profileService;
    }

    public ProfileCache getProfileCache() {
        return profileCache;
    }
}
