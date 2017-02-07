package me.theminecoder.mcsurvival.listeners;

import me.theminecoder.mcsurvival.Survival;
import me.theminecoder.mcsurvival.managers.PlayerManager;
import me.theminecoder.mcsurvival.objects.SurvivalPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Stream;

/**
 * @author theminecoder
 * @version 1.0
 */
public class JoinListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        SurvivalPlayer player = null;
        try {
            File loadFile = new File(Survival.getInstance().getDataFolder(), event.getUniqueId().toString() + ".json");
            if (loadFile.exists()) {
                player = Survival.getInstance().getGson().fromJson(
                        new FileReader(loadFile),
                        SurvivalPlayer.class
                );
            }
        } catch (IOException ignored) {
        }

        if (player == null) {
            player = new SurvivalPlayer(event.getUniqueId(), event.getName());
        }

        // Anyone wishing to add checks please do so here so they
        // get caught before the user object is added to the map.

        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        PlayerManager.getPlayerMap().put(event.getUniqueId(), player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player bukkitPlayer = event.getPlayer();
        SurvivalPlayer player = PlayerManager.getPlayer(bukkitPlayer);

        if (player == null) { // Fix for auto reconnect
            AsyncPlayerPreLoginEvent fakeEvent = new AsyncPlayerPreLoginEvent(
                    bukkitPlayer.getName(),
                    bukkitPlayer.getAddress().getAddress(),
                    bukkitPlayer.getUniqueId()
            );
            this.onAsyncPlayerPreLogin(fakeEvent);
            if (fakeEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                bukkitPlayer.kickPlayer(fakeEvent.getKickMessage());
                return;
            } else {
                player = PlayerManager.getPlayer(bukkitPlayer);
            }
        }

        player.setLastJoin(new Date());

        Stream.of(
                ChatColor.YELLOW + "Welcome to theminecoder's survival server!",
                "",
                ChatColor.YELLOW + "This server is comprised of an open source plugin which anyone can edit if they wish to.",
                ChatColor.YELLOW + "Check it out here: " + ChatColor.AQUA + "https://github.com/theminecoder/mcsurvival"
        ).forEach(event.getPlayer()::sendMessage);

        if (!player.hasReceivedStarterKit()) {
            player.setReceivedStarterKit(true);
            bukkitPlayer.sendMessage(ChatColor.BLUE + "Since we see this is your first time, you have received the starter kit!");
            bukkitPlayer.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
            bukkitPlayer.getInventory().addItem(new ItemStack(Material.STONE_AXE));
            bukkitPlayer.getInventory().addItem(new ItemStack(Material.APPLE, 5));

        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        SurvivalPlayer player = PlayerManager.getPlayerMap().remove(event.getPlayer().getUniqueId());
        try (FileWriter writer = new FileWriter(new File(Survival.getInstance().getDataFolder(), event.getPlayer().getUniqueId() + ".json"))) {
            Survival.getInstance().getGson().toJson(
                    player,
                    writer
            );
        } catch (IOException e) {
            Survival.getInstance().getLogger().severe("Could not save data of user " + player);
            e.printStackTrace();
        }
    }
}
