package com.monkey.mobattack.commands.mobs.shulker;

import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class ShulkerCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public ShulkerCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    private String msg(String path) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + path, path));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(msg("only-players"));
            return true;
        }

        if (!player.hasPermission("mobattack.shulker")) {
            player.sendMessage(msg("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.shulker", 8);
        boolean bypass = player.isOp();
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "shulker", cooldown, bypass)) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "shulker");
            player.sendMessage(msg("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        Entity target = player.getTargetEntity(20);
        if (target == null || target.equals(player)) {
            player.sendMessage(msg("no-valid-target"));
            return true;
        }

        Location loc = player.getEyeLocation().add(player.getLocation().getDirection());
        ShulkerBullet bullet = (ShulkerBullet) player.getWorld().spawnEntity(loc, EntityType.SHULKER_BULLET);
        bullet.setShooter(player);
        bullet.setTarget(target);

        bullet.setMetadata("mobattack_shulker", new FixedMetadataValue(plugin, player.getUniqueId().toString()));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 1f, 1f);

        return true;
    }
}
