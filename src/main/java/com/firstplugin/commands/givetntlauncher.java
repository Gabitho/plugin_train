package com.firstplugin.commands;

import com.gabitho.plugin_train.armes.ArcExplosif;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetArcCommand implements CommandExecutor {

    private final ArcExplosif arc;

    public GetArcCommand(ArcExplosif arc) {
        this.arc = arc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Commande réservée aux joueurs.");
            return true;
        }

        player.getInventory().addItem(arc.create());
        player.sendMessage("§aTu as reçu l’arc explosif !");
        return true;
    }
}
