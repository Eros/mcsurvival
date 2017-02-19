package me.theminecoder.mcsurvival.listeners;

import me.theminecoder.mcsurvival.Survival;
import me.theminecoder.mcsurvival.managers.WarpPadManager;
import me.theminecoder.mcsurvival.objects.WarpPad;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author theminecoder
 * @version 1.0
 */
public class BlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        WarpPad pad = WarpPadManager.getWarpPads().stream().filter(warpPad ->
                warpPad.getLocation().getWorld() == event.getBlock().getWorld() &&
                        warpPad.getLocation().getBlockX() == event.getBlock().getX() &&
                        warpPad.getLocation().getBlockY() == event.getBlock().getY() &&
                        warpPad.getLocation().getBlockZ() == event.getBlock().getZ())
                .findFirst().orElse(null);
        if (pad != null) {
            event.setCancelled(true);
            if (pad.getOwner().equals(event.getPlayer().getUniqueId()) || event.getPlayer().isOp()) {
                pad.despawn();
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    pad.getLocation().getWorld().dropItemNaturally(pad.getLocation().add(0.5, 0.5, 0.5), WarpPad.HAND_ITEM.clone());
                }
                WarpPadManager.getWarpPads().remove(pad);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (WarpPad.HAND_ITEM.isSimilar(event.getItemInHand())) {
            new AnvilGUI(Survival.getInstance(), event.getPlayer(), "Warp Name", (player, name) -> {
                WarpPad pad = WarpPadManager.getWarpPads().stream()
                        .filter(warpPad -> warpPad.getOwner().equals(event.getPlayer().getUniqueId()))
                        .filter(warpPad -> warpPad.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (pad != null) {
                    return "You already have a warp pad with this name!";
                }
                pad = new WarpPad(event.getPlayer().getUniqueId(), name, event.getBlockPlaced().getLocation());
                pad.spawn();
                WarpPadManager.getWarpPads().add(pad);
                return null;
            });
        }
    }

}
