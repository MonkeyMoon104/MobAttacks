package com.monkey.mobattack.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ShulkerBulletListener implements Listener {
    private final JavaPlugin plugin;

    public ShulkerBulletListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBulletHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof ShulkerBullet)) return;
        ShulkerBullet bullet = (ShulkerBullet) event.getEntity();
        if (!(bullet.getShooter() instanceof Player)) return;
        Player shooter = (Player) bullet.getShooter();

        if (!bullet.hasMetadata("mobattack_shulker")) return;

        double damage = plugin.getConfig().getDouble("damage.shulker", 4.0);

        if (event.getHitEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getHitEntity();
            target.damage(damage, shooter);
            shooter.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.shulker-hit", "&aHai colpito correttamente l'entità con il proiettile Shulker!")));
        }
    }
}
