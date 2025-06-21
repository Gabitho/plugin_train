package com.firstplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class first extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("MonPremierPlugin activé !");
        // Ici, on pourra enregistrer commandes et listeners
        // getCommand("salut").setExecutor(new CommandSalut(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("MonPremierPlugin désactivé !");
    }
}
