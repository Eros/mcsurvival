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
public class TpacceptCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player bukkitPlayer = (Player) sender; //person who got the TP request

        if (PlayerManager.getTeleportCache().getIfPresent(bukkitPlayer.getUniqueId())!= null) {

            Player bukkitTarget = Bukkit.getPlayer(PlayerManager.getTeleportCache().getIfPresent(bukkitPlayer.getUniqueId()));

            if (bukkitTarget == null) {
                bukkitPlayer.sendMessage(ChatColor.RED + "The player that requested the teleport went offline!");

            } else {
                bukkitTarget.teleport(bukkitPlayer);
                bukkitTarget.sendMessage(ChatColor.YELLOW + "You have teleported to " + bukkitPlayer.getName());
                bukkitPlayer.sendMessage(ChatColor.YELLOW + bukkitTarget.getName() + " has teleported to you!");
                PlayerManager.getTeleportCache().invalidate(bukkitPlayer.getUniqueId());

            }
        } else {
            bukkitPlayer.sendMessage(ChatColor.RED + "You have no pending teleport requests! Has it been more than 120 seconds since your last request?");
        }

        return true;
    }
}
