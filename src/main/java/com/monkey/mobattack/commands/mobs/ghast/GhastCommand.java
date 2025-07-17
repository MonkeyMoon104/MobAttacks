package com.monkey.mobattack.commands.mobs.ghast;

import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

public class GhastCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public GhastCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
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

        if (!player.hasPermission("mobattack.ghast")) {
            player.sendMessage(getMessage("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.ghast", 7);
        boolean bypass = player.isOp();
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "ghast", cooldown, bypass)) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "ghast");
            player.sendMessage(getMessage("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setIsIncendiary(true);
        fireball.setYield(2f);
        fireball.setShooter(player);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
        player.sendMessage(getMessage("ghast-used"));
        return true;
    }
}
