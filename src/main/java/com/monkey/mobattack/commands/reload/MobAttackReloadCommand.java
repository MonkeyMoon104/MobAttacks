package com.monkey.mobattack.commands.reload;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

public class MobAttackReloadCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public MobAttackReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
        return true;
    }
}
