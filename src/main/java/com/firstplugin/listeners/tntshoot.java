package com.firstplugin.listeners;

import com.firstplugin.utils.XPUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

    public tntshootListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getBow() == null) return;

        ItemMeta meta = event.getBow().getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey(plugin, "weapon_type");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(key, PersistentDataType.STRING)) return;

        String type = container.get(key, PersistentDataType.STRING);

        switch (type) {
            case "tnt_launcher" -> {
                int xpCost = 30;

                if (!XPUtils.removeXP(player, xpCost)) {
                    player.sendMessage("§cPas assez d'expérience !");
                    event.setCancelled(true);
                    return;
                }

                // Supprime la flèche
                event.getProjectile().remove();

                // Fait apparaître une TNT volante à la place
                Projectile flèche = (Projectile) event.getProjectile();

                TNTPrimed tnt = player.getWorld().spawn(flèche.getLocation(), TNTPrimed.class);
                tnt.setVelocity(flèche.getVelocity());
                tnt.setFuseTicks(40); // 2 secondes
            }

            case "fire_bow" -> {
                player.sendMessage("§6Tu tires une flèche enflammée !");
                // Plus tard : ajouter le comportement de feu ici
            }

            default -> {
                player.sendMessage("§7Arme inconnue détectée.");
            }
        }
    }
}
