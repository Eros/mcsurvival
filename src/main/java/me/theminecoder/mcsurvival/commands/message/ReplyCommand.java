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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Please specify a reply message! /r <message>");
            return true;
        }

        if (survivalPlayer.getLastMessaged() == null) {
            player.sendMessage(ChatColor.RED + "You have nobody to reply to!");
            return true;
        }

        Player target = Bukkit.getPlayer(survivalPlayer.getLastMessaged());
        if (target == null) {
            player.sendMessage(ChatColor.RED + "The player " + survivalPlayer.getLastMessaged() + " is now offline!");
            return true;
        }

        String message = Joiner.on(" ").join(args);

        player.spigot().sendMessage(
                new ComponentBuilder(
                        ChatColor.BLUE.toString() + ChatColor.BOLD + "You"
                                + ChatColor.DARK_AQUA + " -> "
                                + ChatColor.BLUE.toString() + ChatColor.BOLD + target.getName()
                                + ChatColor.GRAY + " " + message
                ).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + target.getName() + " ")).create()
        );

        target.spigot().sendMessage(
                new ComponentBuilder(
                        ChatColor.BLUE.toString() + ChatColor.BOLD + player.getName()
                                + ChatColor.DARK_AQUA + " -> "
                                + ChatColor.BLUE.toString() + ChatColor.BOLD + "You"
                                + ChatColor.GRAY + " " + message
                ).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " ")).create()
        );

        return true;
    }
}