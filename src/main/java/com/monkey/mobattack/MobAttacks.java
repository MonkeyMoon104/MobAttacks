package com.monkey.mobattack;

import com.monkey.mobattack.commands.manager.CommandRegister;
import com.monkey.mobattack.listener.ShulkerBulletListener;
import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MobAttacks extends JavaPlugin {

    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        cooldownManager = new CooldownManager();

        new CommandRegister(this, cooldownManager).registerAll();

        getServer().getPluginManager().registerEvents(new ShulkerBulletListener(this), this);

    }

    @Override
    public void onDisable() {
    }
}
