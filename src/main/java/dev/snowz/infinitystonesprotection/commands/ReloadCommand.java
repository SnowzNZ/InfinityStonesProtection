package dev.snowz.infinitystonesprotection.commands;

import dev.snowz.infinitystonesprotection.InfinityStonesProtection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        InfinityStonesProtection.getPlugin().reloadConfig();
        sender.sendMessage("§aConfig reloaded!");
        return true;
    }
}
