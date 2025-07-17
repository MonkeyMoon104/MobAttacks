package com.monkey.mobattack.commands.mobs.evoker;

import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EvokerCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public EvokerCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
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

        if (!player.hasPermission("mobattack.evoker")) {
            player.sendMessage(msg("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.evoker", 10);
        boolean bypass = player.isOp();
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "evoker", cooldown, bypass)) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "evoker");
            player.sendMessage(msg("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        Entity target = player.getTargetEntity(20);
        if (!(target instanceof LivingEntity livingTarget) || target.equals(player)) {
            player.sendMessage(msg("no-valid-target"));
            return true;
        }

        List<Vex> vexes = new ArrayList<>();
        Location baseLoc = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        for (int i = 0; i < 3; i++) {
            Location spawnLoc = baseLoc.clone().add(i - 1, 0, 0);
            Vex vex = (Vex) player.getWorld().spawnEntity(spawnLoc, EntityType.VEX);
            vex.setTarget(livingTarget);
            vex.setPersistent(false);
            vex.setInvulnerable(false);
            vexes.add(vex);
        }

        playEvokerFangsAttack(player, livingTarget);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (livingTarget.isDead()) {
                    for (Vex vex : vexes) {
                        if (!vex.isDead()) vex.remove();
                    }
                    cancel();
                    return;
                }

                boolean allDead = vexes.stream().allMatch(Entity::isDead);
                if (allDead) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);

        player.sendMessage(msg("evoker-used"));
        return true;
    }

    private void playEvokerFangsAttack(Player player, LivingEntity target) {
        Location start = player.getLocation().add(0, 0.1, 0);
        Vector direction = start.getDirection().normalize();
        double damage = plugin.getConfig().getDouble("damage.evoker", 6.0);

        for (int i = 0; i < 5; i++) {
            final int step = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location fangLoc = start.clone().add(direction.clone().multiply(step));
                EvokerFangs fangs = (EvokerFangs) player.getWorld().spawnEntity(fangLoc, EntityType.EVOKER_FANGS);
                fangs.setOwner(player);

                for (Entity entity : fangLoc.getWorld().getNearbyEntities(fangLoc, 1, 1, 1)) {
                    if (entity instanceof LivingEntity hit && !hit.equals(player)) {
                        hit.damage(damage, player);
                    }
                }

                player.getWorld().playSound(fangLoc, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f);
            }, step * 5L);
        }
    }
}