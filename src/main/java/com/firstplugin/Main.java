package com.firstplugin;

import com.firstplugin.commands.gettntlauncher;
import com.firstplugin.listeners.tntshoot;
import com.firstplugin.weapons.TNTLauncher;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private TNTLauncher launcher;

    @Override
    public void onEnable() {
        launcher = new TNTLauncher(this);

        getCommand("gettnt").setExecutor(new gettntlauncher(launcher));
        getServer().getPluginManager().registerEvents(new tntshoot(this), this);
    }

    public TNTLauncher getLauncher() {
        return launcher;
    }
}
