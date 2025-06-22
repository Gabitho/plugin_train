package com.firstplugin.listeners;

import com.firstplugin.Main;
import com.firstplugin.utils.XPUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class tntshoot implements Listener {

    private final JavaPlugin plugin;

    public tntshoot(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

    Player player = event.getPlayer();
    EquipmentSlot handUsed = event.getHand();

    // Main ou offhand
    ItemStack item = handUsed == EquipmentSlot.HAND
        ? player.getInventory().getItemInMainHand()
        : player.getInventory().getItemInOffHand();

    if (item == null || !item.hasItemMeta()) return;

    ItemMeta meta = item.getItemMeta();
    NamespacedKey key = new NamespacedKey(plugin, "tnt_launcher");

    if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) return;

    // Vérifie XP
    if (!XPUtils.removeXP(player, 30)) {
        player.sendMessage("§cPas assez d'expérience !");
        return;
    }

    // Fait apparaître la TNT
    TNTPrimed tnt = player.getWorld().spawn(
        player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(1.2)),
        TNTPrimed.class
    );

    Vector direction = player.getLocation().getDirection().normalize().multiply(0.5); // moins rapide
    direction.setY(0.2); // vers l’avant mais légèrement vers le haut
    tnt.setVelocity(direction);
    tnt.setFuseTicks(40);

    // Stoppe la TNT après un petit délai si elle est presque arrêtée
    new BukkitRunnable() {
        @Override
        public void run() {
            if (tnt.isDead()) return;
            if (tnt.getVelocity().lengthSquared() < 0.01) {
                tnt.setVelocity(new Vector(0, 0, 0));
            }
        }
    }.runTaskLater(plugin, 10); // après 10 ticks (~0.5s)
}

}
