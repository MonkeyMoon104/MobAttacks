package com.monkey.mobattack.commands.nomobs.firebreath;

import com.monkey.mobattack.commands.manager.reflection.ReflectionNoMobCommand;
import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class FireBreathCommand extends ReflectionNoMobCommand {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public FireBreathCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    private String msg(String path) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + path, path));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg("only-players"));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("mobattack.firebreath")) {
            player.sendMessage(msg("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.firebreath", 6);
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "firebreath", cooldown, player.isOp())) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "firebreath");
            player.sendMessage(msg("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        double damage = plugin.getConfig().getDouble("damage.firebreath", 5.0);

        for (int i = 1; i <= 10; i++) {
            Location point = start.clone().add(direction.clone().multiply(i * 0.8));
            player.getWorld().spawnParticle(Particle.FLAME, point, 10, 0.2, 0.2, 0.2, 0);
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, point, 4, 0.2, 0.2, 0.2, 0);
            player.getWorld().playSound(point, Sound.ENTITY_BLAZE_SHOOT, 0.2f, 2f);

            for (Entity entity : point.getWorld().getNearbyEntities(point, 1, 1, 1)) {
                if (entity instanceof LivingEntity && !entity.equals(player)) {
                    LivingEntity target = (LivingEntity) entity;
                    target.setFireTicks(60);
                    target.damage(damage, player);
                }
            }
        }

        player.sendMessage(msg("firebreath-used"));
        return true;
    }

    @Override
    public String getCommandName() {
        return "firebreath";
    }
}
