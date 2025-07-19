package com.monkey.mobattack.commands.mobs.ravager;

import com.monkey.mobattack.commands.manager.reflection.ReflectionMobCommand;
import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class RavagerCommand extends ReflectionMobCommand {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public RavagerCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    private String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + path, path));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("only-players"));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("mobattack.ravager")) {
            player.sendMessage(getMessage("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.ravager", 6);
        boolean bypass = player.isOp();
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "ravager", cooldown, bypass)) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "ravager");
            player.sendMessage(getMessage("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        Vector launch = player.getLocation().getDirection().multiply(2).setY(0.3);
        player.setVelocity(launch);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1f, 1f);
        player.sendMessage(getMessage("ravager-used"));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double damage = plugin.getConfig().getDouble("damage.ravager", 8.0);
            Location loc = player.getLocation();
            for (Entity entity : player.getWorld().getNearbyEntities(loc, 5, 5, 5)) {
                if (entity instanceof LivingEntity && !entity.equals(player)) {
                    LivingEntity target = (LivingEntity) entity;
                    target.damage(damage, player);
                    target.setVelocity(player.getLocation().getDirection().normalize().multiply(1.5));
                }
            }
        }, 10L);

        return true;
    }
    @Override
    public String getCommandName() {
        return "ravager";
    }
}