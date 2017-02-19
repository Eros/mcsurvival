package me.theminecoder.mcsurvival.objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.UUID;

/**
 * @author theminecoder
 * @version 1.0
 */
public class WarpPad {

    public static final ItemStack HAND_ITEM = new ItemStack(Material.STEP, 1, (short) 0, (byte) 0);

    static {
        final ItemMeta HAND_ITEM_META = HAND_ITEM.getItemMeta();
        HAND_ITEM_META.setDisplayName(ChatColor.WHITE + "Warp Pad");
        HAND_ITEM_META.addEnchant(Enchantment.DURABILITY, 10, true);
        HAND_ITEM_META.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        HAND_ITEM.setItemMeta(HAND_ITEM_META);
    }

    private UUID owner;
    private String name;
    private Visibility visibility = Visibility.PRIVATE;

    private int x, y, z;
    private String world;

    private transient boolean visible;
    private transient ArmorStand stand;

    WarpPad() {
    }

    public WarpPad(UUID owner, String name, Location location) {
        this.owner = owner;
        this.name = name;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getName();
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean canSee(Player player) {
        return visibility == Visibility.PUBLIC || owner.equals(player.getUniqueId());
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public ArmorStand getDisplayStand() {
        return stand;
    }

    public void spawn() {
        this.getLocation().getBlock().setType(Material.STEP);

        if (this.stand != null) {
            this.stand.remove();
        }

        stand = this.getLocation().getWorld().spawn(this.getLocation().add(0.5, 0.7, 0.5), ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(ChatColor.AQUA + this.name + " - Warp Pad");
        stand.setCustomNameVisible(true);
        stand.setHeadPose(new EulerAngle(Math.toRadians(180), 0, 0)); // If you can figure out how to center this, be my guest.
        stand.setHelmet(new ItemStack(Material.EYE_OF_ENDER));
        stand.setChestplate(new ItemStack(Material.EYE_OF_ENDER));

        this.visible = true;
    }

    public void despawn() {
        if (stand != null) {
            stand.remove();
            stand = null;
        }

        this.getLocation().getBlock().setType(Material.AIR);

        this.visible = false;
    }

    public enum Visibility {
        PRIVATE("Private"),
        //FRIENDS("Friends Only"), //TODO Friend system?
        PUBLIC("Public");

        private String name;

        Visibility(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
