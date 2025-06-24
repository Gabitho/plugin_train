package com.firstplugin.listeners;

import com.firstplugin.Main;
import com.firstplugin.utils.XPUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.event.block.Action;


import java.util.HashMap;
import java.util.UUID;

public class tntshoot implements Listener {

    private final JavaPlugin plugin;

    // On stocke le moment où le joueur commence à charger (clic droit)
    private final HashMap<UUID, Long> chargingPlayers = new HashMap<>();
    // Pour éviter le spam, on enregistre les derniers tirs
    private final HashMap<UUID, Long> lastFireTimes = new HashMap<>();

    private static final long COOLDOWN_MS = 1000; // 1 seconde entre 2 tirs
    private static final long MAX_CHARGE_MS = 2000; // charge max = 2 secondes

    public tntshoot(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return; // Éviter de détecter deux fois (main et offhand)

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isTNTLauncher(item)) return;

        UUID uuid = player.getUniqueId();

        // Enregistre le moment où le joueur commence à charger
        chargingPlayers.put(uuid, System.currentTimeMillis());

        // Animation client (bande l’arc)
        player.setCooldown(item.getType(), 9999); // Simule une animation de bande (mais le joueur ne pourra pas utiliser l’item tant qu’on n’annule pas)
    }

    @EventHandler
    public void onRelease(PlayerAnimationEvent event) {
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!chargingPlayers.containsKey(uuid)) return;

        long startTime = chargingPlayers.remove(uuid); // fin du chargement
        long now = System.currentTimeMillis();

        long lastFire = lastFireTimes.getOrDefault(uuid, 0L);
        if (now - lastFire < COOLDOWN_MS) {
            player.sendMessage("§cAttendez avant de tirer à nouveau !");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isTNTLauncher(item)) return;

        // Puissance de tir en fonction du temps (entre 0.3 et 1.0)
        double chargeRatio = Math.min(1.0, (now - startTime) / (double) MAX_CHARGE_MS);
        double power = 0.3 + (0.7 * chargeRatio);

        // Consomme l’XP
        if (!XPUtils.removeXP(player, 30)) {
            player.sendMessage("§cPas assez d’expérience !");
            return;
        }

        lastFireTimes.put(uuid, now); // Anti-spam

        // Crée la TNT
        Location spawnLoc = player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(1.2));
        TNTPrimed tnt = player.getWorld().spawn(spawnLoc, TNTPrimed.class);

        Vector direction = player.getLocation().getDirection().normalize().multiply(power);
        direction.setY(0.2 * power); // un peu vers le haut, en fonction de la puissance
        tnt.setVelocity(direction);
        tnt.setFuseTicks(40); // 2s avant explosion

        // Arrête la TNT si elle ralentit trop
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!tnt.isDead() && tnt.getVelocity().lengthSquared() < 0.01) {
                    tnt.setVelocity(new Vector(0, 0, 0));
                }
            }
        }.runTaskLater(plugin, 10); // vérifie après 0.5s

        // Supprime l'animation (cooldown visuel)
        player.setCooldown(item.getType(), 0);
    }

    // Fonction pour savoir si un item est notre lanceur
    private boolean isTNTLauncher(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "tnt_launcher");

        return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    // Supprime les données en quittant pour éviter les fuites mémoire
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        chargingPlayers.remove(uuid);
        lastFireTimes.remove(uuid);
    }
}
