package com.monkey.mobattack.commands.mobs.sgolem;

import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

public class SGolemCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public SGolemCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    private String msg(String key) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + key, key));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(msg("only-players"));
            return true;
        }

        if (!player.hasPermission("mobattack.snowgolem")) {
            player.sendMessage(msg("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.snowgolem", 2);
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "snowgolem", cooldown, player.isOp())) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "snowgolem");
            player.sendMessage(msg("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setShooter(player);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOW_GOLEM_SHOOT, 1f, 1f);
        player.sendMessage(msg("snowgolem-used"));
        return true;
    }
}
