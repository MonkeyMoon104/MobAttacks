package com.monkey.mobattack;

import com.monkey.mobattack.commands.mobs.blaze.BlazeCommand;
import com.monkey.mobattack.commands.mobs.creeper.CreeperCommand;
import com.monkey.mobattack.commands.mobs.edragon.EDragonCommand;
import com.monkey.mobattack.commands.mobs.enderman.EndermanCommand;
import com.monkey.mobattack.commands.mobs.evoker.EvokerCommand;
import com.monkey.mobattack.commands.mobs.ghast.GhastCommand;
import com.monkey.mobattack.commands.mobs.ravager.RavagerCommand;
import com.monkey.mobattack.commands.mobs.sgolem.SGolemCommand;
import com.monkey.mobattack.commands.mobs.shulker.ShulkerCommand;
import com.monkey.mobattack.commands.mobs.warden.WardenCommand;
import com.monkey.mobattack.commands.mobs.wither.WitherCommand;
import com.monkey.mobattack.commands.reload.MobAttackReloadCommand;
import com.monkey.mobattack.listener.ShulkerBulletListener;
import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MobAttacks extends JavaPlugin {

    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        cooldownManager = new CooldownManager();

        getCommand("mobattack").setExecutor(new MobAttackReloadCommand(this));
        getCommand("warden").setExecutor(new WardenCommand(this, cooldownManager));
        getCommand("creeper").setExecutor(new CreeperCommand(this, cooldownManager));
        getCommand("ghast").setExecutor(new GhastCommand(this, cooldownManager));
        getCommand("ravager").setExecutor(new RavagerCommand(this, cooldownManager));
        getCommand("shulker").setExecutor(new ShulkerCommand(this, cooldownManager));
        getCommand("blaze").setExecutor(new BlazeCommand(this, cooldownManager));
        getCommand("evoker").setExecutor(new EvokerCommand(this, cooldownManager));
        getCommand("wither").setExecutor(new WitherCommand(this, cooldownManager));
        getCommand("edragon").setExecutor(new EDragonCommand(this, cooldownManager));
        getCommand("snowgolem").setExecutor(new SGolemCommand(this, cooldownManager));
        getCommand("enderman").setExecutor(new EndermanCommand(this, cooldownManager));


        getServer().getPluginManager().registerEvents(new ShulkerBulletListener(this), this);

    }

    @Override
    public void onDisable() {
    }
}
