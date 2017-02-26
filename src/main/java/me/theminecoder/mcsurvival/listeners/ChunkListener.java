package me.theminecoder.mcsurvival.listeners;

import me.theminecoder.mcsurvival.managers.WarpPadManager;
import me.theminecoder.mcsurvival.objects.WarpPad;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * @author theminecoder
 * @version 1.0
 */
public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        WarpPadManager.getWarpPads().stream().filter(warpPad ->
                event.getChunk().getX() == warpPad.getLocation().getBlockX() >> 4 &&
                        event.getChunk().getZ() == warpPad.getLocation().getBlockZ() >> 4).forEach(WarpPad::spawn);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        WarpPadManager.getWarpPads().stream().filter(warpPad ->
                event.getChunk().getX() == warpPad.getLocation().getBlockX() >> 4 &&
                        event.getChunk().getZ() == warpPad.getLocation().getBlockZ() >> 4).forEach(WarpPad::despawn);
    }

}
