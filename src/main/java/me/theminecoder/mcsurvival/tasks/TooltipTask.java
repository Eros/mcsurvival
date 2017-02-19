package me.theminecoder.mcsurvival.tasks;

import me.theminecoder.mcsurvival.managers.WarpPadManager;
import me.theminecoder.mcsurvival.objects.WarpPad;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author theminecoder
 * @version 1.0
 */
public class TooltipTask extends BukkitRunnable {
    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            String tooltip = null;

            if (WarpPadManager.getWarpPads().stream()
                    .filter(WarpPad::isVisible)
                    .filter(warpPad -> warpPad.getLocation().getWorld() == player.getWorld())
                    .anyMatch(warpPad -> warpPad.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) < 1.2)) {
                tooltip = ChatColor.AQUA + "Press 'f' to interact with the warp pad!";
            }

            if (tooltip != null) {
                player.sendActionBar(tooltip);
            }
        });
    }
}
