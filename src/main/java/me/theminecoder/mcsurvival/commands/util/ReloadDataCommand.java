package me.theminecoder.mcsurvival.commands.util;

import me.theminecoder.mcsurvival.Survival;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

/**
 * @author theminecoder
 * @version 1.0
 */
public class ReloadDataCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "Reloading data files...");
        try {
            Survival.getInstance().reload();
            sender.sendMessage(ChatColor.GREEN + "Reload done!");
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Reload failed, please check console!");
            e.printStackTrace();
        }
        return true;
    }
}
