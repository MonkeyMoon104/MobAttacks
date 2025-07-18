package com.monkey.mobattack.commands.mobs.enderdragon;

import com.monkey.mobattack.commands.manager.reflection.ReflectionMobCommand;
import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderDragonCommand extends ReflectionMobCommand {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public EnderDragonCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
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

        if (!player.hasPermission("mobattack.edragon")) {
            player.sendMessage(msg("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.edragon", 10);
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "edragon", cooldown, player.isOp())) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "edragon");
            player.sendMessage(msg("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        double damage = plugin.getConfig().getDouble("damage.edragon", 6.0);
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 80, 1, 1, 1, 0.05);
        player.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 2f, 1f);

        for (Entity e : player.getWorld().getNearbyEntities(loc, 3, 3, 3)) {
            if (e instanceof LivingEntity && !e.equals(player)) {
                LivingEntity target = (LivingEntity) e;
                target.damage(damage, player);
            }
        }

        player.sendMessage(msg("edragon-used"));
        return true;
    }

    @Override
    public String getCommandName() {
        return "edragon";
    }
}
