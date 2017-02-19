package me.theminecoder.mcsurvival.gui;

import com.sk89q.squirrelid.Profile;
import com.sk89q.squirrelid.resolver.ProfileService;
import me.theminecoder.mcsurvival.Survival;
import me.theminecoder.mcsurvival.managers.WarpPadManager;
import me.theminecoder.mcsurvival.objects.WarpPad;
import net.md_5.bungee.api.ChatColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author theminecoder
 * @version 1.0
 */
public class WarpEditGUI extends GUI {

    WarpPad pad;
    Player player;

    public WarpEditGUI(WarpPad pad, Player player) {
        super("Edit Warp Pad", 36);
        this.pad = pad;
        this.player = player;
    }

    @Override
    protected void onPlayerClick(InventoryClickEvent event) {
        if (event.getRawSlot() == 11) {
            this.close();
            new AnvilGUI(Survival.getInstance(), player, "New Name", (player, name) -> {
                WarpPad otherPad = WarpPadManager.getWarpPads().stream()
                        .filter(warpPad -> warpPad.getOwner().equals(player.getUniqueId()))
                        .filter(warpPad -> warpPad.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (otherPad != null) {
                    return "You already have a warp pad with this name!";
                }
                pad.setName(name);
                if (pad.isVisible()) {
                    pad.despawn();
                    pad.spawn();
                }
                this.scheduleOpen(new WarpEditGUI(pad, player), player);
                return null;
            });
            return;
        }

        if (event.getRawSlot() == 13) {
            int id = this.pad.getVisibility().ordinal();
            if ((id + 1) == WarpPad.Visibility.values().length) {
                id = 0;
            } else {
                id++;
            }
            int finalId = id;
            this.pad.setVisibility(Arrays.stream(WarpPad.Visibility.values()).filter(vis -> vis.ordinal() == finalId).findFirst().get());
            this.repopulate();
            return;
        }

        if (event.getRawSlot() == 15) {
            this.close();
            new AnvilGUI(Survival.getInstance(), player, "New Owner", (player, name) -> {
                Profile profile;
                try {
                    profile = Survival.getInstance().getProfileService().findByName(name);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error finding player profile!";
                }
                if (profile == null) {
                    return "Player not found";
                }

                pad.setOwner(profile.getUniqueId());
                this.scheduleOpen(new WarpListGUI(pad, player), player);
                return null;
            });
            return;
        }

        if (event.getRawSlot() == 31) {
            this.close();
            this.scheduleOpen(new WarpListGUI(pad, player), player);
        }
    }

    @Override
    protected void populate() {
        {
            ItemStack icon = new ItemStack(Material.NAME_TAG);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(ChatColor.GOLD + "Edit name");
            iconMeta.setLore(Collections.singletonList(ChatColor.GRAY + pad.getName()));
            icon.setItemMeta(iconMeta);
            this.inventory.setItem(11, icon);
        }

        {
            ItemStack icon = new ItemStack(pad.getVisibility() == WarpPad.Visibility.PUBLIC ? Material.MAP : Material.EMPTY_MAP);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(ChatColor.GOLD + "Change Viability");
            iconMeta.setLore(Collections.singletonList(ChatColor.GRAY + pad.getVisibility().getName()));
            icon.setItemMeta(iconMeta);
            this.inventory.setItem(13, icon);
        }

        {
            ItemStack icon = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(ChatColor.GOLD + "Edit Owner");
            iconMeta.setLore(Collections.singletonList(ChatColor.GRAY + Bukkit.getOfflinePlayer(pad.getOwner()).getName()));
            icon.setItemMeta(iconMeta);
            this.inventory.setItem(15, icon);
        }

        {
            ItemStack icon = new ItemStack(Material.ARROW);
            ItemMeta arrowMeta = icon.getItemMeta();
            arrowMeta.setDisplayName(ChatColor.WHITE + "<- Back");
            icon.setItemMeta(arrowMeta);
            this.inventory.setItem(31, icon);
        }
    }
}
