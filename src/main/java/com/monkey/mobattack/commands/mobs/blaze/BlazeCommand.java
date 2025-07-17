package com.monkey.mobattack.commands.mobs.blaze;

import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BlazeCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public BlazeCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
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

        if (!player.hasPermission("mobattack.blaze")) {
            player.sendMessage(getMessage("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.blaze", 5);
        boolean bypass = player.isOp();
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "blaze", cooldown, bypass)) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "blaze");
            player.sendMessage(getMessage("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        double damage = plugin.getConfig().getDouble("damage.blaze", 5.0);
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();

        for (int i = -1; i <= 1; i++) {
            SmallFireball fireball = player.getWorld().spawn(loc, SmallFireball.class);
            fireball.setShooter(player);
            fireball.setIsIncendiary(true);
            fireball.setVelocity(dir.clone().add(new Vector(i * 0.1, 0, i * 0.1)));
        }


        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
        player.sendMessage(getMessage("blaze-used"));
        return true;
    }
}
