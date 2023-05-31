package me.gethertv.szaman.cmd;

import me.gethertv.szaman.Szaman;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SzamanCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Szaman.getInstance().getUserData().get(player.getUniqueId()).openInv();
        return false;
    }
}
