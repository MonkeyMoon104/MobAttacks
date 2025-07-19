package com.monkey.mobattack.commands.mobs.wither;

import com.monkey.mobattack.commands.manager.reflection.ReflectionMobCommand;
import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

public class WitherCommand extends ReflectionMobCommand {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public WitherCommand(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    private String msg(String key) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + key, key));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg("only-players"));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("mobattack.wither")) {
            player.sendMessage(msg("no-permission"));
            return true;
        }

        int cooldown = plugin.getConfig().getInt("cooldowns.wither", 7);
        if (cooldownManager.isOnCooldown(player.getUniqueId(), "wither", cooldown, player.isOp())) {
            long remaining = cooldownManager.getRemainingTime(player.getUniqueId(), "wither");
            player.sendMessage(msg("cooldown").replace("%seconds%", String.valueOf(remaining)));
            return true;
        }

        WitherSkull skull = (WitherSkull) player.launchProjectile(WitherSkull.class);
        skull.setShooter(player);
        skull.setYield((float) plugin.getConfig().getDouble("damage.wither", 8.0) / 2);
        skull.setIsIncendiary(false);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 1f);
        player.sendMessage(msg("wither-used"));
        return true;
    }

    @Override
    public String getCommandName() {
        return "wither";
    }
}
