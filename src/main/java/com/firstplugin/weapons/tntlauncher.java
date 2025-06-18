package com.firstplugin.weapons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ArcExplosif {

    private final JavaPlugin plugin;

    public ArcExplosif(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack create() {
        ItemStack arc = new ItemStack(Material.BOW);
        ItemMeta meta = arc.getItemMeta();

        if (meta == null) return arc; // Sécurité

        meta.setDisplayName("§5Arc Explosif");

        // Tag secret (persistent)
        NamespacedKey key = new NamespacedKey(plugin, "arc_explosif");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.BYTE, (byte) 1);

        arc.setItemMeta(meta);
        return arc;
    }
}
