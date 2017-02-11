package me.theminecoder.mcsurvival.commands.teleport;

import me.theminecoder.mcsurvival.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author ItsEternity
 * @version 1.0
 */
public class TpdenyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player bukkitPlayer = (Player) sender;

        if (PlayerManager.getTeleportCache().getIfPresent(bukkitPlayer.getUniqueId()) != null) {
            bukkitPlayer.sendMessage(ChatColor.YELLOW + "You have denied the teleport request");

            Player bukkitTarget = Bukkit.getPlayer(PlayerManager.getTeleportCache().getIfPresent(bukkitPlayer.getUniqueId()));
            if (bukkitTarget != null) {
                bukkitTarget.sendMessage(ChatColor.YELLOW + bukkitPlayer.getName() + " has denied your teleport request!");
            }

            PlayerManager.getTeleportCache().invalidate(bukkitPlayer.getUniqueId());

        } else {
            bukkitPlayer.sendMessage(ChatColor.RED + "You have no pending teleport requests! Has it been more than 120 seconds since your last request?");
        }
        return true;
    }
}
