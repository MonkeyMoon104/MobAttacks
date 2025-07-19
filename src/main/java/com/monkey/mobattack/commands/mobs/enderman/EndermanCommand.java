package com.monkey.mobattack.commands.mobs.enderman;

import com.monkey.mobattack.commands.manager.reflection.ReflectionMobCommand;
import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class EndermanCommand extends ReflectionMobCommand {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public EndermanCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
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

        if (!player.hasPermission("mobattack.enderman")) {
            player.sendMessage(msg("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.enderman", 5);
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "enderman", cooldown, player.isOp())) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "enderman");
            player.sendMessage(msg("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        Entity target = player.getTargetEntity(10);
        if (target instanceof LivingEntity && !target.equals(player)) {
            LivingEntity living = (LivingEntity) target;
            Location targetLoc = target.getLocation();
            Vector backward = targetLoc.getDirection().normalize().multiply(-1);
            Location behind = targetLoc.add(backward);
            behind.setY(targetLoc.getY());

            player.teleport(behind);
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 0.5, 0.5, 0.5);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

            double damage = plugin.getConfig().getDouble("damage.enderman", 6.0);

            if (living instanceof Player) {
                Player targetPlayer = (Player) living;
                if (!targetPlayer.isInvulnerable() && targetPlayer.getGameMode() == GameMode.SURVIVAL) {
                    targetPlayer.playEffect(EntityEffect.HURT);
                    targetPlayer.setHealth(Math.max(0, targetPlayer.getHealth() - damage));
                    targetPlayer.getWorld().playSound(targetPlayer.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
                }
            } else {
                living.damage(damage, player);
            }

            player.sendMessage(msg("enderman-used"));
        } else {
            player.sendMessage(msg("no-valid-target"));
        }

        return true;
    }

    @Override
    public String getCommandName() {
        return "enderman";
    }
}
