package com.firstplugin.listeners;

import com.firstplugin.weapons.TNTLauncher;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BowShootListener implements Listener {

    private final JavaPlugin plugin;

    public BowShootListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Vérifie si c’est bien notre TNTLauncher
        NamespacedKey key = new NamespacedKey(plugin, "tnt_launcher");
        PersistentDataContainer container = event.getBow().getItemMeta().getPersistentDataContainer();
        if (!container.has(key, PersistentDataType.BYTE)) return;

        // Vérifie s’il a assez d’XP
        if (player.getLevel() < 3) {
            player.sendMessage("§cPas assez d'XP !");
            event.setCancelled(true);
            return;
        }

        // Consomme 3 niveaux d’XP
        player.setLevel(player.getLevel() - 3);

        // Supprime la flèche
        event.getProjectile().remove();

        // Fait apparaître une TNT volante à la place
        Projectile flèche = (Projectile) event.getProjectile();

        TNTPrimed tnt = player.getWorld().spawn(flèche.getLocation(), TNTPrimed.class);
        tnt.setVelocity(flèche.getVelocity());
        tnt.setFuseTicks(40); // 2 secondes
    }
}
