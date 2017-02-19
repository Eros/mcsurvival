package me.theminecoder.mcsurvival.gui;

import me.theminecoder.mcsurvival.Survival;
import me.theminecoder.mcsurvival.managers.WarpPadManager;
import me.theminecoder.mcsurvival.objects.WarpPad;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author theminecoder
 * @version 1.0
 */
public class WarpListGUI extends PagedGUI {

    WarpPad pad;
    Player player;

    public WarpListGUI(WarpPad pad, Player player) {
        super("Warp Pad List", 36);
        this.pad = pad;
        this.player = player;
        this.setUpdateTicks(10);
    }

    @Override
    protected List<ItemStack> getIcons() {
        return WarpPadManager.getWarpPads().stream()
                .filter(otherPad -> otherPad != pad)
                .filter(otherPad -> otherPad.canSee(player))
                .map(otherPad -> {
                    ItemStack icon = new ItemStack(Material.EYE_OF_ENDER);
                    ItemMeta iconMeta = icon.getItemMeta();
                    iconMeta.setDisplayName(ChatColor.WHITE + otherPad.getName());
                    iconMeta.setLore(Collections.singletonList(ChatColor.GRAY + Survival.getInstance().getProfileCache().getIfPresent(otherPad.getOwner()).getName()));
                    icon.setItemMeta(iconMeta);
                    return icon;
                }).collect(Collectors.toList());
    }

    @Override
    protected void onPlayerClickIcon(InventoryClickEvent event) {
        if (event.getRawSlot() == 35) {
            if (pad.getOwner().equals(player.getUniqueId())) {
                this.close();
                this.scheduleOpen(new WarpEditGUI(pad, player), player);
            }
        }

        ItemStack stack = event.getCurrentItem();
        if (stack == null || stack.getType() != Material.EYE_OF_ENDER) {
            return;
        }

        String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
        UUID owner;
        try {
            owner = Survival.getInstance().getProfileService().findByName(ChatColor.stripColor(stack.getItemMeta().getLore().get(0))).getUniqueId();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        WarpPadManager.getWarpPads().stream()
                .filter(otherPad -> otherPad != pad)
                .filter(otherPad -> otherPad.getOwner().equals(owner) && otherPad.getName().equals(name))
                .findFirst().ifPresent(otherPad -> {
            this.close();
            player.teleport(otherPad.getLocation().add(0.5, 0.5, 0.5));
        });
    }

    @Override
    protected void populateSpecial() {
        if (pad.getOwner().equals(player.getUniqueId())) {
            ItemStack editIcon = new ItemStack(Material.ANVIL);
            ItemMeta editIconMeta = editIcon.getItemMeta();
            editIconMeta.setDisplayName(ChatColor.GOLD + "Edit this warp pad");
            editIcon.setItemMeta(editIconMeta);

            this.inventory.setItem(35, editIcon);
        }
    }
}
