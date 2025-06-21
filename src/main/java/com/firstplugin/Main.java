package com.firstplugin;

import com.firstplugin.commands.GetTntCommand;
import com.firstplugin.listeners.BowShootListener;
import com.firstplugin.weapons.TNTLauncher;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private TNTLauncher launcher;

    @Override
    public void onEnable() {
        launcher = new TNTLauncher(this);

        getCommand("gettnt").setExecutor(new GetTntCommand(launcher));
        getServer().getPluginManager().registerEvents(new BowShootListener(this), this);
    }

    public TNTLauncher getLauncher() {
        return launcher;
    }
}
