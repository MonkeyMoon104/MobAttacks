package com.monkey.mobattack.commands.mobs.creeper;

import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

public class CreeperCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public CreeperCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    private String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + path, path));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getMessage("only-players"));
            return true;
        }

        if (!player.hasPermission("mobattack.creeper")) {
            player.sendMessage(getMessage("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.creeper", 5);
        boolean bypass = player.isOp();

        if (cooldownManager.isOnCooldown(player.getUniqueId(), "creeper", cooldown, bypass)) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "creeper");
            player.sendMessage(getMessage("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        double damage = plugin.getConfig().getDouble("damage.creeper", 20.0);
        player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
        player.getWorld().createExplosion(player.getLocation(), (float) damage / 5f, false, true);
        player.sendMessage(getMessage("creeper-used"));
        return true;
    }
}
