package com.firstplugin.commands;

import com.firstplugin.weapons.TNTLauncher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class givetntlauncher implements CommandExecutor {

    private final TNTLauncher launcher;

    public GetTntCommand(TNTLauncher launcher) {
        this.launcher = launcher;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Joueurs uniquement.");
            return true;
        }

        player.getInventory().addItem(launcher.create());
        player.sendMessage("§aTu as reçu un Lance-TNT !");
        return true;
    }
    @Override
    public void onEnable() {
        TNTLauncher launcher = new TNTLauncher(this);
        getCommand("gettnt").setExecutor(new GetTntCommand(launcher));

        getServer().getPluginManager().registerEvents(new BowShootListener(this), this);
    }

}
