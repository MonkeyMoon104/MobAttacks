package com.monkey.mobattack.commands.mobs.warden;

import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class WardenCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public WardenCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    private String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + path, path));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getMessage("only-players"));
            return true;
        }

        if (!player.hasPermission("mobattack.warden")) {
            player.sendMessage(getMessage("no-permission"));
            return true;
        }

        boolean bypass = player.isOp();
        int cooldownSec = plugin.getConfig().getInt("cooldowns.warden", 5);
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "warden", cooldownSec, bypass)) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "warden");
            String msg = getMessage("cooldown").replace("%seconds%", String.valueOf(remaining));
            player.sendMessage(msg);
            return true;
        }

        double damage = plugin.getConfig().getDouble("damage.warden", 10.0);
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection().normalize().multiply(1.5);
        Location current = eye.clone();

        for (int i = 0; i < 15; i++) {
            current.add(direction);
            player.getWorld().spawnParticle(Particle.SONIC_BOOM, current, 1);

            for (Entity entity : current.getWorld().getNearbyEntities(current, 1.5, 1.5, 1.5)) {
                if (entity instanceof LivingEntity target && !target.equals(player)) {
                    target.damage(damage, player);
                    target.setVelocity(direction.clone().multiply(0.5));
                    player.getWorld().playSound(target.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1f);
                    player.sendMessage(getMessage("warden-used"));
                    return true;
                }
            }
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1f);
        player.sendMessage(getMessage("warden-used"));
        return true;
    }
}
