/**
 * MessageCommand.java
 * 21:00:41 PM
 * Created by Phineas
 * All rights reserved.
 * Some files may be under a Creative Commons License.
 */
package me.theminecoder.mcsurvival.commands.message;

import com.google.common.base.Joiner;
import me.theminecoder.mcsurvival.managers.PlayerManager;
import me.theminecoder.mcsurvival.objects.SurvivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        SurvivalPlayer survivalPlayer = PlayerManager.getPlayer(player);

        if(args.length < 1) {
            player.sendMessage(ChatColor.RED + "Please specify a reply message! /r <message>");
        } else {
            if (survivalPlayer.getLastMessaged() != null) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(survivalPlayer.getLastMessaged());
                if (!op.isOnline()) {
                    player.sendMessage(ChatColor.RED + "The player " + op.getName() + " is now offline!");

                    survivalPlayer.setLastMessaged(null);
                    return true;
                }

                String reason = Joiner.on(" ").join(args);

                Player target = op.getPlayer();
                player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "You"
                        + ChatColor.DARK_AQUA + " -> " + ChatColor.BLUE.toString() + ChatColor.BOLD +
                        target.getName() + ChatColor.GRAY + " " + reason);
                target.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD
                        + player.getName() + ChatColor.DARK_AQUA + " -> " + ChatColor.BLUE.toString()
                        + ChatColor.BOLD + "You" + ChatColor.GRAY + " "
                        + reason);

            } else {
                player.sendMessage(ChatColor.RED + "You have nobody to reply to!");
                return true;
            }
        }
        return true;
    }
}