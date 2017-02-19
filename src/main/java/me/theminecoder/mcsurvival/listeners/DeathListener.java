package me.theminecoder.mcsurvival.listeners;

import me.theminecoder.mcsurvival.managers.WarpPadManager;
import me.theminecoder.mcsurvival.objects.WarpPad;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

/**
 * @author theminecoder
 * @version 1.0
 */
public class DeathListener implements Listener {

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (WarpPadManager.getWarpPads().stream().map(WarpPad::getDisplayStand)
                .filter(Objects::nonNull)
                .anyMatch(stand -> stand == event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

}
