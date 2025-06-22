package com.firstplugin.listeners;

import com.firstplugin.utils.XPUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class tntshoot implements Listener {

    private final JavaPlugin plugin;

    public tntshoot(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getBow() == null || !event.getBow().hasItemMeta()) return;

        ItemMeta meta = event.getBow().getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "tnt_launcher");

        if (!container.has(key, PersistentDataType.STRING)) return;
        if (!"tnt_launcher".equals(container.get(key, PersistentDataType.STRING))) return;


        // Vérifie l'XP
        int xpCost = 30;
        if (!XPUtils.removeXP(player, xpCost)) {
            player.sendMessage("§cPas assez d'expérience !");
            event.setCancelled(true);
            return;
        }

        // Annule le tir de la flèche
        event.setCancelled(true);

        // Fait apparaître la TNT
        TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
        tnt.setVelocity(event.getProjectile().getVelocity());
        tnt.setFuseTicks(40);
    }
}
