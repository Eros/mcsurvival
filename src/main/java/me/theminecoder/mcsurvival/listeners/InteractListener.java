package me.theminecoder.mcsurvival.listeners;

import me.theminecoder.mcsurvival.gui.WarpListGUI;
import me.theminecoder.mcsurvival.managers.WarpPadManager;
import me.theminecoder.mcsurvival.objects.WarpPad;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Objects;

/**
 * @author theminecoder
 * @version 1.0
 */
public class InteractListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        onPlayerInteractEntityReal(event); // Why
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        onPlayerInteractEntityReal(event); // Bukkit
    }

    @EventHandler
    public void onPlayerInteractArmorStand(PlayerArmorStandManipulateEvent event) {
        onPlayerInteractEntityReal(event); // Why
    }

    private void onPlayerInteractEntityReal(PlayerInteractEntityEvent event) {
        if (WarpPadManager.getWarpPads().stream()
                .map(WarpPad::getDisplayStand)
                .filter(Objects::nonNull)
                .anyMatch(stand -> stand == event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onOffHandSwap(PlayerSwapHandItemsEvent event) {
        WarpPad pad = WarpPadManager.getWarpPads().stream()
                .filter(WarpPad::isVisible)
                .filter(warpPad -> warpPad.getLocation().getWorld() == event.getPlayer().getWorld())
                .filter(warpPad -> warpPad.getLocation().add(0.5, 0.5, 0.5).distance(event.getPlayer().getLocation()) < 1.2)
                .findFirst().orElse(null);
        if (pad != null) {
            event.setCancelled(true);
            new WarpListGUI(pad, event.getPlayer()).open(event.getPlayer());
        }
    }

}
